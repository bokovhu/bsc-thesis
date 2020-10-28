package me.bokov.bsc.surfaceviewer.mesh;

import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;

public interface SDFMeshGenerator {

    SDFMesh generate(VoxelStorage voxelStorage);

    default void tearDown() {
    }

}
