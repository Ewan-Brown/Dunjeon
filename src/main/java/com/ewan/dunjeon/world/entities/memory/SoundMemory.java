package com.ewan.dunjeon.world.entities.memory;

public class SoundMemory extends Memory{
    //Entity position when they heard the noise
    private double receivedX;
    private double receivedY;

    private boolean knowsSourceLocation;

    //Source of the sound, if applicable?
    private double sourceX;
    private double sourceY;

    //Angle of noise from entity -> noise source when they heard it
    private double receivedAngle;
    //Intensity of the sound heard, when it was heard.
    private double receivedIntensity;

    public SoundMemory(double receivedX, double receivedY, double sourceX, double sourceY, boolean knowsSourceLocation, double receivedAngle, double receivedIntensity) {
        this.receivedX = receivedX;
        this.receivedY = receivedY;
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.knowsSourceLocation = knowsSourceLocation;
        this.receivedAngle = receivedAngle;
        this.receivedIntensity = receivedIntensity;
    }

    public double getReceivedX() {
        return receivedX;
    }

    public double getReceivedY() {
        return receivedY;
    }

    public double getSourceX() {
        return sourceX;
    }

    public double getSourceY() {
        return sourceY;
    }

    public double getReceivedAngle() {
        return receivedAngle;
    }

    public double getReceivedIntensity() {
        return receivedIntensity;
    }
}
