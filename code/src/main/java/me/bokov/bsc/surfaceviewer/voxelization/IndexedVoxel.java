package me.bokov.bsc.surfaceviewer.voxelization;

import java.nio.IntBuffer;

/** @formatter:off */
public interface IndexedVoxel extends Voxel {

    int baseIndex();

    default void putEdgesTo(IntBuffer out) {
        out.put(baseIndex() + I000).put(baseIndex() + I100)
                .put(baseIndex() + I100).put(baseIndex() + I101)
                .put(baseIndex() + I101).put(baseIndex() + I001)
                .put(baseIndex() + I001).put(baseIndex() + I000)

                .put(baseIndex() + I010).put(baseIndex() + I110)
                .put(baseIndex() + I110).put(baseIndex() + I111)
                .put(baseIndex() + I111).put(baseIndex() + I011)
                .put(baseIndex() + I011).put(baseIndex() + I010)

                .put(baseIndex() + I000).put(baseIndex() + I010)
                .put(baseIndex() + I100).put(baseIndex() + I110)
                .put(baseIndex() + I101).put(baseIndex() + I111)
                .put(baseIndex() + I001).put(baseIndex() + I011);
    }

    default void putQuadsTo(IntBuffer out) {
        out.put(baseIndex() + I000).put(baseIndex() + I100).put(baseIndex() + I110).put(baseIndex() + I010)
                .put(baseIndex() + I110).put(baseIndex() + I101).put(baseIndex() + I111).put(baseIndex() + I110)
                .put(baseIndex() + I101).put(baseIndex() + I001).put(baseIndex() + I011).put(baseIndex() + I111)
                .put(baseIndex() + I001).put(baseIndex() + I000).put(baseIndex() + I010).put(baseIndex() + I011)
                .put(baseIndex() + I000).put(baseIndex() + I010).put(baseIndex() + I111).put(baseIndex() + I011)
                .put(baseIndex() + I000).put(baseIndex() + I001).put(baseIndex() + I101).put(baseIndex() + I100);
    }

    default void putPointsToInMCOrder(IntBuffer out) {
        out.put(baseIndex() + I000).put(baseIndex() + I100)
                .put(baseIndex() + I101).put(baseIndex() + I001)
                .put(baseIndex() + I010).put(baseIndex() + I110)
                .put(baseIndex() + I111).put(baseIndex() + I011);
    }

}
