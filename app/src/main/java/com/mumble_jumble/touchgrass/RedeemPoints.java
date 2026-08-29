package com.mumble_jumble.touchgrass;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.DecelerateInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.mumble_jumble.touchgrass.data.AuthService;
import com.mumble_jumble.touchgrass.data.FirestoreService;
import com.mumble_jumble.touchgrass.models.User;

public class RedeemPoints extends AppCompatActivity {

    private final AuthService authService = new AuthService();
    private final FirestoreService firestoreService = new FirestoreService();

    // Points required for the next reward.
    private int nextRewardPoints = 1500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // Connect this Activity to your XML layout
        setContentView(R.layout.activity_redeem_points);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pointsPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


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
        // REDEEM BUTTONS
        // ==============================

        MaterialButton redeem5Button = findViewById(R.id.redeem5Button);
        MaterialButton redeem10Button = findViewById(R.id.redeem10Button);
        MaterialButton redeem25Button = findViewById(R.id.redeem25Button);

        redeem5Button.setOnClickListener(v -> attemptRedeem(750, "$5 gift card",
                totalPointsText, nextRewardLabel, progressPercentage, rewardProgressWheel, pointsProgress));

        redeem10Button.setOnClickListener(v -> attemptRedeem(1500, "$10 gift card",
                totalPointsText, nextRewardLabel, progressPercentage, rewardProgressWheel, pointsProgress));

        redeem25Button.setOnClickListener(v -> attemptRedeem(3500, "$25 gift card",
                totalPointsText, nextRewardLabel, progressPercentage, rewardProgressWheel, pointsProgress));


        // ==============================
        // FETCH REAL POINTS FROM FIRESTORE
        // ==============================

        String currentUserId = authService.getCurrentUser() != null
                ? authService.getCurrentUser().getUid()
                : null;

        if (currentUserId == null) {
            displayPoints(0, totalPointsText, nextRewardLabel, progressPercentage, rewardProgressWheel, pointsProgress);
            return;
        }

        firestoreService.getUserProfile(currentUserId, new FirestoreService.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                displayPoints((int) user.points, totalPointsText, nextRewardLabel, progressPercentage, rewardProgressWheel, pointsProgress);
            }

            @Override
            public void onFailure(Exception e) {
                displayPoints(0, totalPointsText, nextRewardLabel, progressPercentage, rewardProgressWheel, pointsProgress);
            }
        });
    }


    // =========================================================
    // REDEEM A REWARD
    // =========================================================

    private void attemptRedeem(
            int cost,
            String rewardLabel,
            TextView totalPointsText,
            TextView nextRewardLabel,
            TextView progressPercentage,
            CircularProgressIndicator rewardProgressWheel,
            LinearProgressIndicator pointsProgress
    ) {
        String uid = authService.getCurrentUser() != null ? authService.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "You need to be signed in to redeem a reward", Toast.LENGTH_SHORT).show();
            return;
        }

        firestoreService.redeemReward(uid, cost, new FirestoreService.RedeemCallback() {
            @Override
            public void onSuccess(long newBalance) {
                Toast.makeText(RedeemPoints.this, "Redeemed " + rewardLabel + "!", Toast.LENGTH_LONG).show();
                displayPoints((int) newBalance, totalPointsText, nextRewardLabel, progressPercentage,
                        rewardProgressWheel, pointsProgress);
            }

            @Override
            public void onInsufficientPoints(long currentBalance) {
                long stillNeeded = cost - currentBalance;
                Toast.makeText(RedeemPoints.this,
                        "Not enough points yet — " + stillNeeded + " more to go", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(RedeemPoints.this, "Couldn't redeem — try again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // =========================================================
    // DISPLAY POINTS
    // =========================================================

    private void displayPoints(
            int currentPoints,
            TextView totalPointsText,
            TextView nextRewardLabel,
            TextView progressPercentage,
            CircularProgressIndicator rewardProgressWheel,
            LinearProgressIndicator pointsProgress
    ) {

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


        // Do not allow progress to go above the reward amount
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