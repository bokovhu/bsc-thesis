package me.bokov.bsc.surfaceviewer.voxelization;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

public interface Voxelizer<TOut extends VoxelStorage, EOut, ECPU, EGPU extends GPUContext> {

    TOut voxelize(Evaluatable<EOut, ECPU, EGPU> generator, MeshTransform transform);

    default void tearDown() {
    }


}
