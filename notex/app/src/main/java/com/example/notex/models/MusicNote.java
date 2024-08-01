package com.example.notex.models;

public class MusicNote {
    private String pitch;
    private double duration;

    public MusicNote(String pitch, double duration) {
        this.pitch = pitch;
        this.duration = duration;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
