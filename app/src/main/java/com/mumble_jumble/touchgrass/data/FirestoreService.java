package com.mumble_jumble.touchgrass.data;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.mumble_jumble.touchgrass.models.Connection;
import com.mumble_jumble.touchgrass.models.Submission;
import com.mumble_jumble.touchgrass.models.User;
import com.mumble_jumble.touchgrass.models.ChallengeProgress;
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

    public interface ConnectionCreatedCallback {
        void onSuccess(String connectionDocId);
        void onFailure(Exception e);
    }

    public interface ConnectionCallback {
        void onSuccess(Connection connection);
        void onFailure(Exception e);
    }

    public interface RedeemCallback {
        void onSuccess(long newBalance);
        void onInsufficientPoints(long currentBalance);
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

        // Points live in two places on purpose: challengeProgress.pointsEarned is
        // the per-pack tally (for that pack's own progress bar), users.points is
        // the single running total everything else (Homepage, RedeemPoints) reads.
        db.collection("users").document(uid)
                .update("points", FieldValue.increment(taskPointValue))
                .addOnFailureListener(e -> Log.e("FirestoreService", "Failed to add points to user total", e));

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

    // --- Mutual connect (QR handshake) ---
    //
    // Safety rule: points/photos only ever get awarded once BOTH sides have
    // explicitly confirmed — see Connection.bothConfirmed. Never award from
    // one side alone.

    /**
     * User A calls this to start a mutual connect. Creates a "connections" doc
     * with userA already confirmed (they initiated it) and userB still empty.
     * The returned doc ID is what gets encoded into the QR code.
     */
    public void createConnection(Connection connection, ConnectionCreatedCallback callback) {
        db.collection("connections")
                .add(connection)
                .addOnSuccessListener(docRef -> callback.onSuccess(docRef.getId()))
                .addOnFailureListener(callback::onFailure);
    }

    /** One-time read of a connection doc — used right after User B scans a code. */
    public void getConnection(String connectionId, ConnectionCallback callback) {
        db.collection("connections").document(connectionId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onFailure(new Exception("That code isn't valid."));
                        return;
                    }
                    callback.onSuccess(doc.toObject(Connection.class));
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * User A keeps this listener attached while their QR code is on screen, so
     * their side reacts the moment User B scans and confirms — no polling needed.
     */
    public ListenerRegistration listenToConnection(String connectionId, ConnectionCallback callback) {
        return db.collection("connections").document(connectionId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        callback.onFailure(error);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        callback.onSuccess(snapshot.toObject(Connection.class));
                    }
                });
    }

    /**
     * User B calls this right after scanning User A's code. Only ever sets
     * User B's own side of the doc — this is what flips bothConfirmed to true.
     */
    public void confirmConnectionAsUserB(String connectionId, String userBUid, String packId,
                                         String taskId, WriteCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("userB", userBUid);
        updates.put("userBPackId", packId);
        updates.put("userBTaskId", taskId);
        updates.put("userBConfirmed", true);
        updates.put("bothConfirmed", true);

        db.collection("connections").document(connectionId)
                .update(updates)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    /** Marks this side's points as awarded, so a re-triggered listener never double-awards. */
    public void markConnectionPointsAwarded(String connectionId, boolean isUserA, WriteCallback callback) {
        String field = isUserA ? "pointsAwardedA" : "pointsAwardedB";
        db.collection("connections").document(connectionId)
                .update(field, true)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // --- Redeem points ---

    /** Thrown inside the redeemReward transaction when the user can't afford the cost. */
    private static class InsufficientPointsException extends RuntimeException {
        final long currentBalance;
        InsufficientPointsException(long currentBalance) {
            this.currentBalance = currentBalance;
        }
    }

    /**
     * Atomically spends `cost` points from the user's balance, refusing if they
     * can't afford it. Runs as a transaction (read-check-write) rather than a
     * plain FieldValue.increment(-cost), so a balance can never go negative —
     * e.g. two rapid taps on "Redeem" can't both succeed off a stale read.
     */
    public void redeemReward(String uid, int cost, RedeemCallback callback) {
        DocumentReference userRef = db.collection("users").document(uid);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(userRef);
            Long currentPoints = snapshot.getLong("points");
            long balance = currentPoints != null ? currentPoints : 0;

            if (balance < cost) {
                throw new InsufficientPointsException(balance);
            }

            long newBalance = balance - cost;
            transaction.update(userRef, "points", newBalance);
            return newBalance;
        }).addOnSuccessListener(newBalance -> callback.onSuccess((Long) newBalance))
          .addOnFailureListener(e -> {
              Throwable cause = e.getCause() instanceof InsufficientPointsException ? e.getCause() : e;
              if (cause instanceof InsufficientPointsException) {
                  callback.onInsufficientPoints(((InsufficientPointsException) cause).currentBalance);
              } else {
                  Log.e("FirestoreService", "Failed to redeem reward", e);
                  callback.onFailure(e);
              }
          });
    }
}