package me.bokov.bsc.surfaceviewer.voxelization;

import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

public interface Voxelizer3D<TOut extends VoxelStorage> extends
        Voxelizer<TOut, Float, CPUContext, GPUContext> {

}
