package me.bokov.bsc.surfaceviewer.render;

import org.joml.Vector3f;

public class Lighting {

    private final Vector3f direction;
    private final Vector3f energy;
    private final Vector3f ambient;

    public Lighting(Vector3f direction, Vector3f energy, Vector3f ambient) {
        this.direction = direction;
        this.energy = energy;
        this.ambient = ambient;
    }

    public Vector3f Ld() {
        return direction;
    }

    public Vector3f Le() {
        return energy;
    }

    public Vector3f La() {
        return ambient;
    }

}
