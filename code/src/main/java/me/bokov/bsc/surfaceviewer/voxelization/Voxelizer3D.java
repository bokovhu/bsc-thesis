package me.bokov.bsc.surfaceviewer.voxelization;

import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.threed.GPUEvaluationContext;
import org.joml.Vector3f;

public interface Voxelizer3D<TOut extends VoxelStorage> extends
        Voxelizer<TOut, Float, CPUContext, GPUContext> {

}
