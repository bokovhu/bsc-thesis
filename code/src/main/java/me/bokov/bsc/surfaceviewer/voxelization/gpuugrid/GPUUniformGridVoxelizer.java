package me.bokov.bsc.surfaceviewer.voxelization.gpuugrid;

import me.bokov.bsc.surfaceviewer.compute.ComputeProgram;
import me.bokov.bsc.surfaceviewer.glsl.generator.GeneratorOptions;
import me.bokov.bsc.surfaceviewer.glsl.generator.VoxelizerComputeShaderGenerator;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.MetricsLogger;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import java.util.*;

public class GPUUniformGridVoxelizer implements Voxelizer3D<GPUUniformGrid> {

    static final int CHUNK_SIZE = 32;
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

    private GPUUniformGrid voxelizeWhole(
            World world,
            MeshTransform transform,
            VoxelizationContext context
    ) {

        long start = System.currentTimeMillis();

        if (voxelizerProgram == null) {
            voxelizerProgram = new ComputeProgram();
            voxelizerProgram.init();

            final var programGenerator = new VoxelizerComputeShaderGenerator(world);
            voxelizerProgram.attachSource(programGenerator.generateShaderSource(new GeneratorOptions()));

            voxelizerProgram.linkAndValidate();
        }

        final var result = new GPUUniformGrid(width, height, depth);

        result.prepareTexture();

        voxelizerProgram.use();

        result.getPositionAndValueTexture()
                .bind(0)
                .bindImage(0, false, true);
        result.getNormalTexture()
                .bind(1)
                .bindImage(1, false, true);

        voxelizerProgram.uniform("u_transform")
                .mat4(transform.M());
        final var voxelSize = new Vector3f(1.0f / width, 1.0f / height, 1.0f / depth);
        voxelizerProgram.uniform("u_voxelSize").vec3(voxelSize);
        voxelizerProgram.uniform("u_voxelOffset").i3(0, 0, 0);

        GL46.glDispatchCompute(width, height, depth);
        GL46.glMemoryBarrier(GL46.GL_ALL_BARRIER_BITS);

        GL46.glUseProgram(0);

        GL46.glFlush();
        GL46.glFinish();

        long gpuEnd = System.currentTimeMillis();

        if (downloadAfterDone) { result.downloadToCPUGrid(); }

        long downloadEnd = System.currentTimeMillis();

        MetricsLogger.logMetrics(
                "GPU Uniform Grid voxelization",
                Map.of(
                        "Runtime", (System.currentTimeMillis() - start) + " ms",
                        "GPU runtime", (gpuEnd - start) + " ms",
                        "Download runtime", (downloadEnd - gpuEnd) + " ms",
                        "Number of generated voxels", result.xVoxelCount() * result.yVoxelCount() * result.zVoxelCount()
                )
        );

        return result;
    }

    private GPUUniformGrid voxelizeInChunks(
            World world,
            MeshTransform transform,
            VoxelizationContext context
    ) {

        List<int[]> invocations = new ArrayList<>();
        for (int iz = 0; iz < depth; iz += CHUNK_SIZE) {
            for (int iy = 0; iy < height; iy += CHUNK_SIZE) {
                for (int ix = 0; ix < width; ix += CHUNK_SIZE) {
                    invocations.add(
                            new int[]{
                                    ix, iy, iz,
                                    Math.min(CHUNK_SIZE, width - ix),
                                    Math.min(CHUNK_SIZE, height - iy),
                                    Math.min(CHUNK_SIZE, depth - iz)
                            }
                    );
                }
            }
        }

        long start = System.currentTimeMillis();

        if (voxelizerProgram == null) {
            voxelizerProgram = new ComputeProgram();
            voxelizerProgram.init();

            final var programGenerator = new VoxelizerComputeShaderGenerator(world);
            voxelizerProgram.attachSource(programGenerator.generateShaderSource(new GeneratorOptions()));

            voxelizerProgram.linkAndValidate();
        }

        final var result = new GPUUniformGrid(width, height, depth);

        result.prepareTexture();

        voxelizerProgram.use();

        result.getPositionAndValueTexture()
                .bind(0)
                .bindImage(0, false, true);
        result.getNormalTexture()
                .bind(1)
                .bindImage(1, false, true);

        voxelizerProgram.uniform("u_transform")
                .mat4(transform.M());

        final var voxelSize = new Vector3f(1.0f / width, 1.0f / height, 1.0f / depth);
        voxelizerProgram.uniform("u_voxelSize").vec3(voxelSize);

        for (int[] invoc : invocations) {

            long invocStart = System.currentTimeMillis();

            voxelizerProgram.uniform("u_voxelOffset")
                    .i3(invoc[0], invoc[1], invoc[2]);

            GL46.glDispatchCompute(invoc[3], invoc[4], invoc[5]);
            GL46.glMemoryBarrier(GL46.GL_ALL_BARRIER_BITS);

            long invocEnd = System.currentTimeMillis();
            System.out.println("Voxelization invocation " + Arrays.toString(invoc) + " took " + (invocEnd - invocStart) + " ms");

        }

        GL46.glFlush();
        GL46.glFinish();

        long gpuEnd = System.currentTimeMillis();

        if (downloadAfterDone) { result.downloadToCPUGrid(); }

        long downloadEnd = System.currentTimeMillis();

        MetricsLogger.logMetrics(
                "GPU Uniform Grid voxelization",
                Map.of(
                        "Runtime", (System.currentTimeMillis() - start) + " ms",
                        "GPU runtime", (gpuEnd - start) + " ms",
                        "Download runtime", (downloadEnd - gpuEnd) + " ms",
                        "Number of generated voxels", result.xVoxelCount() * result.yVoxelCount() * result.zVoxelCount()
                )
        );

        return result;

    }

    @Override
    public GPUUniformGrid voxelize(
            World world,
            MeshTransform transform,
            VoxelizationContext context
    ) {

        // return voxelizeWhole(world, transform, context);
        return voxelizeInChunks(world, transform, context);
    }
}
