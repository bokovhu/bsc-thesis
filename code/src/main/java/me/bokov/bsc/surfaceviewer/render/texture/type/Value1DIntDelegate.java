package me.bokov.bsc.surfaceviewer.render.texture.type;

import org.lwjgl.opengl.GL46;

import java.nio.IntBuffer;

public class Value1DIntDelegate extends IntTextureTypeDelegate {
    public Value1DIntDelegate() {
        super(1);
    }

    @Override
    public void uploadData(IntBuffer dataBuffer, int[] dimensions) {
        GL46.glTexImage2D(
                GL46.GL_TEXTURE_2D,
                0,
                GL46.GL_R32I,
                dimensions[0], dimensions[1],
                0,
                GL46.GL_RED, GL46.GL_INT,
                dataBuffer
        );
    }

    @Override
    public void downloadData(IntBuffer targetBuffer) {

        GL46.glGetTexImage(
                GL46.GL_TEXTURE_2D,
                0, GL46.GL_RED, GL46.GL_INT,
                targetBuffer
        );

    }
}
