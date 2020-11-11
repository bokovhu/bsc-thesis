package me.bokov.bsc.surfaceviewer.voxelization.gpuugrid;

import me.bokov.bsc.surfaceviewer.compute.ComputeProgram;
import me.bokov.bsc.surfaceviewer.compute.VoxelizerComputeShaderGenerator;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import org.lwjgl.opengl.GL46;

public class GPUUniformGridVoxelizer implements Voxelizer3D<GPUUniformGrid> {

    private final int width, height, depth;
    private final boolean downloadAfterDone;
    private ComputeProgram voxelizerProgram = null;

    public GPUUniformGridVoxelizer(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.downloadAfterDone = true;
    }

    public GPUUniformGridVoxelizer(int width, int height, int depth, boolean doDownload) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.downloadAfterDone = doDownload;
    }

    @Override
    public GPUUniformGrid voxelize(
            Evaluatable<Float, CPUContext, GPUContext> generator,
            MeshTransform transform,
            VoxelizationContext context
    ) {

        if (voxelizerProgram == null) {
            voxelizerProgram = new ComputeProgram();
            voxelizerProgram.init();

            final var programGenerator = new VoxelizerComputeShaderGenerator(generator);
            voxelizerProgram.attachSource(programGenerator.generateVoxelizerComputeShaderSource());

            voxelizerProgram.linkAndValidate();
        }

        final var result = new GPUUniformGrid(width, height, depth);

        result.applyTransform(transform);
        result.prepareTexture();

        voxelizerProgram.use();

        result.getPositionAndValueTexture()
                .bind(0)
                .bindImage(0, false, true);
        result.getNormalTexture()
                .bind(1)
                .bindImage(1, false, true);

        voxelizerProgram.uniform("u_transform")
                .mat4(result.getTransformationMatrix());

        GL46.glDispatchCompute(width, height, depth);
        GL46.glMemoryBarrier(GL46.GL_ALL_BARRIER_BITS);

        if (downloadAfterDone) { result.downloadToCPUGrid(); }

        return result;
    }
}
