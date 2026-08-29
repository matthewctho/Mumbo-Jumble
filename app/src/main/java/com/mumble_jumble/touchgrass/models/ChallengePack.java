package com.mumble_jumble.touchgrass.models;

public class ChallengePack {
    public String name;
    public String activityType;
    public String description;
    public String challengeId;  // set manually from doc.getId() after reading from Firestore

    public ChallengePack() {
        // Required empty constructor for Firestore
    }

    public ChallengePack(String name, String activityType, String description) {
        this.name = name;
        this.activityType = activityType;
        this.description = description;
    }
}