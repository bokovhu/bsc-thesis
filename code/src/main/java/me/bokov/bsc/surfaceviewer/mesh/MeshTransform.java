package me.bokov.bsc.surfaceviewer.mesh;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;

public class MeshTransform implements Serializable {

    private static final Matrix4f IDENTITY = new Matrix4f().identity();
    private static final Matrix4f IDENTITY_INV = new Matrix4f().identity().invert();

    private final Vector3f position;
    private final Quaternionf orientation;
    private float scale;
    private final Matrix4f modelMatrix;
    private final Matrix4f modelInvMatrix;

    public MeshTransform() {
        this.position = new Vector3f(0f, 0f, 0f);
        this.orientation = new Quaternionf().identity();
        this.scale = 1f;
        this.modelMatrix = new Matrix4f();
        this.modelInvMatrix = new Matrix4f();
        update();
    }

    public MeshTransform(Vector3f position, Quaternionf orientation, float scale) {
        this.position = new Vector3f(position);
        this.orientation = new Quaternionf(orientation);
        this.scale = scale;
        this.modelMatrix = new Matrix4f();
        this.modelInvMatrix = new Matrix4f();
        update();

    }

    private void update() {
        this.modelMatrix.identity()
                .translate(this.position)
                .rotate(this.orientation)
                .scale(this.scale);
        this.modelInvMatrix.identity()
                .set(this.modelMatrix)
                .invert();
    }

    public Matrix4f M() {
        return this.modelMatrix;
    }

    public Matrix4f Minv() {
        return this.modelInvMatrix;
    }

    public MeshTransform applyPosition(Vector3f pos) {
        this.position.set(pos);
        update();
        return this;
    }

    public MeshTransform applyOrientation(Quaternionf ori) {
        this.orientation.set(ori);
        update();
        return this;
    }

    public MeshTransform applyScale(float scale) {
        this.scale = 1f;
        update();
        return this;
    }

    public Vector3f position() {
        return this.position;
    }

    public Quaternionf orientation() {
        return this.orientation;
    }

    public float scale() {
        return this.scale;
    }

    public boolean isIdentity() {
        return modelInvMatrix.equals(IDENTITY) || modelInvMatrix.equals(IDENTITY_INV);
    }

}
