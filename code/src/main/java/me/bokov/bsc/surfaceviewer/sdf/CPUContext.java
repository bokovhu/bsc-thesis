package me.bokov.bsc.surfaceviewer.sdf;

import org.joml.Vector3f;

public interface CPUContext {

    Vector3f getPoint();

    CPUContext transform(Vector3f newPoint);

}
