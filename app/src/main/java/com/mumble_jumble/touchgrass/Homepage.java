package com.mumble_jumble.touchgrass;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Homepage extends AppCompatActivity {

    // =========================================================
    // ONGOING CHALLENGES
    // =========================================================

    private LinearLayout ongoingChallengesContainer;
    private HorizontalScrollView ongoingChallengesScrollView;
    private TextView ongoingStatusText;

    private int ongoingChallengeCount = 0;


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

    private TextView enrolChallengeButton;

    // This is now used ONLY for closing the popup.
    private View closeChallengeButton;


    // =========================================================
    // CURRENTLY SELECTED CHALLENGE
    // =========================================================

    /*
     * These variables store the actual challenge the user
     * selected.
     *
     * This prevents the enrol button from accidentally
     * defaulting to Hiking.
     */

    private String currentChallengeCategory = "";
    private String currentChallengeTitle = "";
    private String currentChallengeDescription = "";


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

        View header =
                findViewById(R.id.header);

        View mainContent =
                findViewById(R.id.mainContent);

        View hikingChallenge =
                findViewById(R.id.hikingChallenge);

        View basketballChallenge =
                findViewById(R.id.basketballChallenge);

        View photoWalksChallenge =
                findViewById(R.id.photoWalksChallenge);

        View signOutButton =
                findViewById(R.id.signOutButton);

        TextView pointsText =
                findViewById(R.id.pointsText);


        // =====================================================
        // ONGOING CHALLENGES
        // =====================================================

        ongoingChallengesContainer =
                findViewById(
                        R.id.ongoingChallengesContainer
                );

        ongoingChallengesScrollView =
                findViewById(
                        R.id.ongoingChallengesScrollView
                );

        ongoingStatusText =
                findViewById(
                        R.id.ongoingStatusText
                );


        // =====================================================
        // TASK LIST
        // =====================================================

        taskListOverlay =
                findViewById(
                        R.id.taskListOverlay
                );

        taskListPopup =
                findViewById(
                        R.id.taskListPopup
                );

        View closeTaskListButton =
                findViewById(
                        R.id.closeTaskListButton
                );

        View closeTaskListText =
                findViewById(
                        R.id.closeTaskListText
                );

        View newChallengesHeader =
                findViewById(
                        R.id.newChallengesHeader
                );

        View taskHiking =
                findViewById(
                        R.id.taskHiking
                );

        View taskBasketball =
                findViewById(
                        R.id.taskBasketball
                );

        View taskPhotoWalk =
                findViewById(
                        R.id.taskPhotoWalk
                );

        View taskExplore =
                findViewById(
                        R.id.taskExplore
                );


        // =====================================================
        // CHALLENGE POPUP
        // =====================================================

        challengeOverlay =
                findViewById(
                        R.id.challengeOverlay
                );

        challengePopup =
                findViewById(
                        R.id.challengePopup
                );

        challengePopupCategory =
                findViewById(
                        R.id.challengePopupCategory
                );

        challengePopupTitle =
                findViewById(
                        R.id.challengePopupTitle
                );

        challengePopupDescription =
                findViewById(
                        R.id.challengePopupDescription
                );


        // =====================================================
        // TASK 1
        // =====================================================

        challengeTask1Title =
                findViewById(
                        R.id.challengeTask1Title
                );

        challengeTask1Description =
                findViewById(
                        R.id.challengeTask1Description
                );


        // =====================================================
        // TASK 2
        // =====================================================

        challengeTask2Title =
                findViewById(
                        R.id.challengeTask2Title
                );

        challengeTask2Description =
                findViewById(
                        R.id.challengeTask2Description
                );


        // =====================================================
        // TASK 3
        // =====================================================

        challengeTask3Title =
                findViewById(
                        R.id.challengeTask3Title
                );

        challengeTask3Description =
                findViewById(
                        R.id.challengeTask3Description
                );


        // =====================================================
        // TASK 4
        // =====================================================

        challengeTask4Title =
                findViewById(
                        R.id.challengeTask4Title
                );

        challengeTask4Description =
                findViewById(
                        R.id.challengeTask4Description
                );


        // =====================================================
        // ENROL BUTTON
        // =====================================================

        enrolChallengeButton =
                findViewById(
                        R.id.enrolChallengeButton
                );



        // =====================================================
        // CLOSE CHALLENGE BUTTON
        // =====================================================

        closeChallengeButton =
                findViewById(
                        R.id.closeChallengeButton
                );


        // =====================================================
        // PAGE ENTRANCE ANIMATIONS
        // =====================================================

        animateEntrance(
                header,
                0
        );

        animateEntrance(
                mainContent,
                150
        );

        animateEntrance(
                hikingChallenge,
                300
        );

        animateEntrance(
                basketballChallenge,
                450
        );

        animateEntrance(
                photoWalksChallenge,
                600
        );


        // =====================================================
        // PRESS ANIMATIONS
        // =====================================================

        addPressAnimation(
                hikingChallenge
        );

        addPressAnimation(
                basketballChallenge
        );

        addPressAnimation(
                photoWalksChallenge
        );

        addPressAnimation(
                signOutButton
        );

        addPressAnimation(
                taskHiking
        );

        addPressAnimation(
                taskBasketball
        );

        addPressAnimation(
                taskPhotoWalk
        );

        addPressAnimation(
                taskExplore
        );

        addPressAnimation(
                closeTaskListButton
        );

        addPressAnimation(
                enrolChallengeButton
        );

        addPressAnimation(
                closeChallengeButton
        );


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

        newChallengesHeader.setOnClickListener(
                v -> showTaskList()
        );


        // =====================================================
        // CLOSE TASK LIST
        // =====================================================

        closeTaskListButton.setOnClickListener(
                v -> hideTaskList()
        );

        closeTaskListText.setOnClickListener(
                v -> hideTaskList()
        );


        // =====================================================
        // CLOSE CHALLENGE POPUP
        // =====================================================

        closeChallengeButton.setOnClickListener(
                v -> hideChallenge()
        );


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
        // PHOTO WALK
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
        // ENROL CHALLENGE
        // =====================================================

        enrolChallengeButton.setOnClickListener(
                v -> enrolCurrentChallenge()
        );




        // =====================================================
        // TASK LIST - HIKING
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


        // =====================================================
        // TASK LIST - BASKETBALL
        // =====================================================

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


        // =====================================================
        // TASK LIST - PHOTO WALK
        // =====================================================

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


        // =====================================================
        // TASK LIST - TRY SOMETHING NEW
        // =====================================================

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
        // OVERLAY TOUCHES
        // =====================================================

        taskListOverlay.setOnTouchListener(
                (v, event) -> true
        );

        challengeOverlay.setOnTouchListener(
                (v, event) -> true
        );
    }


    // =========================================================
    // ENROL CURRENT CHALLENGE
    // =========================================================

    private void enrolCurrentChallenge() {

        /*
         * Use the challenge that was explicitly selected.
         *
         * We DO NOT read the popup text here anymore.
         * Instead, showChallenge() stores the selected
         * challenge in currentChallenge... variables.
         */

        if (
                currentChallengeTitle == null ||
                        currentChallengeTitle.isEmpty()
        ) {

            return;
        }


        addOngoingChallenge(

                currentChallengeCategory,

                currentChallengeTitle,

                currentChallengeDescription
        );


        // Close popup after enrolment.
        hideChallenge();
    }


    // =========================================================
    // ADD ONGOING CHALLENGE
    // =========================================================

    private void addOngoingChallenge(
            String category,
            String title,
            String description
    ) {

        // =====================================================
        // REMOVE EMPTY CARD
        // =====================================================

        View emptyCard =
                findViewById(
                        R.id.emptyOngoingCard
                );

        if (emptyCard != null) {

            ongoingChallengesContainer.removeView(
                    emptyCard
            );
        }


        // =====================================================
        // UPDATE COUNT
        // =====================================================

        ongoingChallengeCount++;

        ongoingStatusText.setText(
                ongoingChallengeCount + " ACTIVE"
        );


        // =====================================================
        // CREATE CARD
        // =====================================================

        CardView card =
                new CardView(this);

        card.setRadius(
                dpToPx(22)
        );

        card.setCardElevation(
                dpToPx(7)
        );

        card.setCardBackgroundColor(
                getChallengeColor(category)
        );

        card.setClickable(true);

        card.setFocusable(true);


        // =====================================================
        // CARD LAYOUT PARAMS
        // =====================================================

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(

                        dpToPx(330),

                        LinearLayout.LayoutParams.MATCH_PARENT
                );

        cardParams.setMargins(
                0,
                0,
                dpToPx(12),
                0
        );

        card.setLayoutParams(
                cardParams
        );


        // =====================================================
        // CONTENT
        // =====================================================

        ConstraintLayout content =
                new ConstraintLayout(this);

        content.setPadding(
                dpToPx(20),
                dpToPx(20),
                dpToPx(20),
                dpToPx(20)
        );

        card.addView(content);


        // =====================================================
        // CATEGORY
        // =====================================================

        TextView categoryText =
                new TextView(this);

        categoryText.setId(
                View.generateViewId()
        );

        categoryText.setText(
                category
        );

        categoryText.setTextColor(
                Color.parseColor("#BFC9B7")
        );

        categoryText.setTextSize(
                10
        );

        categoryText.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        categoryText.setLetterSpacing(
                0.12f
        );


        ConstraintLayout.LayoutParams categoryParams =
                new ConstraintLayout.LayoutParams(

                        ConstraintLayout.LayoutParams.WRAP_CONTENT,

                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );

        categoryParams.startToStart =
                ConstraintLayout.LayoutParams.PARENT_ID;

        categoryParams.topToTop =
                ConstraintLayout.LayoutParams.PARENT_ID;


        content.addView(
                categoryText,
                categoryParams
        );


        // =====================================================
        // TITLE
        // =====================================================

        TextView titleText =
                new TextView(this);

        titleText.setId(
                View.generateViewId()
        );

        titleText.setText(
                title
        );

        titleText.setTextColor(
                Color.parseColor("#F8F5EE")
        );

        titleText.setTextSize(
                21
        );

        titleText.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        titleText.setMaxLines(
                2
        );


        ConstraintLayout.LayoutParams titleParams =
                new ConstraintLayout.LayoutParams(

                        0,

                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.startToStart =
                ConstraintLayout.LayoutParams.PARENT_ID;

        titleParams.endToEnd =
                ConstraintLayout.LayoutParams.PARENT_ID;

        titleParams.topToBottom =
                categoryText.getId();

        titleParams.topMargin =
                dpToPx(10);


        content.addView(
                titleText,
                titleParams
        );


        // =====================================================
        // DESCRIPTION
        // =====================================================

        TextView descriptionText =
                new TextView(this);

        descriptionText.setId(
                View.generateViewId()
        );

        descriptionText.setText(
                description
        );

        descriptionText.setTextColor(
                Color.parseColor("#D8DFD3")
        );

        descriptionText.setTextSize(
                12
        );

        descriptionText.setLineSpacing(
                dpToPx(2),
                1f
        );

        descriptionText.setMaxLines(
                4
        );


        ConstraintLayout.LayoutParams descriptionParams =
                new ConstraintLayout.LayoutParams(

                        0,

                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );

        descriptionParams.startToStart =
                ConstraintLayout.LayoutParams.PARENT_ID;

        descriptionParams.endToEnd =
                ConstraintLayout.LayoutParams.PARENT_ID;

        descriptionParams.topToBottom =
                titleText.getId();

        descriptionParams.topMargin =
                dpToPx(8);


        content.addView(
                descriptionText,
                descriptionParams
        );


        // =====================================================
        // PROGRESS
        // =====================================================

        TextView progressText =
                new TextView(this);

        progressText.setId(
                View.generateViewId()
        );

        progressText.setText(
                "Keep going  →"
        );

        progressText.setTextColor(
                Color.parseColor("#E6E7DF")
        );

        progressText.setTextSize(
                13
        );

        progressText.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );


        ConstraintLayout.LayoutParams progressParams =
                new ConstraintLayout.LayoutParams(

                        ConstraintLayout.LayoutParams.WRAP_CONTENT,

                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );

        progressParams.startToStart =
                ConstraintLayout.LayoutParams.PARENT_ID;

        progressParams.bottomToBottom =
                ConstraintLayout.LayoutParams.PARENT_ID;


        content.addView(
                progressText,
                progressParams
        );


        // =====================================================
        // ARROW
        // =====================================================

        TextView arrow =
                new TextView(this);

        arrow.setId(
                View.generateViewId()
        );

        arrow.setText(
                "→"
        );

        arrow.setTextColor(
                Color.parseColor("#F8F5EE")
        );

        arrow.setTextSize(
                30
        );

        arrow.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );


        ConstraintLayout.LayoutParams arrowParams =
                new ConstraintLayout.LayoutParams(

                        ConstraintLayout.LayoutParams.WRAP_CONTENT,

                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );

        arrowParams.endToEnd =
                ConstraintLayout.LayoutParams.PARENT_ID;

        arrowParams.bottomToBottom =
                ConstraintLayout.LayoutParams.PARENT_ID;


        content.addView(
                arrow,
                arrowParams
        );


        // =====================================================
        // PRESS ANIMATION
        // =====================================================

        addPressAnimation(card);


        // =====================================================
        // ADD CARD TO CONTAINER
        // =====================================================

        ongoingChallengesContainer.addView(
                card
        );


        // =====================================================
        // ENTRANCE ANIMATION
        // =====================================================

        card.setAlpha(
                0f
        );

        card.setScaleX(
                0.70f
        );

        card.setScaleY(
                0.70f
        );

        card.setTranslationX(
                dpToPx(100)
        );


        card.animate()

                .alpha(1f)

                .scaleX(1f)

                .scaleY(1f)

                .translationX(0)

                .setDuration(550)

                .setInterpolator(
                        new OvershootInterpolator(1.1f)
                )

                .start();


        // =====================================================
        // SCROLL TO NEW CARD
        // =====================================================

        ongoingChallengesScrollView.postDelayed(
                () -> {

                    ongoingChallengesScrollView.fullScroll(
                            HorizontalScrollView.FOCUS_RIGHT
                    );

                },
                350
        );
    }


    // =========================================================
    // CHALLENGE COLOUR
    // =========================================================

    private int getChallengeColor(
            String category
    ) {

        if (
                category.equalsIgnoreCase(
                        "SPORT"
                )
        ) {

            return Color.parseColor(
                    "#463927"
            );
        }

        if (
                category.equalsIgnoreCase(
                        "CREATIVE"
                )
        ) {

            return Color.parseColor(
                    "#3B4038"
            );
        }

        if (
                category.equalsIgnoreCase(
                        "ADVENTURE"
                )
        ) {

            return Color.parseColor(
                    "#783911"
            );
        }

        return Color.parseColor(
                "#34472B"
        );
    }


    // =========================================================
    // SHOW TASK LIST
    // =========================================================

    private void showTaskList() {

        taskListOverlay.bringToFront();

        taskListOverlay.setVisibility(
                View.VISIBLE
        );

        taskListOverlay.setAlpha(
                0f
        );

        taskListPopup.setAlpha(
                0f
        );

        taskListPopup.setScaleX(
                0.88f
        );

        taskListPopup.setScaleY(
                0.88f
        );

        taskListPopup.setTranslationY(
                dpToPx(30)
        );

        taskListPopup.setVisibility(
                View.VISIBLE
        );


        taskListOverlay.animate()

                .alpha(1f)

                .setDuration(220)

                .setInterpolator(
                        new DecelerateInterpolator()
                )

                .start();


        taskListPopup.animate()

                .alpha(1f)

                .scaleX(1f)

                .scaleY(1f)

                .translationY(0)

                .setDuration(350)

                .setInterpolator(
                        new DecelerateInterpolator()
                )

                .start();
    }


    // =========================================================
    // HIDE TASK LIST
    // =========================================================

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

                .translationY(
                        dpToPx(20)
                )

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

                                taskListPopup.setAlpha(
                                        1f
                                );

                                taskListPopup.setScaleX(
                                        1f
                                );

                                taskListPopup.setScaleY(
                                        1f
                                );

                                taskListPopup.setTranslationY(
                                        0
                                );

                            })

                            .start();

                })

                .start();
    }


    // =========================================================
    // SHOW CHALLENGE
    // =========================================================

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
        // STORE CURRENT CHALLENGE
        // =====================================================

        /*
         * This is the important fix.
         *
         * Every time a challenge is opened, we explicitly
         * remember which challenge the user selected.
         */

        currentChallengeCategory =
                category;

        currentChallengeTitle =
                title;

        currentChallengeDescription =
                description;


        // =====================================================
        // SET POPUP TEXT
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

        challengeOverlay.setAlpha(
                0f
        );


        challengePopup.setVisibility(
                View.VISIBLE
        );

        challengePopup.setAlpha(
                0f
        );

        challengePopup.setScaleX(
                0.82f
        );

        challengePopup.setScaleY(
                0.82f
        );

        challengePopup.setTranslationY(
                dpToPx(50)
        );


        // =====================================================
        // OVERLAY ANIMATION
        // =====================================================

        challengeOverlay.animate()

                .alpha(1f)

                .setDuration(250)

                .setInterpolator(
                        new DecelerateInterpolator()
                )

                .start();


        // =====================================================
        // POPUP ANIMATION
        // =====================================================

        challengePopup.animate()

                .alpha(1f)

                .scaleX(1f)

                .scaleY(1f)

                .translationY(0)

                .setDuration(420)

                .setInterpolator(
                        new DecelerateInterpolator()
                )

                .start();
    }


    // =========================================================
    // HIDE CHALLENGE
    // =========================================================

    private void hideChallenge() {

        if (
                challengeOverlay.getVisibility()
                        != View.VISIBLE
        ) {

            return;
        }


        challengePopup.animate()

                .alpha(0f)

                .scaleX(0.82f)

                .scaleY(0.82f)

                .translationY(
                        dpToPx(40)
                )

                .setDuration(220)

                .setInterpolator(
                        new DecelerateInterpolator()
                )

                .withEndAction(() -> {

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
                                        0
                                );

                            })

                            .start();

                })

                .start();
    }


    // =========================================================
    // POINTS ANIMATION
    // =========================================================

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


        animator.setDuration(
                1400
        );


        animator.setInterpolator(
                new DecelerateInterpolator()
        );


        animator.addUpdateListener(
                animation -> {

                    int currentPoints =
                            (int)
                                    animation.getAnimatedValue();

                    pointsText.setText(
                            currentPoints
                                    + " points"
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


    // =========================================================
    // ENTRANCE ANIMATION
    // =========================================================

    private void animateEntrance(
            View view,
            long delay
    ) {

        view.setAlpha(
                0f
        );

        view.setTranslationY(
                dpToPx(40)
        );

        view.setScaleX(
                0.96f
        );

        view.setScaleY(
                0.96f
        );


        view.animate()

                .alpha(1f)

                .translationY(0)

                .scaleX(1f)

                .scaleY(1f)

                .setStartDelay(
                        delay
                )

                .setDuration(500)

                .setInterpolator(
                        new DecelerateInterpolator()
                )

                .start();
    }


    // =========================================================
    // PRESS ANIMATION
    // =========================================================

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


                    // Return false so normal click listeners
                    // continue to work.
                    return false;
                }
        );
    }


    // =========================================================
    // DP TO PX
    // =========================================================

    private int dpToPx(
            int dp
    ) {

        return (int) (

                dp *

                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}

