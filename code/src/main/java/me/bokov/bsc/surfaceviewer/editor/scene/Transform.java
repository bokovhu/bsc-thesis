package me.bokov.bsc.surfaceviewer.editor.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {

    private final Vector3f position = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private final Vector3f scale = new Vector3f(1f, 1f, 1f);
    private final Vector3f up = new Vector3f(0f, 1f, 0f);
    private final Vector3f forward = new Vector3f(0f, 0f, 1f);
    private final Vector3f right = new Vector3f(1f, 0f, 0f);

    private final Matrix4f model = new Matrix4f();
    private final Matrix4f modelInverse = new Matrix4f();

    public Transform() {
        this(new Vector3f(0f, 0f, 0f), new Quaternionf(), new Vector3f(1f, 1f, 1f));
    }

    public Transform(Vector3f p, Quaternionf r, Vector3f s) {
        position.set(p);
        rotation.set(r);
        scale.set(s);
        update();
    }

    public Transform(Vector3f p) {
        this(p, new Quaternionf(), new Vector3f(1f, 1f, 1f));
    }

    public Transform(Vector3f p, Quaternionf r) {
        this(p, r, new Vector3f(1f, 1f, 1f));
    }

    public Transform(Transform other) {
        this(other.position, other.rotation, other.scale);
    }

    public Transform(float x, float y, float z) {
        this(new Vector3f(x, y, z), new Quaternionf(), new Vector3f(1f, 1f, 1f));
    }

    private void update() {
        model.identity()
                .scale(scale)
                .rotate(rotation)
                .translate(position);
        modelInverse.identity().set(model)
                .invert();

        up.set(0f, 1f, 0f)
                .rotate(rotation)
                .normalize();
        forward.set(0f, 0f, 1f)
                .rotate(rotation)
                .normalize();
        right.set(1f, 0f, 0f)
                .rotate(rotation)
                .normalize();
    }

    public Transform combine(Transform other) {
        position.add(other.position);
        rotation.mul(other.rotation);
        scale.mul(other.scale);
        update();
        return this;
    }

    public Transform set(Vector3f p, Quaternionf r, Vector3f s) {
        position.set(p);
        rotation.set(r);
        scale.set(s);
        update();
        return this;
    }

    public Transform set(Transform t) {
        return set(t.position, t.rotation, t.scale);
    }

    public Vector3f P() {
        return position;
    }

    public Transform P(Vector3f out) {
        out.set(position);
        return this;
    }

    public Quaternionf R() {
        return rotation;
    }

    public Transform R(Quaternionf out) {
        out.set(rotation);
        return this;
    }

    public Vector3f S() {
        return this.scale;
    }

    public Transform S(Vector3f out) {
        out.set(scale);
        return this;
    }

    public Matrix4f M() {
        return model;
    }

    public Transform M(Matrix4f out) {
        out.set(model);
        return this;
    }

    public Matrix4f Minv() {
        return modelInverse;
    }

    public Transform Minv(Matrix4f out) {
        out.set(modelInverse);
        return this;
    }

    public Vector3f up() {
        return up;
    }

    public Transform up(Vector3f out) {
        out.set(up);
        return this;
    }

    public Vector3f right() {
        return right;
    }

    public Transform right(Vector3f out) {
        out.set(right);
        return this;
    }

    public Vector3f forward() {
        return forward;
    }

    public Transform forward(Vector3f out) {
        out.set(forward);
        return this;
    }

    public Transform moveGlobal(Vector3f offset) {
        position.add(offset);
        update();
        return this;
    }

    public Transform moveLocal(Vector3f offset) {
        Vector3f deltaX = new Vector3f(right).mul(offset.x);
        Vector3f deltaY = new Vector3f(up).mul(offset.y);
        Vector3f deltaZ = new Vector3f(forward).mul(offset.z);
        position.add(new Vector3f(deltaX).add(deltaY).add(deltaZ));
        return this;
    }

    public Transform positionTo(Vector3f newPosition) {
        position.set(newPosition);
        update();
        return this;
    }

    public Transform rotate(Quaternionf deltaRotation) {
        this.rotation.mul(deltaRotation);
        update();
        return this;
    }

    public Transform rotateTo(Quaternionf orientation) {
        this.rotation.set(orientation);
        update();
        return this;
    }

    public Transform scale(Vector3f s) {
        this.scale.add(s);
        update();
        return this;
    }

    public Transform scaleTo(Vector3f s) {
        this.scale.set(s);
        update();
        return this;
    }

}
