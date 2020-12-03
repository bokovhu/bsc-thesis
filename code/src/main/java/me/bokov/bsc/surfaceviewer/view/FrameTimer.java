package me.bokov.bsc.surfaceviewer.view;

import org.lwjgl.glfw.GLFW;

import java.util.*;

public class FrameTimer {

    private final String label;

    private double lastTime = 0.0;
    private double lastUpdateTime = 0.0;
    private double[] movingAverageValues = new double[10];
    private double movingAverage = 0.0;
    private double reportTimer = 0.0;
    private int ticks = 0;

    public FrameTimer(String label) {
        this.label = label;
    }

    private void shiftAverage() {
        for (int i = movingAverageValues.length - 1; i >= 1; i--) {
            movingAverageValues[i] = movingAverageValues[i - 1];
        }
    }

    public void catchUp() {
        lastTime = GLFW.glfwGetTime();
    }

    public void update() {

        double now = GLFW.glfwGetTime();
        double delta = now - lastTime;
        double updateDelta = now - lastUpdateTime;
        lastTime = now;
        lastUpdateTime = now;

        shiftAverage();
        movingAverageValues[0] = delta;
        movingAverage = 0.0;
        for (int i = 0; i < movingAverageValues.length; i++) {
            movingAverage += movingAverageValues[i];
        }
        movingAverage /= (double) movingAverageValues.length;

        reportTimer += updateDelta;
        if (reportTimer >= 1.0) {
            System.out.println(label + ": ticks: " + ticks + ", average: " + String.format(
                    Locale.ENGLISH,
                    "%.4f",
                    movingAverage
            ) + " ms");
            reportTimer = 0.0;
            ticks = 0;
        }

        ++ticks;

    }

}
