package me.bokov.bsc.surfaceviewer.render;

import me.bokov.bsc.surfaceviewer.render.texture.type.*;

import static org.lwjgl.opengl.GL46.*;

public class Texture {

    public interface BufferDelegate<B, T> {
        void put(B buffer, T[] data);
    }

    public interface UploadDelegate<T> {
        void upload(T data, int[] offset, int[] dimensions);
    }

    public interface DownloadDelegate<T> {
        T download(int[] offset, int[] dimensions);
    }

    public enum Type {
        Value1DFloat(
                GL_TEXTURE_1D, GL_FLOAT, 1, GL_RED, GL_R32F,
                new Value1DFloatDelegate()
        ),
        Value2DFloat(
                GL_TEXTURE_2D, GL_FLOAT, 1, GL_RED, GL_R32F,
                new Value2DFloatDelegate()
        ),
        Value3DFloat(
                GL_TEXTURE_3D, GL_FLOAT, 1, GL_RED, GL_R32F,
                new Value3DFloatDelegate()
        ),
        Value1DInt(GL_TEXTURE_1D, GL_INT, 1, GL_RED, GL_R32I, new Value1DIntDelegate()),
        RGB2DInt(GL_TEXTURE_2D, GL_INT, 3, GL_RGB, GL_RGB32I, new RGB2DIntDelegate()),
        RGB2DUnsignedByte(GL_TEXTURE_2D, GL_UNSIGNED_BYTE, 3, GL_RGB, GL_RGB8UI, new RGB2DUnsignedByteDelegate()),
        RGBA2DUnsignedByte(GL_TEXTURE_2D, GL_UNSIGNED_BYTE, 4, GL_RGBA, GL_RGBA8UI, new RGBA2DUnsignedByteDelegate());
        public final int textureTarget;
        public final int elementType;
        public final int elementCount;
        public final int format;
        public final int internalFormat;
        public final TextureTypeDelegate<?, ?> delegate;

        Type(
                int textureTarget,
                int elementType,
                int elementCount,
                int format,
                int internalFormat,
                TextureTypeDelegate<?, ?> delegate
        ) {
            this.textureTarget = textureTarget;
            this.elementType = elementType;
            this.elementCount = elementCount;
            this.format = format;
            this.internalFormat = internalFormat;
            this.delegate = delegate;
        }
    }

    private int handle;
    private int width = 1, depth = 1, height = 1;
    private Type type;

}
