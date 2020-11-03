package me.bokov.bsc.surfaceviewer.render.texture.type;

import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;

public class Value1DFloatDelegate extends FloatTextureTypeDelegate {

    public Value1DFloatDelegate() {
        super(1);
    }

    @Override
    public void uploadData(FloatBuffer dataBuffer, int[] dimensions) {
        GL46.glTexImage1D(
                GL46.GL_TEXTURE_1D,
                0,
                GL46.GL_R32F,
                dimensions[0],
                0,
                GL46.GL_RED,
                GL46.GL_FLOAT,
                dataBuffer
        );
    }

    @Override
    public void downloadData(FloatBuffer targetBuffer) {

        GL46.glGetTexImage(
                GL46.GL_TEXTURE_1D,
                0, GL46.GL_RED, GL46.GL_FLOAT,
                targetBuffer
        );

    }

}
