package me.bokov.bsc.surfaceviewer.voxelization.gpuugrid;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;

public class GPUUniformGridVoxelizer implements Voxelizer3D<GPUUniformGrid> {

    private final int width, height, depth;

    public GPUUniformGridVoxelizer(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    @Override
    public GPUUniformGrid voxelize(
            Evaluatable<Float, CPUContext, GPUContext> generator,
            MeshTransform transform,
            VoxelizationContext context
    ) {

        final GPUEvaluator<GPUContext> distanceExpression = generator.gpu();

        return null;
    }
}
