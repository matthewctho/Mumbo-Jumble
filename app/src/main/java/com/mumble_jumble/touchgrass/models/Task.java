package com.mumble_jumble.touchgrass.models;

public class Task {
    public String taskId;     // set from the Firestore doc ID after fetching — see TaskListActivity
    public String packId;
    public String name;
    public String type;       // "scenery_photo" or "mutual_connect"
    public int pointValue;
    public String description; // optional detail used to sharpen the AI verification prompt

    public Task() {
        // Required empty constructor for Firestore
    }

    public Task(String name, String type, int pointValue, String description) {
        this.name = name;
        this.type = type;
        this.pointValue = pointValue;
        this.description = description;
    }
}