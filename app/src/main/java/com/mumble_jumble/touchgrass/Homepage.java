package com.mumble_jumble.touchgrass;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.mumble_jumble.touchgrass.data.AuthService;
import com.mumble_jumble.touchgrass.data.FirestoreService;
import com.mumble_jumble.touchgrass.models.User;

public class Homepage extends AppCompatActivity {

    private final AuthService authService = new AuthService();
    private final FirestoreService firestoreService = new FirestoreService();

    // =========================================================
    // TASK LIST POPUP
    // =========================================================

    private View taskListOverlay;
    private View taskListPopup;


    // =========================================================
    // CHALLENGE POPUP
    // =========================================================

    private View challengeOverlay;
    private View challengePopup;

    private TextView challengePopupCategory;
    private TextView challengePopupTitle;
    private TextView challengePopupDescription;

    private TextView challengeTask1Title;
    private TextView challengeTask1Description;

    private TextView challengeTask2Title;
    private TextView challengeTask2Description;

    private TextView challengeTask3Title;
    private TextView challengeTask3Description;

    private TextView challengeTask4Title;
    private TextView challengeTask4Description;

    private View enrolChallengeButton;
    private View enrolChallengeText;


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

        TextView usernameText =
                findViewById(R.id.usernameText);

        View signOutButton =
                findViewById(R.id.signOutButton);

        View redeemPointsButton =
                findViewById(R.id.redeemPointsButton);


        // =========================================================
        // TASK LIST POPUP
        // =========================================================

        taskListOverlay =
                findViewById(R.id.taskListOverlay);

        taskListPopup =
                findViewById(R.id.taskListPopup);

        View closeTaskListButton =
                findViewById(R.id.closeTaskListButton);

        View closeTaskListText =
                findViewById(R.id.closeTaskListText);

        View newChallengesHeader =
                findViewById(R.id.newChallengesHeader);

        View taskHiking =
                findViewById(R.id.taskHiking);

        View taskBasketball =
                findViewById(R.id.taskBasketball);

        View taskPhotoWalk =
                findViewById(R.id.taskPhotoWalk);

        View taskExplore =
                findViewById(R.id.taskExplore);


        // =========================================================
        // CHALLENGE POPUP
        // =========================================================

        challengeOverlay =
                findViewById(R.id.challengeOverlay);

        challengePopup =
                findViewById(R.id.challengePopup);

        challengePopupCategory =
                findViewById(R.id.challengePopupCategory);

        challengePopupTitle =
                findViewById(R.id.challengePopupTitle);

        challengePopupDescription =
                findViewById(R.id.challengePopupDescription);

        challengeTask1Title =
                findViewById(R.id.challengeTask1Title);

        challengeTask1Description =
                findViewById(R.id.challengeTask1Description);

        challengeTask2Title =
                findViewById(R.id.challengeTask2Title);

        challengeTask2Description =
                findViewById(R.id.challengeTask2Description);

        challengeTask3Title =
                findViewById(R.id.challengeTask3Title);

        challengeTask3Description =
                findViewById(R.id.challengeTask3Description);

        challengeTask4Title =
                findViewById(R.id.challengeTask4Title);

        challengeTask4Description =
                findViewById(R.id.challengeTask4Description);

        enrolChallengeButton =
                findViewById(R.id.enrolChallengeButton);

        enrolChallengeText =
                findViewById(R.id.enrolChallengeText);


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
        addPressAnimation(signOutButton);
        addPressAnimation(redeemPointsButton);

        addPressAnimation(taskHiking);
        addPressAnimation(taskBasketball);
        addPressAnimation(taskPhotoWalk);
        addPressAnimation(taskExplore);
        addPressAnimation(closeTaskListButton);
        addPressAnimation(enrolChallengeButton);


        // =========================================================
        // SIGN OUT
        // =========================================================

        signOutButton.setOnClickListener(v -> {
            authService.signOut();
            startActivity(new Intent(this, AuthScreen.class));
            finish();
        });


        // =========================================================
        // REDEEM POINTS
        // =========================================================

        redeemPointsButton.setOnClickListener(v ->
                startActivity(new Intent(this, RedeemPoints.class)));


        // =========================================================
        // REAL USER DATA (username + points) FROM FIRESTORE
        // =========================================================

        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            firestoreService.getUserProfile(currentUser.getUid(), new FirestoreService.UserProfileCallback() {
                @Override
                public void onSuccess(User user) {
                    usernameText.setText(user.displayName);
                    animatePoints(pointsText, (int) user.points);
                }

                @Override
                public void onFailure(Exception e) {
                    // leave placeholder header text, non-fatal
                }
            });
        }


        // =========================================================
        // OPEN TASK LIST
        // =========================================================

        newChallengesHeader.setOnClickListener(v -> showTaskList());


        // =========================================================
        // CLOSE TASK LIST
        // =========================================================

        closeTaskListButton.setOnClickListener(v -> hideTaskList());
        closeTaskListText.setOnClickListener(v -> hideTaskList());


        // =========================================================
        // HIKING
        // =========================================================

        hikingChallenge.setOnClickListener(v -> showChallenge(
                "OUTDOOR",
                "Hiking",
                "Leave the noise behind. Find the silence that's actually full.",

                "Find a Trail",
                "Choose a new walking trail you've never explored.",

                "Walk 2 Kilometres",
                "Complete a peaceful 2 km walk along your chosen trail.",

                "Take a Nature Break",
                "Stop somewhere quiet and spend 5 minutes taking in your surroundings.",

                "Capture the View",
                "Take one photo of the best view you discover."
        ));


        // =========================================================
        // BASKETBALL
        // =========================================================

        basketballChallenge.setOnClickListener(v -> showChallenge(
                "SPORT",
                "Basketball",
                "Every dribble, every shot, every clutch moment. Find out what you're made of.",

                "Warm Up",
                "Complete 10 easy shots close to the basket.",

                "Make 10 Shots",
                "Score 10 successful shots from different positions.",

                "Dribble Challenge",
                "Dribble around an obstacle course without losing control.",

                "Hit a Three",
                "Make at least one successful three-point shot."
        ));


        // =========================================================
        // PHOTO WALKS
        // =========================================================

        photoWalksChallenge.setOnClickListener(v -> showChallenge(
                "CREATIVE",
                "Photo Walk",
                "Slow down, look twice, and let the ordinary surprise you.",

                "Find Something Green",
                "Take a photo of something naturally green.",

                "Find a Texture",
                "Capture an interesting texture that you normally overlook.",

                "Find a Reflection",
                "Take a photo using a reflection from glass, water or metal.",

                "Capture the Unexpected",
                "Photograph something that makes you stop and look twice."
        ));


        // =========================================================
        // DISMISS CHALLENGE
        // =========================================================

        enrolChallengeButton.setOnClickListener(v -> hideChallenge());
        enrolChallengeText.setOnClickListener(v -> hideChallenge());


        // =========================================================
        // TASK LIST TASKS
        // =========================================================

        taskHiking.setOnClickListener(v -> {
            hideTaskList();
            showChallenge(
                    "OUTDOOR",
                    "Hiking",
                    "Leave the noise behind. Find the silence that's actually full.",

                    "Find a Trail",
                    "Choose a new walking trail you've never explored.",

                    "Walk 2 Kilometres",
                    "Complete a peaceful 2 km walk along your chosen trail.",

                    "Take a Nature Break",
                    "Stop somewhere quiet and spend 5 minutes taking in your surroundings.",

                    "Capture the View",
                    "Take one photo of the best view you discover."
            );
        });

        taskBasketball.setOnClickListener(v -> {
            hideTaskList();
            showChallenge(
                    "SPORT",
                    "Basketball",
                    "Every dribble, every shot, every clutch moment. Find out what you're made of.",

                    "Warm Up",
                    "Complete 10 easy shots close to the basket.",

                    "Make 10 Shots",
                    "Score 10 successful shots from different positions.",

                    "Dribble Challenge",
                    "Dribble around an obstacle course without losing control.",

                    "Hit a Three",
                    "Make at least one successful three-point shot."
            );
        });

        taskPhotoWalk.setOnClickListener(v -> {
            hideTaskList();
            showChallenge(
                    "CREATIVE",
                    "Photo Walk",
                    "Slow down, look twice, and let the ordinary surprise you.",

                    "Find Something Green",
                    "Take a photo of something naturally green.",

                    "Find a Texture",
                    "Capture an interesting texture that you normally overlook.",

                    "Find a Reflection",
                    "Take a photo using a reflection from glass, water or metal.",

                    "Capture the Unexpected",
                    "Photograph something that makes you stop and look twice."
            );
        });

        taskExplore.setOnClickListener(v -> {
            hideTaskList();
            showChallenge(
                    "ADVENTURE",
                    "Try Something New",
                    "Break your routine and give yourself a reason to explore.",

                    "Take a Different Route",
                    "Travel somewhere familiar using a completely different route.",

                    "Try Something You've Never Done",
                    "Choose one small activity that is completely new to you.",

                    "Talk to Someone New",
                    "Start a friendly conversation with someone you don't normally talk to.",

                    "Document the Experience",
                    "Take a photo or write down what surprised you."
            );
        });


        // =========================================================
        // PREVENT TOUCHES THROUGH OVERLAYS
        // =========================================================

        taskListOverlay.setOnTouchListener((v, event) -> true);
        challengeOverlay.setOnTouchListener((v, event) -> true);
    }


    // =============================================================
    // SHOW TASK LIST
    // =============================================================

    private void showTaskList() {
        taskListOverlay.bringToFront();
        taskListOverlay.setVisibility(View.VISIBLE);
        taskListOverlay.setAlpha(0f);

        taskListPopup.setAlpha(0f);
        taskListPopup.setScaleX(0.88f);
        taskListPopup.setScaleY(0.88f);
        taskListPopup.setTranslationY(30f);
        taskListPopup.setVisibility(View.VISIBLE);

        taskListOverlay.animate()
                .alpha(1f)
                .setDuration(220)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        taskListPopup.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(350)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }


    // =============================================================
    // HIDE TASK LIST
    // =============================================================

    private void hideTaskList() {
        if (taskListOverlay.getVisibility() != View.VISIBLE) {
            return;
        }

        taskListPopup.animate()
                .alpha(0f)
                .scaleX(0.88f)
                .scaleY(0.88f)
                .translationY(20f)
                .setDuration(220)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> taskListOverlay.animate()
                        .alpha(0f)
                        .setDuration(180)
                        .setInterpolator(new DecelerateInterpolator())
                        .withEndAction(() -> {
                            taskListOverlay.setVisibility(View.GONE);
                            taskListPopup.setAlpha(1f);
                            taskListPopup.setScaleX(1f);
                            taskListPopup.setScaleY(1f);
                            taskListPopup.setTranslationY(0f);
                        })
                        .start())
                .start();
    }

    // =============================================================
    // SHOW CHALLENGE
    // =============================================================

    private void showChallenge(
            String category,
            String title,
            String description,

            String task1Title,
            String task1Description,

            String task2Title,
            String task2Description,

            String task3Title,
            String task3Description,

            String task4Title,
            String task4Description
    ) {
        challengePopupCategory.setText(category);
        challengePopupTitle.setText(title);
        challengePopupDescription.setText(description);

        challengeTask1Title.setText(task1Title);
        challengeTask1Description.setText(task1Description);

        challengeTask2Title.setText(task2Title);
        challengeTask2Description.setText(task2Description);

        challengeTask3Title.setText(task3Title);
        challengeTask3Description.setText(task3Description);

        challengeTask4Title.setText(task4Title);
        challengeTask4Description.setText(task4Description);

        challengeOverlay.bringToFront();

        challengeOverlay.setVisibility(View.VISIBLE);
        challengeOverlay.setAlpha(0f);

        challengePopup.setVisibility(View.VISIBLE);
        challengePopup.setAlpha(0f);
        challengePopup.setScaleX(0.82f);
        challengePopup.setScaleY(0.82f);
        challengePopup.setTranslationY(50f);

        challengeOverlay.animate()
                .alpha(1f)
                .setDuration(250)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        challengePopup.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(420)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    // =============================================================
    // HIDE CHALLENGE
    // =============================================================

    private void hideChallenge() {
        if (challengeOverlay.getVisibility() != View.VISIBLE) {
            return;
        }

        challengePopup.animate()
                .alpha(0f)
                .scaleX(0.82f)
                .scaleY(0.82f)
                .translationY(40f)
                .setDuration(220)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> challengeOverlay.animate()
                        .alpha(0f)
                        .setDuration(180)
                        .setInterpolator(new DecelerateInterpolator())
                        .withEndAction(() -> {
                            challengeOverlay.setVisibility(View.GONE);
                            challengePopup.setAlpha(1f);
                            challengePopup.setScaleX(1f);
                            challengePopup.setScaleY(1f);
                            challengePopup.setTranslationY(0f);
                        })
                        .start())
                .start();
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
