package me.bokov.bsc.surfaceviewer.render.texture.type;

import me.bokov.bsc.surfaceviewer.util.IterationUtil;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

public abstract class IntTextureTypeDelegate extends TextureTypeDelegate<IntBuffer, Integer> {

    protected final int elementCount;

    public IntTextureTypeDelegate(int elementCount) {
        this.elementCount = elementCount;
    }

    @Override
    public IntBuffer createBuffer(int[] dimensions) {
        int dimensionProduct = dimensions[0];
        for (int i = 1; i < dimensions.length; i++) {
            dimensionProduct *= dimensions[i];
        }
        return BufferUtils.createIntBuffer(elementCount * dimensionProduct);
    }

    @Override
    public IntBuffer copyData(
            IntBuffer targetBuffer,
            int[] dimensions,
            Integer[] data
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
