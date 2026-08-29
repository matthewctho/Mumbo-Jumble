package com.mumble_jumble.touchgrass.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mumble_jumble.touchgrass.R;
import com.mumble_jumble.touchgrass.adapters.TaskAdapter;
import com.mumble_jumble.touchgrass.data.AuthService;
import com.mumble_jumble.touchgrass.data.FirestoreService;
import com.mumble_jumble.touchgrass.data.GeminiVerificationService;
import com.mumble_jumble.touchgrass.data.StorageService;
import com.mumble_jumble.touchgrass.models.Submission;
import com.mumble_jumble.touchgrass.models.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    private static final String TAG = "TaskListActivity";

    private TaskAdapter adapter;
    private List<Task> tasks = new ArrayList<>();

    private String packId;

    // The task currently being submitted for, set right before we launch the camera
    // so we know which task the resulting photo belongs to when the camera returns.
    private Task pendingTask;
    private boolean isSubmitting = false;

    private final AuthService authService = new AuthService();
    private final StorageService storageService = new StorageService();
    private final FirestoreService firestoreService = new FirestoreService();
    private final GeminiVerificationService verificationService = new GeminiVerificationService();

    // Registers the camera launcher. TakePicturePreview returns a small preview
    // Bitmap directly — no FileProvider/manifest setup needed, good enough for
    // both Gemini's input and Storage upload at hackathon scope.
    private final ActivityResultLauncher<Void> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap == null) {
                    Toast.makeText(this, "Photo cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pendingTask != null) {
                    submitPhotoForTask(pendingTask, bitmap);
                }
            });

    // Requests the CAMERA runtime permission if it hasn't been granted yet.
    // Declaring it in the manifest only lets the user grant it — the app
    // still has to ask at the moment it's needed, via this system dialog.
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    cameraLauncher.launch(null);
                } else {
                    Toast.makeText(this, "Camera permission is needed to submit a photo", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        packId = getIntent().getStringExtra("packId");
        String packName = getIntent().getStringExtra("packName");
        if (packName != null) {
            setTitle(packName);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(tasks, this::onTaskClicked);
        recyclerView.setAdapter(adapter);

        loadTasksFromFirestore(packId);
    }

    private void onTaskClicked(Task task) {
        if (isSubmitting) {
            Toast.makeText(this, "Still processing your last submission…", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("scenery_photo".equals(task.type)) {
            pendingTask = task;

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(null);
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }

        } else {
            // Mutual connect flow (QR generate/scan) isn't wired up on this screen yet.
            Toast.makeText(this, "Mutual connect flow coming soon", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitPhotoForTask(Task task, Bitmap photo) {
        String uid = authService.getCurrentUser() != null ? authService.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "You need to be signed in to submit a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        isSubmitting = true;
        Toast.makeText(this, "Uploading photo…", Toast.LENGTH_SHORT).show();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] imageBytes = stream.toByteArray();

        storageService.uploadSubmissionPhoto(uid, task.taskId, imageBytes, new StorageService.UploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                Submission submission = new Submission(uid, task.taskId, packId, downloadUrl);
                firestoreService.createSubmission(submission, new FirestoreService.SubmissionCreatedCallback() {
                    @Override
                    public void onSuccess(String submissionDocId) {
                        verifyAndFinish(uid, task, submissionDocId, imageBytes);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed to create submission doc", e);
                        isSubmitting = false;
                        Toast.makeText(TaskListActivity.this, "Couldn't save submission — try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Photo upload failed", e);
                isSubmitting = false;
                Toast.makeText(TaskListActivity.this, "Upload failed — check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyAndFinish(String uid, Task task, String submissionDocId, byte[] imageBytes) {
        Toast.makeText(this, "Verifying photo…", Toast.LENGTH_SHORT).show();

        verificationService.verifyPhoto(imageBytes, task.name, task.description, task.type,
                new GeminiVerificationService.VerificationCallback() {
                    @Override
                    public void onResult(boolean approved, String reason) {
                        String status = approved ? "verified" : "rejected";
                        firestoreService.updateSubmissionStatus(submissionDocId, status, approved,
                                new FirestoreService.WriteCallback() {
                                    @Override
                                    public void onSuccess() {
                                        if (approved) {
                                            awardPoints(uid, task);
                                        } else {
                                            isSubmitting = false;
                                            Toast.makeText(TaskListActivity.this,
                                                    "Not verified: " + reason, Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e(TAG, "Failed to update submission status", e);
                                        isSubmitting = false;
                                    }
                                });
                    }

                    @Override
                    public void onError(Exception e) {
                        // AI verification unavailable — fall back to an honest "pending" state
                        // rather than silently rejecting or faking a result.
                        Log.e(TAG, "Gemini verification failed", e);
                        firestoreService.updateSubmissionStatus(submissionDocId, "pending", null,
                                new FirestoreService.WriteCallback() {
                                    @Override
                                    public void onSuccess() {
                                        isSubmitting = false;
                                        Toast.makeText(TaskListActivity.this,
                                                "Uploaded! Verification is unavailable right now — pending manual review",
                                                Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(Exception e2) {
                                        Log.e(TAG, "Failed to mark submission pending", e2);
                                        isSubmitting = false;
                                    }
                                });
                    }
                });
    }

    private void awardPoints(String uid, Task task) {
        firestoreService.completeTask(uid, packId, task.taskId, task.pointValue, tasks.size(),
                new FirestoreService.WriteCallback() {
                    @Override
                    public void onSuccess() {
                        isSubmitting = false;
                        Toast.makeText(TaskListActivity.this,
                                "Verified! +" + task.pointValue + " points", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed to award points", e);
                        isSubmitting = false;
                        Toast.makeText(TaskListActivity.this,
                                "Verified, but couldn't save points — try again", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadTasksFromFirestore(String packId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tasks")
                .whereEqualTo("packId", packId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    tasks.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Task task = doc.toObject(Task.class);
                        task.taskId = doc.getId();
                        task.packId = packId;
                        tasks.add(task);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading tasks", e);
                });
    }
}