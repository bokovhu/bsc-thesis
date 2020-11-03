package me.bokov.bsc.surfaceviewer.render.texture.type;

import me.bokov.bsc.surfaceviewer.util.IterationUtil;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

public abstract class UnsignedByteTextureTypeDelegate extends TextureTypeDelegate<ByteBuffer, Byte> {

    protected final int elementCount;

    public UnsignedByteTextureTypeDelegate(int elementCount) {
        this.elementCount = elementCount;
    }

    @Override
    public ByteBuffer createBuffer(int[] dimensions) {
        int dimensionProduct = dimensions[0];
        for (int i = 1; i < dimensions.length; i++) {
            dimensionProduct *= dimensions[i];
        }
        return BufferUtils.createByteBuffer(elementCount * dimensionProduct);
    }

    @Override
    public ByteBuffer copyData(
            ByteBuffer targetBuffer,
            int[] dimensions,
            Byte[] data
    ) {

        IterationUtil.iterateDimensionsLexicographically(
                dimensions.length,
                dimensions,
                (iter, idx) -> {
                    for (int elem = 0; elem < elementCount; elem++) {
                        targetBuffer.put(
                                data[elementCount * idx + elem]
                        );
                    }
                }
        );

        return targetBuffer;

    }
}
