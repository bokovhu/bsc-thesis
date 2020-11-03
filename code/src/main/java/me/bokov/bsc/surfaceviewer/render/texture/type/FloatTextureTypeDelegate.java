package me.bokov.bsc.surfaceviewer.render.texture.type;

import me.bokov.bsc.surfaceviewer.util.IterationUtil;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public abstract class FloatTextureTypeDelegate extends TextureTypeDelegate<FloatBuffer, Float> {

    protected final int elementCount;

    public FloatTextureTypeDelegate(int elementCount) {
        this.elementCount = elementCount;
    }

    @Override
    public FloatBuffer createBuffer(int[] dimensions) {
        int dimensionProduct = dimensions[0];
        for (int i = 1; i < dimensions.length; i++) {
            dimensionProduct *= dimensions[i];
        }
        return BufferUtils.createFloatBuffer(elementCount * dimensionProduct);
    }

    @Override
    public FloatBuffer copyData(
            FloatBuffer targetBuffer,
            int[] dimensions,
            Float[] data
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
