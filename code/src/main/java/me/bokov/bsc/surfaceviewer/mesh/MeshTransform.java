package me.bokov.bsc.surfaceviewer.mesh;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;

// TODO: Transformation combination
// TODO: Manipulation functions
public class MeshTransform implements Serializable {

    private final Vector3f position;
    private final Quaternionf orientation;
    private final Vector3f scale;
    private final Matrix4f modelMatrix;
    private final Matrix4f modelInvMatrix;

    public MeshTransform(Vector3f position, Quaternionf orientation, Vector3f scale) {
        this.position = position;
        this.orientation = orientation;
        this.scale = scale;
        this.modelMatrix = new Matrix4f()
                .translate(this.position)
                .rotate(this.orientation)
                .scale(this.scale);
        this.modelInvMatrix = new Matrix4f()
                .set(this.modelMatrix)
                .invert();
    }

    public Matrix4f M() {
        return this.modelMatrix;
    }

    public Matrix4f Minv() {
        return this.modelInvMatrix;
    }

}
