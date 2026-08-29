package com.mumble_jumble.touchgrass.models;

public class Task {
    public String name;
    public String type;       // "scenery_photo" or "mutual_connect"
    public int pointValue;
    public String packId;     // links back to the ChallengePack this task belongs to

    public Task() {
        // Required empty constructor for Firestore
    }

    public Task(String name, String type, int pointValue, String packId) {
        this.name = name;
        this.type = type;
        this.pointValue = pointValue;
        this.packId = packId;
    }
}