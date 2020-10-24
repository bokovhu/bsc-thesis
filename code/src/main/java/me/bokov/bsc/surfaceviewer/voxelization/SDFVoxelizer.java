package me.bokov.bsc.surfaceviewer.voxelization;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.SDFGenerator;

public interface SDFVoxelizer<TOut extends SDFVoxelStorage> {

    TOut voxelize(SDFGenerator generator, MeshTransform transform);
    default void tearDown() {}

}
