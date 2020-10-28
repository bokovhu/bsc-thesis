package me.bokov.bsc.surfaceviewer.render;

import org.joml.Matrix4f;
import org.joml.Vector2f;

public class UI {

    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f view = new Matrix4f();
    private final Matrix4f viewProjection = new Matrix4f();

    private final Vector2f dimensions = new Vector2f();

    public UI update(Vector2f dims) {

        this.dimensions.set(dims);

        this.projection.identity()
                .ortho(
                        0f, dims.x, dims.y, 0f, -1f, 1f, true
                );
        this.view.identity();

        this.viewProjection.set(projection)
                .mul(this.view);

        return this;

    }

    public Matrix4f V() {
        return view;
    }

    public Matrix4f P() {
        return projection;
    }

    public Matrix4f VP() {
        return viewProjection;
    }

    public Vector2f dims() {
        return dimensions;
    }

}
