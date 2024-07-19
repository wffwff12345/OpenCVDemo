package com.example.opencvdemo.model;

import java.util.List;

public class Detect {
    private String name;

    private Double confidence;

    private Location box;

    private int trackId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Location getBox() {
        return box;
    }

    public void setBox(Location box) {
        this.box = box;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }
}
