package me.bokov.bsc.surfaceviewer.render.texture.type;

import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class RGB2DUnsignedByteDelegate extends UnsignedByteTextureTypeDelegate {
    public RGB2DUnsignedByteDelegate() {
        super(3);
    }

    @Override
    public void uploadData(ByteBuffer dataBuffer, int[] dimensions) {
        GL46.glTexImage2D(
                GL46.GL_TEXTURE_2D,
                0,
                GL46.GL_RGB8UI,
                dimensions[0], dimensions[1],
                0,
                GL46.GL_RGB, GL46.GL_UNSIGNED_BYTE,
                dataBuffer
        );
    }

    @Override
    public void downloadData(ByteBuffer targetBuffer) {

        GL46.glGetTexImage(
                GL46.GL_TEXTURE_2D,
                0, GL46.GL_RGB, GL46.GL_UNSIGNED_BYTE,
                targetBuffer
        );

    }
}
