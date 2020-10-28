package me.bokov.bsc.surfaceviewer.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f view = new Matrix4f();
    private final Matrix4f viewProjection = new Matrix4f();
    private final Matrix4f inverseViewProjection = new Matrix4f();

    private final Vector3f eye = new Vector3f();
    private final Vector3f target = new Vector3f(0f, 0f, 1f);
    private final Vector3f up = new Vector3f(0f, 1f, 0f);

    private final Vector3f right = new Vector3f(1f, 0f, 0f);
    private final Vector3f forward = new Vector3f(0f, 0f, 1f);

    private float aspect = 1.0f;

    private final float fovTan = 0.5f * (float) Math.tan(Math.toRadians(75.0f));

    public Camera update(float aspect) {
        this.aspect = aspect;

        this.projection.identity().perspective(
                (float) Math.toRadians(60.0),
                aspect,
                0.1f, 100.0f
        );

        this.view.identity().lookAt(eye, target, up);
        this.viewProjection.set(projection).mul(view);
        this.inverseViewProjection.identity()
                .mul(projection)
                .lookAt(new Vector3f(0f), new Vector3f(target).sub(eye).normalize(), up)
                .invert();

        this.forward.set(this.target).sub(this.eye);
        this.forward.normalize();

        this.right.set(forward).cross(this.up).normalize();

        return this;

    }

    public Camera update() {
        return update(this.aspect);
    }

    public Camera lookAt(Vector3f pos, Vector3f center, Vector3f up) {
        this.eye.set(pos);
        this.target.set(center);
        this.up.set(up);
        return this;
    }

    public Matrix4f P() {
        return projection;
    }

    public Matrix4f V() {
        return view;
    }

    public Matrix4f VP() {
        return viewProjection;
    }

    public Matrix4f VPinv() {
        return inverseViewProjection;
    }

    public Vector3f eye() {
        return this.eye;
    }

    public Vector3f right() {
        return this.right;
    }

    public Vector3f forward() {
        return this.forward;
    }

    public float aspectRatio() {
        return aspect;
    }

    public float fovTan() {
        return fovTan;
    }

    public Vector3f up() {
        return up;
    }

}
