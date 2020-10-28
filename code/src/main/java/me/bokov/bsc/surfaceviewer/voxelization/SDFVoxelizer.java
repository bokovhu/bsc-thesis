package me.bokov.bsc.surfaceviewer.voxelization;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.SDFGenerator3D;
import me.bokov.bsc.surfaceviewer.sdf.threed.ExpressionEvaluationContext;
import org.joml.Vector3f;

public interface SDFVoxelizer<TOut extends SDFVoxelStorage> {

    TOut voxelize(Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator, MeshTransform transform);

    default void tearDown() {
    }

}
