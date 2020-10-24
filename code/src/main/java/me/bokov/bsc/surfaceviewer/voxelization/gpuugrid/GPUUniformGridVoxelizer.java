package me.bokov.bsc.surfaceviewer.voxelization.gpuugrid;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression;
import me.bokov.bsc.surfaceviewer.sdf.SDFGenerator;
import me.bokov.bsc.surfaceviewer.voxelization.SDFVoxelizer;

public class GPUUniformGridVoxelizer implements SDFVoxelizer<GPUUniformGrid> {

    private final int width, height, depth;

    public GPUUniformGridVoxelizer(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    @Override
    public GPUUniformGrid voxelize(SDFGenerator generator, MeshTransform transform) {

        if(!(generator instanceof GLSLDistanceExpression)) {
            throw new UnsupportedOperationException("GPU voxelization is only supported for GLSL evaluetable SDF scenes!");
        }

        final GLSLDistanceExpression distanceExpression = (GLSLDistanceExpression) generator;

        return null;
    }
}
