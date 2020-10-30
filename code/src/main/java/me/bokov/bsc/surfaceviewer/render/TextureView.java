package me.bokov.bsc.surfaceviewer.render;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;

public class TextureView {

    private final Texture texture;
    private final FloatBuffer pixelBuffer;

    public TextureView(Texture texture) {
        this.texture = texture;
        this.pixelBuffer = BufferUtils.createFloatBuffer(
                4 * texture.w() * texture.h() * texture.d()
        );
        GL46.glGetTexImage(this.texture.handle(), 0, GL46.GL_RGBA, GL46.GL_FLOAT, this.pixelBuffer);
    }

    public int width() {
        return texture.w();
    }

    public int height() {
        return texture.h();
    }

    public int depth() {
        return texture.d();
    }

    public Texture.TextureType type() {
        return texture.textureType();
    }

    public Vector4f at(int x, int y, int z) {
        int idx = 4 * (this.texture.w() * this.texture.h() * z + this.texture.w() * y + x);
        return new Vector4f(
                pixelBuffer.get(idx),
                pixelBuffer.get(idx + 1),
                pixelBuffer.get(idx + 2),
                pixelBuffer.get(idx + 3)
        );
    }

    public Vector4f atTrilinear(float x, float y, float z) {

        float fractX = x - (int) x;
        float fractY = y - (int) y;
        float fractZ = z - (int) z;

        int u = (int) Math.floor(x);
        int v = (int) Math.floor(y);
        int w = (int) Math.floor(z);

        if (u >= texture.w() - 1) {
            u = texture.w() - 2;
            fractX = 1.0f;
        }

        if (u < 0) {
            u = 0;
            fractX = 0.0f;
        }

        if (v >= texture.h() - 1) {
            v = texture.h() - 2;
            fractY = 1.0f;
        }

        if (v < 0) {
            v = 0;
            fractY = 0.0f;
        }

        if (w >= texture.d() - 1) {
            w = texture.d() - 2;
            fractZ = 1.0f;
        }

        if (w < 0) {
            w = 0;
            fractZ = 0.0f;
        }

        int idx000 = 4 * (this.texture.w() * this.texture.h() * w + this.texture.w() * v + u);
        int idx001 = 4 * (this.texture.w() * this.texture.h() * (w + 1) + this.texture.w() * v + u);
        int idx010 = 4 * (this.texture.w() * this.texture.h() * w + this.texture.w() * (v + 1) + u);
        int idx011 = 4 * (this.texture.w() * this.texture.h() * (w + 1) + this.texture.w() * (v + 1) + u);
        int idx100 = 4 * (this.texture.w() * this.texture.h() * w + this.texture.w() * v + (u + 1));
        int idx101 = 4 * (this.texture.w() * this.texture.h() * (w + 1) + this.texture.w() * v + (u + 1));
        int idx110 = 4 * (this.texture.w() * this.texture.h() * w + this.texture.w() * (v + 1) + (u + 1));
        int idx111 = 4 * (this.texture.w() * this.texture.h() * (w + 1) + this.texture.w() * (v + 1) + (u + 1));

        Vector4f c00 = new Vector4f(
                pixelBuffer.get(idx000),
                pixelBuffer.get(idx000 + 1),
                pixelBuffer.get(idx000 + 2),
                pixelBuffer.get(idx000 + 3)
        ).lerp(
                new Vector4f(
                        pixelBuffer.get(idx100),
                        pixelBuffer.get(idx100 + 1),
                        pixelBuffer.get(idx100 + 2),
                        pixelBuffer.get(idx100 + 3)
                ),
                fractX
        );

        Vector4f c01 = new Vector4f(
                pixelBuffer.get(idx001),
                pixelBuffer.get(idx001 + 1),
                pixelBuffer.get(idx001 + 2),
                pixelBuffer.get(idx001 + 3)
        ).lerp(
                new Vector4f(
                        pixelBuffer.get(idx101),
                        pixelBuffer.get(idx101 + 1),
                        pixelBuffer.get(idx101 + 2),
                        pixelBuffer.get(idx101 + 3)
                ),
                fractX
        );

        Vector4f c10 = new Vector4f(
                pixelBuffer.get(idx010),
                pixelBuffer.get(idx010 + 1),
                pixelBuffer.get(idx010 + 2),
                pixelBuffer.get(idx010 + 3)
        ).lerp(
                new Vector4f(
                        pixelBuffer.get(idx110),
                        pixelBuffer.get(idx110 + 1),
                        pixelBuffer.get(idx110 + 2),
                        pixelBuffer.get(idx110 + 3)
                ),
                fractX
        );

        Vector4f c11 = new Vector4f(
                pixelBuffer.get(idx011),
                pixelBuffer.get(idx011 + 1),
                pixelBuffer.get(idx011 + 2),
                pixelBuffer.get(idx011 + 3)
        ).lerp(
                new Vector4f(
                        pixelBuffer.get(idx111),
                        pixelBuffer.get(idx111 + 1),
                        pixelBuffer.get(idx111 + 2),
                        pixelBuffer.get(idx111 + 3)
                ),
                fractX
        );

        Vector4f c0 = new Vector4f(c00).lerp(c10, fractY);
        Vector4f c1 = new Vector4f(c01).lerp(c11, fractY);

        return new Vector4f(c0).lerp(c1, fractZ);

    }

}
