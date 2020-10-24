package me.bokov.bsc.surfaceviewer.voxelization;

import org.joml.Vector3f;

public class Corner {

    private final Vector3f point;
    private float value;
    private final Vector3f normal;

    public Corner(Vector3f point, float value, Vector3f normal) {
        this.point = point;
        this.value = value;
        this.normal = normal;
    }

    public Vector3f getPoint() {
        return point;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Vector3f getNormal() {
        return normal;
    }
}
