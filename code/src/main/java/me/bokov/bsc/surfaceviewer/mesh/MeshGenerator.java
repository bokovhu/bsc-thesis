package me.bokov.bsc.surfaceviewer.mesh;

import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;

public interface MeshGenerator {

    SDFMesh generate(VoxelStorage voxelStorage);

    default void tearDown() {
    }

}
