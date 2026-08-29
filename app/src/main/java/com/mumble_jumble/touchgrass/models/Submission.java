package com.mumble_jumble.touchgrass.models;
public class Submission {
    public String userId;
    public String taskId;
    public String packId;
    public String photoUrl;
    public String status;      // "pending" | "verified" | "rejected"
    public Boolean aiVerified; // null until the AI call returns
    public long timestamp;

    public Submission() {
        // Required empty constructor for Firestore
    }

    public Submission(String userId, String taskId, String packId, String photoUrl) {
        this.userId = userId;
        this.taskId = taskId;
        this.packId = packId;
        this.photoUrl = photoUrl;
        this.status = "pending";
        this.aiVerified = null;
        this.timestamp = System.currentTimeMillis();
    }
}