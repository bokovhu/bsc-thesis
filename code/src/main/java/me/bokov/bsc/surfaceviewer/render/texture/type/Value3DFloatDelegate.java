package me.bokov.bsc.surfaceviewer.render.texture.type;

import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;

public class Value3DFloatDelegate extends FloatTextureTypeDelegate {

    public Value3DFloatDelegate() {
        super(1);
    }

    @Override
    public void uploadData(FloatBuffer dataBuffer, int[] dimensions) {
        GL46.glTexImage3D(
                GL46.GL_TEXTURE_3D,
                0,
                GL46.GL_R32F,
                dimensions[0], dimensions[1], dimensions[2],
                0,
                GL46.GL_RED, GL46.GL_FLOAT,
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
