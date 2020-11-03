package me.bokov.bsc.surfaceviewer.mesh;

import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;

public interface MeshGenerator {

    Drawable generate(VoxelStorage voxelStorage);

    default void tearDown() {
    }

}
