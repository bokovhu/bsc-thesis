package me.bokov.bsc.surfaceviewer.sdf;

import org.joml.Vector3f;

// TODO: CPUContext and GPUContext should be immutable
//      A builder may be created for both to overcome the issues
public interface CPUContext {

    Vector3f getPoint();

    CPUContext transform(Vector3f newPoint);

}
