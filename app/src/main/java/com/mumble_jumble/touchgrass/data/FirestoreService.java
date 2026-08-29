package com.mumble_jumble.touchgrass.data;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mumble_jumble.touchgrass.models.ChallengeProgress;
import com.mumble_jumble.touchgrass.models.Submission;
import com.mumble_jumble.touchgrass.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirestoreService {

    public interface WriteCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface UserProfileCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public interface ProgressCallback {
        void onSuccess(ChallengeProgress progress);
        void onFailure(Exception e);
    }

    public interface SubmissionCreatedCallback {
        void onSuccess(String submissionDocId);
        void onFailure(Exception e);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getUserProfile(String uid, UserProfileCallback callback) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> callback.onSuccess(doc.toObject(User.class)))
                .addOnFailureListener(callback::onFailure);
    }

    public void createUserProfile(String uid, String displayName, String phone, String location, WriteCallback callback) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("displayName", displayName);
        profile.put("phone", phone);
        profile.put("location", location);
        profile.put("points", 0);
        profile.put("activePacks", new ArrayList<String>());
        profile.put("pointsHidden", false);

        db.collection("users").document(uid)
                .set(profile)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // --- Challenge progress tracking ---
    // Document ID is "{uid}_{packId}" so we can look up a user's progress on
    // a specific pack directly, instead of running a query every time.

    private String progressDocId(String uid, String packId) {
        return uid + "_" + packId;
    }

    /**
     * Call this when a user joins/starts a pack. Creates a progress doc if
     * one doesn't already exist; safe to call again (won't overwrite existing progress).
     */
    public void startChallenge(String uid, String packId, WriteCallback callback) {
        String docId = progressDocId(uid, packId);
        db.collection("challengeProgress").document(docId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // Already started, nothing to do
                        callback.onSuccess();
                        return;
                    }
                    ChallengeProgress progress = new ChallengeProgress(uid, packId);
                    db.collection("challengeProgress").document(docId)
                            .set(progress)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Call this when a user completes a task. Adds the task to tasksCompleted,
     * adds its points, and marks the pack complete if totalTasksInPack is reached.
     * taskPointValue and totalTasksInPack come from the pack's task list already
     * loaded on screen (e.g. from TaskAdapter's data).
     */
    public void completeTask(String uid, String packId, String taskId, int taskPointValue,
                             int totalTasksInPack, WriteCallback callback) {
        String docId = progressDocId(uid, packId);
        db.collection("challengeProgress").document(docId)
                .update(
                        "tasksCompleted", FieldValue.arrayUnion(taskId),
                        "pointsEarned", FieldValue.increment(taskPointValue)
                )
                .addOnSuccessListener(unused -> {
                    // Re-read to check if that was the final task needed to complete the pack
                    db.collection("challengeProgress").document(docId)
                            .get()
                            .addOnSuccessListener(doc -> {
                                ChallengeProgress progress = doc.toObject(ChallengeProgress.class);
                                if (progress != null && progress.tasksCompleted.size() >= totalTasksInPack
                                        && !progress.packComplete) {
                                    db.collection("challengeProgress").document(docId)
                                            .update("packComplete", true)
                                            .addOnSuccessListener(unused2 -> callback.onSuccess())
                                            .addOnFailureListener(callback::onFailure);
                                } else {
                                    callback.onSuccess();
                                }
                            })
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /** Fetch a user's progress on a specific pack (e.g. to show a progress bar). */
    public void getProgress(String uid, String packId, ProgressCallback callback) {
        db.collection("challengeProgress").document(progressDocId(uid, packId))
                .get()
                .addOnSuccessListener(doc -> callback.onSuccess(doc.toObject(ChallengeProgress.class)))
                .addOnFailureListener(callback::onFailure);
    }

    // --- Photo submissions ---

    /**
     * Creates a "submissions" doc, starting with status "pending". Call this
     * right after the photo finishes uploading to Storage. Returns the new
     * doc's ID via the callback so you can update it once verification finishes.
     */
    public void createSubmission(Submission submission, SubmissionCreatedCallback callback) {
        db.collection("submissions")
                .add(submission)
                .addOnSuccessListener(docRef -> callback.onSuccess(docRef.getId()))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Updates a submission after the AI verification call returns (or after a
     * fallback decision — e.g. the Gemini call failed/timed out, so mark
     * "pending" for manual review rather than silently rejecting the user).
     */
    public void updateSubmissionStatus(String submissionDocId, String status, Boolean aiVerified,
                                       WriteCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("aiVerified", aiVerified);

        db.collection("submissions").document(submissionDocId)
                .update(updates)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}