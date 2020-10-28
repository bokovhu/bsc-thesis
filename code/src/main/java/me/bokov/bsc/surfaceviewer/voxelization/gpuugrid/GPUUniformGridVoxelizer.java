package me.bokov.bsc.surfaceviewer.voxelization.gpuugrid;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.threed.ExpressionEvaluationContext;
import me.bokov.bsc.surfaceviewer.voxelization.SDFVoxelizer;
import org.joml.Vector3f;

public class GPUUniformGridVoxelizer implements SDFVoxelizer<GPUUniformGrid> {

    private final int width, height, depth;

    public GPUUniformGridVoxelizer(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    @Override
    public GPUUniformGrid voxelize(
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator,
            MeshTransform transform
    ) {

        if (!(generator instanceof GLSLDistanceExpression3D)) {
            throw new UnsupportedOperationException(
                    "GPU voxelization is only supported for GLSL evaluetable SDF scenes!");
        }

        final GPUEvaluator<ExpressionEvaluationContext> distanceExpression = generator.gpu();

        return null;
    }
}
