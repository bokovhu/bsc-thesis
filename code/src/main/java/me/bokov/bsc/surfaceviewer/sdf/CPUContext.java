package me.bokov.bsc.surfaceviewer.sdf;

import me.bokov.bsc.surfaceviewer.render.TextureView;
import org.joml.Vector3f;

public interface CPUContext {

    Vector3f getPoint();

    TextureView getTexture(String name);

    CPUContext transform(Vector3f newPoint);

    CPUContext withTexture(String name, TextureView view);

}
