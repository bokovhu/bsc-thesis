package me.bokov.bsc.surfaceviewer.scene;

import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import org.joml.Vector3f;

import java.io.Serializable;

public interface Materializer extends Serializable {

    int getId();

    Evaluable<Float, CPUContext, GPUContext> getBoundary();
    Evaluable<Vector3f, CPUContext, GPUContext> getDiffuseColor();
    Evaluable<Float, CPUContext, GPUContext> getShininess();

}
