package com.mumble_jumble.touchgrass;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mumble_jumble.touchgrass.data.AuthService;
import com.mumble_jumble.touchgrass.data.FirestoreService;
import com.mumble_jumble.touchgrass.models.ChallengePack;
import com.mumble_jumble.touchgrass.models.User;

import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity {

    // =========================================================
    // FIREBASE SERVICES
    // =========================================================

    private final AuthService authService = new AuthService();
    private final FirestoreService firestoreService =
            new FirestoreService();


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

    private View closeChallengeButton;


    // =========================================================
    // CURRENTLY SELECTED CHALLENGE
    // =========================================================

    /*
     * Stores the challenge currently being displayed.
     *
     * This is important because the enrol button needs
     * to know which challenge the user selected.
     */

    private String currentChallengeCategory = "";
    private String currentChallengeTitle = "";
    private String currentChallengeDescription = "";

    // The real Firestore pack matching whichever challenge popup is currently
    // open, resolved by keyword match against pack name/activityType (see
    // resolvePackId()). Null if no matching pack was found — e.g. "Try
    // Something New" has no backend pack behind it yet.
    private String currentChallengePackId = null;

    // Loaded once at startup so we can match hardcoded challenge titles
    // (Hiking, Basketball, Photo Walk) to their real Firestore doc IDs.
    private final List<ChallengePack> allChallengePacks = new ArrayList<>();


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

        loadChallengePacksForMapping();


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

        TextView pointsText =
                findViewById(R.id.pointsText);

        TextView usernameText =
                findViewById(R.id.usernameText);

        View signOutButton =
                findViewById(R.id.signOutButton);

        View redeemPointsButton =
                findViewById(R.id.redeemPointsButton);


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
        // TASK LIST POPUP
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
                redeemPointsButton
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
        // SIGN OUT
        // =====================================================

        signOutButton.setOnClickListener(v -> {

            authService.signOut();

            startActivity(
                    new Intent(
                            this,
                            AuthScreen.class
                    )
            );

            finish();
        });


        // =====================================================
        // REDEEM POINTS
        // =====================================================

        redeemPointsButton.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            this,
                            RedeemPoints.class
                    )
            );
        });


        // =====================================================
        // REAL USER DATA
        // =====================================================

        FirebaseUser currentUser =
                authService.getCurrentUser();

        if (currentUser != null) {

            firestoreService.getUserProfile(
                    currentUser.getUid(),
                    new FirestoreService.UserProfileCallback() {

                        @Override
                        public void onSuccess(User user) {

                            // Username from Firestore
                            usernameText.setText(
                                    user.displayName
                            );

                            // Points from Firestore
                            animatePoints(
                                    pointsText,
                                    (int) user.points
                            );
                        }

                        @Override
                        public void onFailure(Exception e) {

                            /*
                             * Non-fatal.
                             *
                             * The UI keeps its XML
                             * placeholder values.
                             */
                        }
                    }
            );

        } else {

            /*
             * If there is no logged-in user,
             * keep a safe fallback.
             */
            pointsText.setText(
                    "0 points"
            );
        }


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

        if (
                currentChallengeTitle == null ||
                        currentChallengeTitle.isEmpty()
        ) {
            return;
        }


        addOngoingChallenge(

                currentChallengeCategory,

                currentChallengeTitle,

                currentChallengeDescription,

                currentChallengePackId
        );


        // =====================================================
        // START REAL PROGRESS TRACKING IN FIRESTORE
        // =====================================================

        FirebaseUser currentUser = authService.getCurrentUser();

        if (currentChallengePackId != null && currentUser != null) {

            firestoreService.startChallenge(
                    currentUser.getUid(),
                    currentChallengePackId,
                    new FirestoreService.WriteCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d("Homepage", "Challenge progress started for pack " + currentChallengePackId);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("Homepage", "Failed to start challenge progress", e);
                        }
                    }
            );

        } else {

            // No matching Firestore pack found for this card (e.g. "Try
            // Something New" has no backend pack yet) — enrolled locally
            // only, tapping into it later won't open the real task screen.
            Log.w("Homepage", "No matching Firestore pack for '" + currentChallengeTitle
                    + "' — enrolled locally only, no backend progress tracking");
        }


        hideChallenge();
    }


    // =========================================================
    // ADD ONGOING CHALLENGE
    // =========================================================

    private void addOngoingChallenge(

            String category,

            String title,

            String description,

            String packId
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
        // CARD LAYOUT
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

        card.addView(
                content
        );


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

        addPressAnimation(
                card
        );


        // =====================================================
        // TAP TO OPEN REAL TASK SCREEN
        // =====================================================

        card.setOnClickListener(v -> {

            if (packId != null) {

                Intent intent = new Intent(Homepage.this, ChallengeProgress.class);
                intent.putExtra("packId", packId);
                intent.putExtra("packName", title);
                intent.putExtra("packDescription", description);
                startActivity(intent);

            } else {

                Toast.makeText(
                        Homepage.this,
                        "This challenge isn't linked to a real pack yet",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // =====================================================
        // ADD CARD
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

        currentChallengeCategory =
                category;

        currentChallengeTitle =
                title;

        currentChallengeDescription =
                description;

        currentChallengePackId =
                resolvePackId(title);


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
    // LOAD REAL CHALLENGE PACKS (for mapping hardcoded UI cards
    // to their actual Firestore doc IDs)
    // =========================================================

    private void loadChallengePacksForMapping() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("challengePacks")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    allChallengePacks.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        ChallengePack pack = doc.toObject(ChallengePack.class);
                        pack.challengeId = doc.getId();
                        allChallengePacks.add(pack);
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("Homepage", "Failed to load challenge packs for mapping", e)
                );
    }


    // =========================================================
    // RESOLVE PACK ID
    //
    // Matches a hardcoded challenge title (Hiking, Basketball,
    // Photo Walk) to the real Firestore pack behind it, by
    // checking the pack's name/activityType for a keyword.
    // Returns null if no match is found (e.g. "Try Something
    // New" has no backend pack yet).
    // =========================================================

    private String resolvePackId(String title) {

        String lowerTitle = title.toLowerCase();

        String keyword = null;

        if (lowerTitle.contains("hik")) {
            keyword = "hik";
        } else if (lowerTitle.contains("basketball")) {
            keyword = "basketball";
        } else if (lowerTitle.contains("photo")) {
            keyword = "photo";
        }

        if (keyword == null) {
            return null;
        }

        for (ChallengePack pack : allChallengePacks) {

            String name = pack.name != null ? pack.name.toLowerCase() : "";
            String type = pack.activityType != null ? pack.activityType.toLowerCase() : "";

            if (name.contains(keyword) || type.contains(keyword)) {
                return pack.challengeId;
            }
        }

        // Fallback: "Photo Walk" was originally seeded as "Culture Crawl"
        // in some versions of the seed data — check that too.
        if ("photo".equals(keyword)) {
            for (ChallengePack pack : allChallengePacks) {
                String name = pack.name != null ? pack.name.toLowerCase() : "";
                if (name.contains("culture")) {
                    return pack.challengeId;
                }
            }
        }

        return null;
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