package com.mumble_jumble.touchgrass;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the XML layout
        setContentView(R.layout.activity_homepage);


        // =========================================================
        // FIND VIEWS
        // =========================================================

        View header = findViewById(R.id.header);
        View mainContent = findViewById(R.id.mainContent);

        View ongoingChallenge =
                findViewById(R.id.ongoingChallengeBox);

        View hikingChallenge =
                findViewById(R.id.hikingChallenge);

        View basketballChallenge =
                findViewById(R.id.basketballChallenge);

        View photoWalksChallenge =
                findViewById(R.id.photoWalksChallenge);

        TextView pointsText =
                findViewById(R.id.pointsText);


        // =========================================================
        // PAGE ENTRANCE ANIMATIONS
        // =========================================================

        animateEntrance(header, 0);
        animateEntrance(mainContent, 150);

        animateEntrance(ongoingChallenge, 300);

        animateEntrance(hikingChallenge, 450);
        animateEntrance(basketballChallenge, 550);
        animateEntrance(photoWalksChallenge, 650);


        // =========================================================
        // CARD PRESS ANIMATIONS
        // =========================================================

        addPressAnimation(ongoingChallenge);
        addPressAnimation(hikingChallenge);
        addPressAnimation(basketballChallenge);
        addPressAnimation(photoWalksChallenge);
        addPressAnimation(findViewById(R.id.signOutButton));


        // =========================================================
        // POINTS TALLY ANIMATION
        // =========================================================
        //
        // Replace this with the user's REAL points value
        // from your database/backend when you have it.
        //
        // Example:
        // int userPoints = database.getUserPoints();
        //
        int userPoints = 125;

        animatePoints(pointsText, userPoints);


        // =========================================================
        // YOUR EXISTING CLICK LOGIC
        // =========================================================
        //
        // KEEP YOUR EXISTING CLICK LISTENERS HERE.
        //
        // Example:
        //
        // hikingChallenge.setOnClickListener(v -> {
        //     Intent intent = new Intent(Homepage.this,
        //             HikingActivity.class);
        //     startActivity(intent);
        // });
        //
        // Don't remove your existing navigation/database/etc.
        // logic.
        // =========================================================
    }


    // =============================================================
    // POINTS TALLY ANIMATION
    // =============================================================

    private void animatePoints(TextView pointsText, int targetPoints) {

        // Start at zero
        pointsText.setText("0 points");

        // Create the counting animation
        ValueAnimator animator =
                ValueAnimator.ofInt(0, targetPoints);

        // How long the tally takes
        animator.setDuration(1400);

        // Makes the animation start fast and slow down smoothly
        animator.setInterpolator(
                new DecelerateInterpolator()
        );

        // Update the number on screen every frame
        animator.addUpdateListener(animation -> {

            int currentPoints =
                    (int) animation.getAnimatedValue();

            pointsText.setText(
                    currentPoints + " points"
            );
        });

        // Small finishing animation
        animator.addListener(
                new android.animation.AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(
                            android.animation.Animator animation) {

                        pointsText.animate()
                                .scaleX(1.08f)
                                .scaleY(1.08f)
                                .setDuration(120)
                                .withEndAction(() -> {

                                    pointsText.animate()
                                            .scaleX(1f)
                                            .scaleY(1f)
                                            .setDuration(180)
                                            .start();

                                })
                                .start();
                    }
                }
        );

        // Start counting
        animator.start();
    }


    // =============================================================
    // ENTRANCE ANIMATION
    // =============================================================

    private void animateEntrance(View view, long delay) {

        // Starting state
        view.setAlpha(0f);
        view.setTranslationY(40f);
        view.setScaleX(0.96f);
        view.setScaleY(0.96f);

        // Animate into place
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(delay)
                .setDuration(500)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();
    }


    // =============================================================
    // CARD PRESS ANIMATION
    // =============================================================

    private void addPressAnimation(View view) {

        view.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    // Slightly shrink the card
                    v.animate()
                            .scaleX(0.97f)
                            .scaleY(0.97f)
                            .setDuration(100)
                            .setInterpolator(
                                    new DecelerateInterpolator()
                            )
                            .start();

                    break;


                case MotionEvent.ACTION_UP:

                    // Return to normal size
                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .setInterpolator(
                                    new DecelerateInterpolator()
                            )
                            .start();

                    break;


                case MotionEvent.ACTION_CANCEL:

                    // Return to normal size if touch is cancelled
                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .setInterpolator(
                                    new DecelerateInterpolator()
                            )
                            .start();

                    break;
            }

            // IMPORTANT:
            // false allows the normal click listener to
            // continue receiving the event.
            return false;
        });
    }
}
