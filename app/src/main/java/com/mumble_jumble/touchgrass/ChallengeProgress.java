package com.mumble_jumble.touchgrass;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChallengeProgress extends AppCompatActivity {

    // Camera request codes
    private static final int CAMERA_TASK_1 = 101;
    private static final int CAMERA_TASK_2 = 102;
    private static final int CAMERA_TASK_3 = 103;
    private static final int CAMERA_TASK_4 = 104;

    // Task completion
    private boolean task1Completed = false;
    private boolean task2Completed = false;
    private boolean task3Completed = false;
    private boolean task4Completed = false;

    // UI
    private ProgressBar overallProgress;
    private TextView progressPercentage;
    private TextView progressTaskCount;

    private CardView taskCard1;
    private CardView taskCard2;
    private CardView taskCard3;
    private CardView taskCard4;

    private TextView taskStatus1;
    private TextView taskStatus2;
    private TextView taskStatus3;
    private TextView taskStatus4;

    private int currentTask = 0;

    // Your XML colour palette
    private static final int GREEN_CARD = 0xFF34472B;
    private static final int GREEN_NUMBER = 0xFF506542;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_challenge_progress);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // Find UI elements
        overallProgress = findViewById(R.id.overallProgress);
        progressPercentage = findViewById(R.id.progressPercentage);
        progressTaskCount = findViewById(R.id.progressTaskCount);

        taskCard1 = findViewById(R.id.taskCard1);
        taskCard2 = findViewById(R.id.taskCard2);
        taskCard3 = findViewById(R.id.taskCard3);
        taskCard4 = findViewById(R.id.taskCard4);

        taskStatus1 = findViewById(R.id.taskStatus1);
        taskStatus2 = findViewById(R.id.taskStatus2);
        taskStatus3 = findViewById(R.id.taskStatus3);
        taskStatus4 = findViewById(R.id.taskStatus4);

        // Camera buttons
        CardView camera1 = findViewById(R.id.taskCamera1);
        CardView camera2 = findViewById(R.id.taskCamera2);
        CardView camera3 = findViewById(R.id.taskCamera3);
        CardView camera4 = findViewById(R.id.taskCamera4);

        // Make all camera buttons clickable
        camera1.setOnClickListener(v -> openCamera(CAMERA_TASK_1));
        camera2.setOnClickListener(v -> openCamera(CAMERA_TASK_2));
        camera3.setOnClickListener(v -> openCamera(CAMERA_TASK_3));
        camera4.setOnClickListener(v -> openCamera(CAMERA_TASK_4));

        // Optional: make entire task cards clickable too
        taskCard1.setOnClickListener(v -> openCamera(CAMERA_TASK_1));
        taskCard2.setOnClickListener(v -> openCamera(CAMERA_TASK_2));
        taskCard3.setOnClickListener(v -> openCamera(CAMERA_TASK_3));
        taskCard4.setOnClickListener(v -> openCamera(CAMERA_TASK_4));

        // Back button
        TextView backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        // Initial progress
        updateProgress(false);
    }


    /**
     * Opens the phone's camera application.
     */
    private void openCamera(int taskNumber) {

        currentTask = taskNumber;

        Intent cameraIntent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check whether camera app exists
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(cameraIntent, taskNumber);

        } else {

            Toast.makeText(
                    this,
                    "No camera application found.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    /**
     * Receives the photo after the camera is closed.
     */
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (resultCode == RESULT_OK && data != null) {

            Bitmap photo =
                    (Bitmap) data.getExtras().get("data");

            if (photo != null) {

                // Save picture locally
                savePictureLocally(photo, requestCode);

                // Complete task
                completeTask(requestCode);
            }

        } else {

            Toast.makeText(
                    this,
                    "Picture was not taken.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    /**
     * Saves the camera image inside the app's private storage.
     */
    private void savePictureLocally(
            Bitmap bitmap,
            int taskNumber) {

        String fileName =
                "challenge_task_" + taskNumber + ".jpg";

        File directory = new File(
                getFilesDir(),
                "challenge_photos"
        );

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File imageFile =
                new File(directory, fileName);

        try {

            FileOutputStream outputStream =
                    new FileOutputStream(imageFile);

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    outputStream
            );

            outputStream.flush();
            outputStream.close();

            Toast.makeText(
                    this,
                    "Photo saved successfully.",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (IOException e) {

            Toast.makeText(
                    this,
                    "Could not save photo.",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();
        }
    }


    /**
     * Marks the selected task as completed.
     */
    private void completeTask(int taskNumber) {

        switch (taskNumber) {

            case CAMERA_TASK_1:

                if (!task1Completed) {

                    task1Completed = true;

                    taskStatus1.setText("✓ COMPLETED");

                    makeTaskGreen(taskCard1);
                }

                break;


            case CAMERA_TASK_2:

                if (!task2Completed) {

                    task2Completed = true;

                    taskStatus2.setText("✓ COMPLETED");

                    makeTaskGreen(taskCard2);
                }

                break;


            case CAMERA_TASK_3:

                if (!task3Completed) {

                    task3Completed = true;

                    taskStatus3.setText("✓ COMPLETED");

                    makeTaskGreen(taskCard3);
                }

                break;


            case CAMERA_TASK_4:

                if (!task4Completed) {

                    task4Completed = true;

                    taskStatus4.setText("✓ COMPLETED");

                    makeTaskGreen(taskCard4);
                }

                break;
        }

        // Update progress after task completion
        updateProgress(true);
    }


    /**
     * Changes a completed task to the green colour
     * from your existing XML palette.
     */
    private void makeTaskGreen(CardView card) {

        // Main task card
        card.setCardBackgroundColor(GREEN_CARD);

        // Find the number TextView inside the card
        TextView numberView = null;

        if (card.getId() == R.id.taskCard1) {

            numberView = findViewById(R.id.taskNumber1);

        } else if (card.getId() == R.id.taskCard2) {

            numberView = findViewById(R.id.taskNumber2);

        } else if (card.getId() == R.id.taskCard3) {

            numberView = findViewById(R.id.taskNumber3);

        } else if (card.getId() == R.id.taskCard4) {

            numberView = findViewById(R.id.taskNumber4);
        }

        if (numberView != null) {
            numberView.setBackgroundColor(GREEN_NUMBER);
        }
    }


    /**
     * Calculates and updates overall challenge progress.
     */
    private void updateProgress(boolean animate) {

        int completedTasks = 0;

        if (task1Completed) {
            completedTasks++;
        }

        if (task2Completed) {
            completedTasks++;
        }

        if (task3Completed) {
            completedTasks++;
        }

        if (task4Completed) {
            completedTasks++;
        }

        // There are 4 tasks
        int percentage =
                (completedTasks * 100) / 4;

        // Update text
        progressPercentage.setText(
                percentage + "%"
        );

        progressTaskCount.setText(
                completedTasks + " of 4 tasks completed"
        );

        // Animate progress bar
        if (animate) {

            int oldProgress =
                    overallProgress.getProgress();

            android.animation.ObjectAnimator animator =
                    android.animation.ObjectAnimator.ofInt(
                            overallProgress,
                            "progress",
                            oldProgress,
                            percentage
                    );

            animator.setDuration(700);

            animator.setInterpolator(
                    new android.view.animation.DecelerateInterpolator()
            );

            animator.start();

        } else {

            overallProgress.setProgress(
                    percentage
            );
        }
    }
}