package com.mumble_jumble.touchgrass.models;

public class Connection {
    public String userA;
    public String userAPackId;
    public String userATaskId;
    public boolean userAConfirmed;
    public boolean pointsAwardedA;

    public String userB;
    public String userBPackId;
    public String userBTaskId;
    public boolean userBConfirmed;
    public boolean pointsAwardedB;

    public boolean bothConfirmed;

    public Connection() {
        // Required empty constructor for Firestore
    }

    public Connection(String userA, String userAPackId, String userATaskId) {
        this.userA = userA;
        this.userAPackId = userAPackId;
        this.userATaskId = userATaskId;
        this.userAConfirmed = true;
        this.pointsAwardedA = false;

        this.userBConfirmed = false;
        this.pointsAwardedB = false;

        this.bothConfirmed = false;
    }
}
