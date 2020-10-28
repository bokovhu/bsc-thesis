package me.bokov.bsc.surfaceviewer.voxelization;

import me.bokov.bsc.surfaceviewer.sdf.threed.ExpressionEvaluationContext;
import org.joml.Vector3f;

public interface Voxelizer3D<TOut extends VoxelStorage> extends
        Voxelizer<TOut, Float, Vector3f, ExpressionEvaluationContext> {

}
