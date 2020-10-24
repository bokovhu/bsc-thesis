package me.bokov.bsc.surfaceviewer.render;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

public final class FullScreenQuad {

    private FullScreenQuad() {}

    public static Drawable create() {

        Drawable d = Drawable.direct();
        d.init();

        FloatBuffer fb = BufferUtils.createFloatBuffer(6 * d.vertexElementCount());

        fb.put(new float[]{
                -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,

                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f
        });

        d.upload(fb.flip(), GL46.GL_TRIANGLES, 6);

        return d;

    }

}
