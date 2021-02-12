package me.bokov.bsc.surfaceviewer.scene;

import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Vector3f;

import java.io.Serializable;

public interface Materializer extends Serializable {

    int getId();

    SceneNode getBoundary();
    Evaluable<Vector3f, ColorCPUContext, ColorGPUContext> getDiffuseColor();
    Evaluable<Float, ColorCPUContext, ColorGPUContext> getShininess();

}
