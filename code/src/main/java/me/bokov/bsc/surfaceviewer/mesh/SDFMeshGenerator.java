package me.bokov.bsc.surfaceviewer.mesh;

import me.bokov.bsc.surfaceviewer.voxelization.SDFVoxelStorage;

public interface SDFMeshGenerator  {

    SDFMesh generate(SDFVoxelStorage voxelStorage);
    default void tearDown () {}

}
