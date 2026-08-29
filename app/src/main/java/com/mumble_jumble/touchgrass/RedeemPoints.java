package com.mumble_jumble.touchgrass;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.widget.TextView;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class RedeemPoints extends AppCompatActivity {

    // ==============================
    // USER POINTS
    // ==============================

    // Change this later when you connect
    // your points to a database/backend.
    private int currentPoints = 1400;

    // Points required for the next reward.
    private int nextRewardPoints = 1500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect this Activity to your XML layout
        setContentView(R.layout.activity_redeem_points);


        // ==============================
        // FIND VIEWS
        // ==============================

        MaterialToolbar toolbar =
                findViewById(R.id.pointsToolbar);

        TextView totalPointsText =
                findViewById(R.id.totalPointsText);

        TextView nextRewardLabel =
                findViewById(R.id.nextRewardLabel);

        TextView progressPercentage =
                findViewById(R.id.rewardProgressText);

        CircularProgressIndicator rewardProgressWheel =
                findViewById(R.id.rewardProgressWheel);

        LinearProgressIndicator pointsProgress =
                findViewById(R.id.pointsProgress);


        // ==============================
        // BACK BUTTON
        // ==============================

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });


        // ==============================
        // DISPLAY TOTAL POINTS
        // ==============================

        totalPointsText.setText(
                String.format("%,d", currentPoints)
        );


        // ==============================
        // CALCULATE POINTS REMAINING
        // ==============================

        int pointsRemaining =
                nextRewardPoints - currentPoints;


        // ==============================
        // UPDATE "POINTS UNTIL REWARD"
        // ==============================

        if (pointsRemaining > 0) {

            nextRewardLabel.setText(
                    pointsRemaining +
                            " pts until your next reward"
            );

        } else {

            nextRewardLabel.setText(
                    "Reward unlocked! 🎉"
            );
        }


        // ==============================
        // SET UP HORIZONTAL PROGRESS BAR
        // ==============================

        pointsProgress.setMax(nextRewardPoints);

        pointsProgress.setProgressCompat(
                0,
                false
        );


        // ==============================
        // SET UP CIRCULAR PROGRESS WHEEL
        // ==============================

        rewardProgressWheel.setMax(
                nextRewardPoints
        );

        rewardProgressWheel.setProgressCompat(
                0,
                false
        );


        // ==============================
        // ANIMATE BOTH PROGRESS BARS
        // ==============================

        animateRewardProgress(
                rewardProgressWheel,
                pointsProgress,
                progressPercentage,
                currentPoints,
                nextRewardPoints
        );
    }


    // =========================================================
    // ANIMATE REWARD PROGRESS
    // =========================================================

    private void animateRewardProgress(
            CircularProgressIndicator progressWheel,
            LinearProgressIndicator progressBar,
            TextView percentageText,
            int currentPoints,
            int nextRewardPoints
    ) {

        // Make sure we don't divide by zero
        if (nextRewardPoints <= 0) {
            return;
        }


        // Don't allow progress to go above the reward amount
        int safePoints =
                Math.min(
                        currentPoints,
                        nextRewardPoints
                );


        // Start animation from 0 points
        // and animate to the user's current points.
        ValueAnimator animator =
                ValueAnimator.ofInt(
                        0,
                        safePoints
                );


        // Animation duration
        animator.setDuration(1500);


        // Makes the animation start quickly
        // and slow down smoothly.
        animator.setInterpolator(
                new DecelerateInterpolator()
        );


        // ==============================
        // ANIMATION UPDATE
        // ==============================

        animator.addUpdateListener(animation -> {

            // Get the current animated value
            int animatedPoints =
                    (int) animation.getAnimatedValue();


            // ---------------------------------
            // Update circular progress wheel
            // ---------------------------------

            progressWheel.setProgressCompat(
                    animatedPoints,
                    false
            );


            // ---------------------------------
            // Update horizontal progress bar
            // ---------------------------------

            progressBar.setProgressCompat(
                    animatedPoints,
                    false
            );


            // ---------------------------------
            // Calculate percentage
            // ---------------------------------

            int percentage =
                    (int) (
                            (animatedPoints /
                                    (float) nextRewardPoints)
                                    * 100
                    );


            // Make sure percentage doesn't
            // exceed 100%.
            percentage =
                    Math.min(
                            percentage,
                            100
                    );


            // ---------------------------------
            // Update percentage text
            // ---------------------------------

            percentageText.setText(
                    percentage + "%"
            );
        });


        // Start animation
        animator.start();
    }
}