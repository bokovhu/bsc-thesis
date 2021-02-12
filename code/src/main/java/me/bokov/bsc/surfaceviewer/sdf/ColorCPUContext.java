package me.bokov.bsc.surfaceviewer.sdf;

import org.joml.Vector3f;

public interface ColorCPUContext extends CPUContext {

    Vector3f getNormal();

    ColorCPUContext normal(Vector3f n);

}
