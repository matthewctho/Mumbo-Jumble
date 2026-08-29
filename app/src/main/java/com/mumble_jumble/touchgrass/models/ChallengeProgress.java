package com.mumble_jumble.touchgrass.models;

import java.util.ArrayList;
import java.util.List;

public class ChallengeProgress {
    public String userId;
    public String packId;
    public List<String> tasksCompleted = new ArrayList<>();
    public long pointsEarned;
    public boolean packComplete;

    public ChallengeProgress() {
        // Required empty constructor for Firestore
    }

    public ChallengeProgress(String userId, String packId) {
        this.userId = userId;
        this.packId = packId;
        this.pointsEarned = 0;
        this.packComplete = false;
    }
}