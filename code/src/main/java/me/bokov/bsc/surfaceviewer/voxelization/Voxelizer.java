package me.bokov.bsc.surfaceviewer.voxelization;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

public interface Voxelizer<TOut extends VoxelStorage, EOut, ECPU extends CPUContext, EGPU extends GPUContext> {


    TOut voxelize(
            World world,
            MeshTransform transform,
            VoxelizationContext context
    );

    default void tearDown() {
    }


}
