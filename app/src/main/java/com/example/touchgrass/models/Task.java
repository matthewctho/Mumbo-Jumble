package com.example.touchgrass.models;

public class Task {
    public String name;
    public String type;       // "scenery_photo" or "mutual_connect"
    public int pointValue;

    public Task(String name, String type, int pointValue) {
        this.name = name;
        this.type = type;
        this.pointValue = pointValue;
    }
}