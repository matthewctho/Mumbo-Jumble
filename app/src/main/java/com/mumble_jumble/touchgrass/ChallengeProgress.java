package com.mumble_jumble.touchgrass;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mumble_jumble.touchgrass.activity.MutualConnectActivity;
import com.mumble_jumble.touchgrass.data.AuthService;
import com.mumble_jumble.touchgrass.data.FirestoreService;
import com.mumble_jumble.touchgrass.data.GeminiVerificationService;
import com.mumble_jumble.touchgrass.models.Task;
import com.mumble_jumble.touchgrass.models.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The real backend behind your teammate's "ongoing challenge" design
 * (activity_challenge_progress.xml). Shows a pack's real 4 tasks from
 * Firestore, the user's real per-task completion state, and lets them
 * submit a photo (Gemini-verified, same pipeline as TaskListActivity) or
 * open the mutual-connect QR flow, right from this screen.
 */
public class ChallengeProgress extends AppCompatActivity {

    private static final String TAG = "ChallengeProgress";

    private final AuthService authService = new AuthService();
    private final FirestoreService firestoreService = new FirestoreService();
    private final GeminiVerificationService verificationService = new GeminiVerificationService();

    private String packId;
    private final List<Task> tasks = new ArrayList<>();
    private final List<String> tasksCompleted = new ArrayList<>();

    // The task currently being submitted for, set right before the camera
    // launches so we know which task the resulting photo belongs to.
    private Task pendingTask;
    private boolean isSubmitting = false;

    private TextView usernameText;
    private TextView pointsText;
    private TextView challengeTitle;
    private TextView challengeDescription;
    private TextView progressPercentage;
    private ProgressBar overallProgress;
    private TextView progressTaskCount;
    private TextView ongoingStatus;

    private CardView[] taskCards;
    private TextView[] taskTitles;
    private TextView[] taskDescriptions;
    private TextView[] taskStatuses;
    private CardView[] taskCameraButtons;

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

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_challenge_progress);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        packId = getIntent().getStringExtra("packId");
        String packName = getIntent().getStringExtra("packName");
        String packDescription = getIntent().getStringExtra("packDescription");

        // ==============================
        // FIND VIEWS
        // ==============================

        usernameText = findViewById(R.id.usernameText);
        pointsText = findViewById(R.id.pointsText);
        challengeTitle = findViewById(R.id.challengeTitle);
        challengeDescription = findViewById(R.id.challengeDescription);
        progressPercentage = findViewById(R.id.progressPercentage);
        overallProgress = findViewById(R.id.overallProgress);
        progressTaskCount = findViewById(R.id.progressTaskCount);
        ongoingStatus = findViewById(R.id.ongoingStatus);

        taskCards = new CardView[]{
                findViewById(R.id.taskCard1), findViewById(R.id.taskCard2),
                findViewById(R.id.taskCard3), findViewById(R.id.taskCard4)
        };
        taskTitles = new TextView[]{
                findViewById(R.id.taskTitle1), findViewById(R.id.taskTitle2),
                findViewById(R.id.taskTitle3), findViewById(R.id.taskTitle4)
        };
        taskDescriptions = new TextView[]{
                findViewById(R.id.taskDescription1), findViewById(R.id.taskDescription2),
                findViewById(R.id.taskDescription3), findViewById(R.id.taskDescription4)
        };
        taskStatuses = new TextView[]{
                findViewById(R.id.taskStatus1), findViewById(R.id.taskStatus2),
                findViewById(R.id.taskStatus3), findViewById(R.id.taskStatus4)
        };
        taskCameraButtons = new CardView[]{
                findViewById(R.id.taskCamera1), findViewById(R.id.taskCamera2),
                findViewById(R.id.taskCamera3), findViewById(R.id.taskCamera4)
        };

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Show what we already know immediately; real data fills in as it loads.
        if (packName != null) {
            challengeTitle.setText(packName);
        }
        if (packDescription != null) {
            challengeDescription.setText(packDescription);
        }

        findViewById(R.id.mainCameraButton).setOnClickListener(v -> openCameraForNextIncompleteTask());

        loadEverything();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh whenever we return here (e.g. after a mutual-connect scan
        // or a photo submission finishes), so completed tasks show up live.
        if (packId != null) {
            loadEverything();
        }
    }

    private void loadEverything() {
        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser == null || packId == null) {
            return;
        }
        String uid = currentUser.getUid();

        firestoreService.getUserProfile(uid, new FirestoreService.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                usernameText.setText(user.displayName);
                pointsText.setText(user.points + " points");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to load user profile", e);
            }
        });

        loadTasksFromFirestore(uid);
    }

    private void loadTasksFromFirestore(String uid) {
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
                    loadProgress(uid);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading tasks", e));
    }

    private void loadProgress(String uid) {
        firestoreService.getProgress(uid, packId, new FirestoreService.ProgressCallback() {
            @Override
            public void onSuccess(com.mumble_jumble.touchgrass.models.ChallengeProgress progress) {
                tasksCompleted.clear();
                if (progress != null && progress.tasksCompleted != null) {
                    tasksCompleted.addAll(progress.tasksCompleted);
                }
                renderTasks();
            }

            @Override
            public void onFailure(Exception e) {
                // No progress doc yet (user hasn't started this pack) — render as all-incomplete.
                tasksCompleted.clear();
                renderTasks();
            }
        });
    }

    private void renderTasks() {
        int slots = Math.min(tasks.size(), taskCards.length);

        for (int i = 0; i < taskCards.length; i++) {
            boolean hasTask = i < slots;
            taskCards[i].setVisibility(hasTask ? android.view.View.VISIBLE : android.view.View.GONE);
            if (!hasTask) {
                continue;
            }

            Task task = tasks.get(i);
            boolean completed = tasksCompleted.contains(task.taskId);

            taskTitles[i].setText(task.name);
            taskDescriptions[i].setText(task.description != null ? task.description
                    : "+" + task.pointValue + " points");
            taskStatuses[i].setText(completed ? "COMPLETED" : "NOT STARTED");

            taskCameraButtons[i].setOnClickListener(v -> onTaskTapped(task));
            taskCards[i].setOnClickListener(v -> onTaskTapped(task));
        }

        int completedCount = 0;
        for (Task task : tasks) {
            if (tasksCompleted.contains(task.taskId)) {
                completedCount++;
            }
        }

        int totalTasks = tasks.size();
        int percentage = totalTasks > 0 ? (completedCount * 100) / totalTasks : 0;
        progressPercentage.setText(percentage + "%");
        overallProgress.setMax(100);
        overallProgress.setProgress(percentage);
        progressTaskCount.setText(completedCount + " of " + totalTasks + " tasks completed");
        ongoingStatus.setText(completedCount >= totalTasks && totalTasks > 0 ? "●  COMPLETE" : "●  ONGOING");
    }

    private void onTaskTapped(Task task) {
        if (isSubmitting) {
            Toast.makeText(this, "Still processing your last submission…", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("mutual_connect".equals(task.type)) {
            Intent intent = new Intent(this, MutualConnectActivity.class);
            intent.putExtra("packId", packId);
            intent.putExtra("taskId", task.taskId);
            intent.putExtra("taskPointValue", task.pointValue);
            intent.putExtra("totalTasksInPack", tasks.size());
            startActivity(intent);
            return;
        }

        pendingTask = task;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(null);
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCameraForNextIncompleteTask() {
        for (Task task : tasks) {
            if (!tasksCompleted.contains(task.taskId)) {
                onTaskTapped(task);
                return;
            }
        }
        Toast.makeText(this, "You've completed every task in this challenge!", Toast.LENGTH_SHORT).show();
    }

    private void submitPhotoForTask(Task task, Bitmap photo) {
        String uid = authService.getCurrentUser() != null ? authService.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "You need to be signed in to submit a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        isSubmitting = true;
        Toast.makeText(this, "Verifying photo…", Toast.LENGTH_SHORT).show();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] imageBytes = stream.toByteArray();

        verificationService.verifyPhoto(imageBytes, task.name, task.description, task.type,
                new GeminiVerificationService.VerificationCallback() {
                    @Override
                    public void onResult(boolean approved, String reason) {
                        if (approved) {
                            awardPoints(uid, task);
                        } else {
                            isSubmitting = false;
                            Toast.makeText(ChallengeProgress.this,
                                    "Not verified: " + reason, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Gemini verification failed", e);
                        isSubmitting = false;
                        Toast.makeText(ChallengeProgress.this,
                                "Verification unavailable right now — try again in a moment",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void awardPoints(String uid, Task task) {
        firestoreService.completeTask(uid, packId, task.taskId, task.pointValue, tasks.size(),
                new FirestoreService.WriteCallback() {
                    @Override
                    public void onSuccess() {
                        isSubmitting = false;
                        Toast.makeText(ChallengeProgress.this,
                                "Verified! +" + task.pointValue + " points", Toast.LENGTH_LONG).show();
                        loadEverything();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed to award points", e);
                        isSubmitting = false;
                        Toast.makeText(ChallengeProgress.this,
                                "Verified, but couldn't save points — try again", Toast.LENGTH_LONG).show();
                    }
                });
    }
}