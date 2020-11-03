package me.bokov.bsc.surfaceviewer.render.texture.type;

import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class RGBA2DUnsignedByteDelegate extends UnsignedByteTextureTypeDelegate {
    public RGBA2DUnsignedByteDelegate() {
        super(4);
    }

    @Override
    public void uploadData(ByteBuffer dataBuffer, int[] dimensions) {
        GL46.glTexImage2D(
                GL46.GL_TEXTURE_2D,
                0,
                GL46.GL_RGBA8UI,
                dimensions[0], dimensions[1],
                0,
                GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE,
                dataBuffer
        );
    }

    @Override
    public void downloadData(ByteBuffer targetBuffer) {

        GL46.glGetTexImage(
                GL46.GL_TEXTURE_2D,
                0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE,
                targetBuffer
        );

    }
}
