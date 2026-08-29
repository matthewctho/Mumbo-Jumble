package com.mumble_jumble.touchgrass;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Homepage extends AppCompatActivity {

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


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // =====================================================
        // LOAD XML
        // =====================================================

        setContentView(R.layout.activity_homepage);


        // =====================================================
        // MAIN VIEWS
        // =====================================================

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


        // =====================================================
        // TASK LIST POPUP
        // =====================================================

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


        // =====================================================
        // CHALLENGE POPUP
        // =====================================================

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


        // TASK 1

        challengeTask1Title =
                findViewById(R.id.challengeTask1Title);

        challengeTask1Description =
                findViewById(R.id.challengeTask1Description);


        // TASK 2

        challengeTask2Title =
                findViewById(R.id.challengeTask2Title);

        challengeTask2Description =
                findViewById(R.id.challengeTask2Description);


        // TASK 3

        challengeTask3Title =
                findViewById(R.id.challengeTask3Title);

        challengeTask3Description =
                findViewById(R.id.challengeTask3Description);


        // TASK 4

        challengeTask4Title =
                findViewById(R.id.challengeTask4Title);

        challengeTask4Description =
                findViewById(R.id.challengeTask4Description);


        // DISMISS

        enrolChallengeButton =
                findViewById(R.id.enrolChallengeButton);

        enrolChallengeText =
                findViewById(R.id.enrolChallengeText);


        // =====================================================
        // PAGE ENTRANCE ANIMATIONS
        // =====================================================

        animateEntrance(header, 0);

        animateEntrance(mainContent, 150);

        animateEntrance(
                ongoingChallenge,
                300
        );

        animateEntrance(
                hikingChallenge,
                450
        );

        animateEntrance(
                basketballChallenge,
                550
        );

        animateEntrance(
                photoWalksChallenge,
                650
        );


        // =====================================================
        // CARD PRESS ANIMATIONS
        // =====================================================

        addPressAnimation(ongoingChallenge);

        addPressAnimation(hikingChallenge);

        addPressAnimation(basketballChallenge);

        addPressAnimation(photoWalksChallenge);

        addPressAnimation(
                findViewById(R.id.signOutButton)
        );

        addPressAnimation(taskHiking);

        addPressAnimation(taskBasketball);

        addPressAnimation(taskPhotoWalk);

        addPressAnimation(taskExplore);

        addPressAnimation(closeTaskListButton);

        addPressAnimation(enrolChallengeButton);


        // =====================================================
        // POINTS
        // =====================================================

        int userPoints = 125;

        animatePoints(
                pointsText,
                userPoints
        );


        // =====================================================
        // OPEN TASK LIST
        // =====================================================

        newChallengesHeader.setOnClickListener(v -> {

            showTaskList();

        });


        // =====================================================
        // CLOSE TASK LIST
        // =====================================================

        closeTaskListButton.setOnClickListener(v -> {

            hideTaskList();

        });

        closeTaskListText.setOnClickListener(v -> {

            hideTaskList();

        });


        // =====================================================
        // HIKING
        // =====================================================

        hikingChallenge.setOnClickListener(v -> {

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


        // =====================================================
        // BASKETBALL
        // =====================================================

        basketballChallenge.setOnClickListener(v -> {

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


        // =====================================================
        // PHOTO WALKS
        // =====================================================

        photoWalksChallenge.setOnClickListener(v -> {

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


        // =====================================================
        // DISMISS CHALLENGE
        // =====================================================

        enrolChallengeButton.setOnClickListener(v -> {

            hideChallenge();

        });

        enrolChallengeText.setOnClickListener(v -> {

            hideChallenge();

        });


        // =====================================================
        // TASK LIST TASKS
        // =====================================================

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


        // =====================================================
        // PREVENT TOUCHES THROUGH OVERLAYS
        // =====================================================

        taskListOverlay.setOnTouchListener(
                (v, event) -> true
        );

        challengeOverlay.setOnTouchListener(
                (v, event) -> true
        );
    }


    // =============================================================
    // SHOW TASK LIST
    // =============================================================

    private void showTaskList() {

        taskListOverlay.bringToFront();

        taskListOverlay.setVisibility(
                View.VISIBLE
        );

        taskListOverlay.setAlpha(0f);

        taskListPopup.setAlpha(0f);

        taskListPopup.setScaleX(0.88f);

        taskListPopup.setScaleY(0.88f);

        taskListPopup.setTranslationY(30f);

        taskListPopup.setVisibility(
                View.VISIBLE
        );


        // =====================================================
        // OVERLAY
        // =====================================================

        taskListOverlay.animate()
                .alpha(1f)
                .setDuration(220)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();


        // =====================================================
        // POPUP
        // =====================================================

        taskListPopup.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(350)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();
    }


    // =============================================================
    // HIDE TASK LIST
    // =============================================================

    private void hideTaskList() {

        if (
                taskListOverlay.getVisibility()
                        != View.VISIBLE
        ) {
            return;
        }


        taskListPopup.animate()
                .alpha(0f)
                .scaleX(0.88f)
                .scaleY(0.88f)
                .translationY(20f)
                .setDuration(220)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .withEndAction(() -> {

                    taskListOverlay.animate()
                            .alpha(0f)
                            .setDuration(180)
                            .setInterpolator(
                                    new DecelerateInterpolator()
                            )
                            .withEndAction(() -> {

                                taskListOverlay.setVisibility(
                                        View.GONE
                                );

                                taskListPopup.setAlpha(1f);

                                taskListPopup.setScaleX(1f);

                                taskListPopup.setScaleY(1f);

                                taskListPopup.setTranslationY(0f);

                            })
                            .start();

                })
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


        // =====================================================
        // SET TEXT
        // =====================================================

        challengePopupCategory.setText(
                category
        );

        challengePopupTitle.setText(
                title
        );

        challengePopupDescription.setText(
                description
        );


        challengeTask1Title.setText(
                task1Title
        );

        challengeTask1Description.setText(
                task1Description
        );


        challengeTask2Title.setText(
                task2Title
        );

        challengeTask2Description.setText(
                task2Description
        );


        challengeTask3Title.setText(
                task3Title
        );

        challengeTask3Description.setText(
                task3Description
        );


        challengeTask4Title.setText(
                task4Title
        );

        challengeTask4Description.setText(
                task4Description
        );


        // =====================================================
        // BRING TO FRONT
        // =====================================================

        challengeOverlay.bringToFront();


        // =====================================================
        // INITIAL STATE
        // =====================================================

        challengeOverlay.setVisibility(
                View.VISIBLE
        );

        challengeOverlay.setAlpha(0f);


        challengePopup.setVisibility(
                View.VISIBLE
        );

        challengePopup.setAlpha(0f);

        challengePopup.setScaleX(0.82f);

        challengePopup.setScaleY(0.82f);

        challengePopup.setTranslationY(50f);


        // =====================================================
        // OVERLAY FADE
        // =====================================================

        challengeOverlay.animate()
                .alpha(1f)
                .setDuration(250)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();


        // =====================================================
        // POPUP
        // =====================================================

        challengePopup.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(420)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();
    }


    // =============================================================
    // HIDE CHALLENGE
    // =============================================================

    private void hideChallenge() {

        if (
                challengeOverlay.getVisibility()
                        != View.VISIBLE
        ) {
            return;
        }


        // =====================================================
        // POPUP CLOSE
        // =====================================================

        challengePopup.animate()
                .alpha(0f)
                .scaleX(0.82f)
                .scaleY(0.82f)
                .translationY(40f)
                .setDuration(220)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .withEndAction(() -> {


                    // =========================================
                    // OVERLAY CLOSE
                    // =========================================

                    challengeOverlay.animate()
                            .alpha(0f)
                            .setDuration(180)
                            .setInterpolator(
                                    new DecelerateInterpolator()
                            )
                            .withEndAction(() -> {

                                challengeOverlay.setVisibility(
                                        View.GONE
                                );


                                // Reset

                                challengePopup.setAlpha(
                                        1f
                                );

                                challengePopup.setScaleX(
                                        1f
                                );

                                challengePopup.setScaleY(
                                        1f
                                );

                                challengePopup.setTranslationY(
                                        0f
                                );

                            })
                            .start();

                })
                .start();
    }


    // =============================================================
    // POINTS ANIMATION
    // =============================================================

    private void animatePoints(
            TextView pointsText,
            int targetPoints
    ) {

        pointsText.setText(
                "0 points"
        );


        ValueAnimator animator =
                ValueAnimator.ofInt(
                        0,
                        targetPoints
                );


        animator.setDuration(1400);


        animator.setInterpolator(
                new DecelerateInterpolator()
        );


        animator.addUpdateListener(
                animation -> {

                    int currentPoints =
                            (int) animation.getAnimatedValue();

                    pointsText.setText(
                            currentPoints + " points"
                    );

                }
        );


        animator.addListener(
                new android.animation.AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(
                            android.animation.Animator animation
                    ) {

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


        animator.start();
    }


    // =============================================================
    // ENTRANCE ANIMATION
    // =============================================================

    private void animateEntrance(
            View view,
            long delay
    ) {

        view.setAlpha(0f);

        view.setTranslationY(40f);

        view.setScaleX(0.96f);

        view.setScaleY(0.96f);


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
    // PRESS ANIMATION
    // =============================================================

    private void addPressAnimation(
            View view
    ) {

        view.setOnTouchListener(
                (v, event) -> {

                    switch (
                            event.getAction()
                    ) {

                        case MotionEvent.ACTION_DOWN:

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


                    // Allow click listener

                    return false;
                }
        );
    }
}