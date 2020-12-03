package me.bokov.bsc.surfaceviewer.mesh.mcgpu;

import me.bokov.bsc.surfaceviewer.compute.ComputeProgram;
import me.bokov.bsc.surfaceviewer.mesh.MeshGenerator;
import me.bokov.bsc.surfaceviewer.mesh.mccpu.EdgeTable;
import me.bokov.bsc.surfaceviewer.mesh.mccpu.TriangleTable;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.GPUBuffer;
import me.bokov.bsc.surfaceviewer.util.MetricsLogger;
import me.bokov.bsc.surfaceviewer.util.ResourceUtil;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.gpuugrid.GPUUniformGrid;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.IntBuffer;
import java.util.*;

public class GPUMarchingCubes implements MeshGenerator {

    private ComputeProgram marchingCubesProgram;

    private GPUBuffer vertexCountBuffer;
    private GPUBuffer outputDataBuffer;
    private GPUBuffer triangleTableBuffer;
    private GPUBuffer edgeTableBuffer;

    private Drawable doGenerate(GPUUniformGrid gpuGrid) {

        long start = System.currentTimeMillis();

        if (marchingCubesProgram == null) {

            marchingCubesProgram = new ComputeProgram();
            marchingCubesProgram.init();
            marchingCubesProgram.attachSource(
                    ResourceUtil.readResource("glsl/mc.compute.glsl")
            );

            marchingCubesProgram.linkAndValidate();

            IntBuffer vertexCountData = BufferUtils.createIntBuffer(1);
            vertexCountData.put(0);
            vertexCountBuffer = new GPUBuffer().init()
                    .bind(GL46.GL_SHADER_STORAGE_BUFFER)
                    .upload(vertexCountData.flip(), GL46.GL_DYNAMIC_DRAW);
            outputDataBuffer = new GPUBuffer().init().bind(GL46.GL_SHADER_STORAGE_BUFFER)
                    .storage(
                            (3 + 3 + 4) * (5 * 3 * gpuGrid.xVoxelCount() * gpuGrid.yVoxelCount() * gpuGrid.zVoxelCount()) * Float.BYTES,
                            GL46.GL_MAP_READ_BIT | GL46.GL_MAP_WRITE_BIT
                    );

            final IntBuffer edgeTableData = BufferUtils.createIntBuffer(EdgeTable.EDGE_TABLE.length);
            for (int i = 0; i < EdgeTable.EDGE_TABLE.length; i++) {
                edgeTableData.put(EdgeTable.EDGE_TABLE[i]);
            }

            edgeTableBuffer = new GPUBuffer().init()
                    .bind(GL46.GL_SHADER_STORAGE_BUFFER)
                    .upload(edgeTableData.flip(), GL46.GL_STATIC_DRAW);

            final IntBuffer triangleTableData = BufferUtils.createIntBuffer(16 * TriangleTable.TRIANGLE_TABLE.length);
            for (int i = 0; i < TriangleTable.TRIANGLE_TABLE.length; i++) {

                final int[] triangleTableRow = TriangleTable.TRIANGLE_TABLE[i];

                for (int j = 0; j < 16; j++) {

                    if (j < triangleTableRow.length) {
                        triangleTableData.put(triangleTableRow[j]);
                    } else {
                        triangleTableData.put(-1);
                    }

                }

            }

            triangleTableBuffer = new GPUBuffer().init()
                    .bind(GL46.GL_SHADER_STORAGE_BUFFER)
                    .upload(triangleTableData.flip(), GL46.GL_STATIC_DRAW);

        }

        long initEnd = System.currentTimeMillis();

        marchingCubesProgram.use();

        outputDataBuffer.bind(GL46.GL_SHADER_STORAGE_BUFFER);
        outputDataBuffer.bind(GL46.GL_SHADER_STORAGE_BUFFER, 0);

        vertexCountBuffer.bind(GL46.GL_SHADER_STORAGE_BUFFER);
        vertexCountBuffer.bind(GL46.GL_SHADER_STORAGE_BUFFER, 1);

        gpuGrid.getPositionAndValueTexture().bind(0)
                .setupSampling(
                        GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE,
                        GL46.GL_LINEAR, GL46.GL_LINEAR
                );
        // gpuGrid.getPositionAndValueTexture().bindImage(2, true, false);

        gpuGrid.getNormalTexture().bind(1)
                .setupSampling(
                        GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE,
                        GL46.GL_LINEAR, GL46.GL_LINEAR
                );
        // gpuGrid.getNormalTexture().bindImage(3, true, false);

        gpuGrid.getColorShininessTexture()
                .bind(2)
                .setupSampling(
                        GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE,
                        GL46.GL_LINEAR, GL46.GL_LINEAR
                );

        triangleTableBuffer.bind(GL46.GL_SHADER_STORAGE_BUFFER, 2);
        edgeTableBuffer.bind(GL46.GL_SHADER_STORAGE_BUFFER, 3);

        long setupEnd = System.currentTimeMillis();

        marchingCubesProgram.uniform("in_positionAndValue")
                .i1(0);
        marchingCubesProgram.uniform("in_normal")
                .i1(1);
        marchingCubesProgram.uniform("in_colorShininess")
                .i1(2);

        GL46.glDispatchCompute(
                gpuGrid.xVoxelCount(),
                gpuGrid.yVoxelCount(),
                gpuGrid.zVoxelCount()
        );

        GL46.glMemoryBarrier(GL46.GL_ALL_BARRIER_BITS);
        // long fence = GL46.glFenceSync(GL46.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);

        GL46.glFlush();
        GL46.glFinish();
        // GL46.glClientWaitSync(fence, 0, 100_000L);

        long computeEnd = System.currentTimeMillis();

        vertexCountBuffer.bind(GL46.GL_SHADER_STORAGE_BUFFER);
        IntBuffer vertexCountContent = BufferUtils.createIntBuffer(1);
        GL46.glGetBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, 0L, vertexCountContent);

        // fence = GL46.glFenceSync(GL46.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);

        GL46.glFlush();
        GL46.glFinish();
        // GL46.glClientWaitSync(fence, 0, 100_000L);

        final int vertexCount = vertexCountContent.get(0);

        Drawable drawable = Drawable.standard3D();
        drawable.init();
        GL46.glBindVertexArray(0);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        outputDataBuffer.bind(GL46.GL_COPY_READ_BUFFER);
        GL46.glBindBuffer(GL46.GL_COPY_WRITE_BUFFER, drawable.getVboHandle());
        GL46.glBufferStorage(
                GL46.GL_COPY_WRITE_BUFFER,
                Float.BYTES * (3 + 3 + 4) * vertexCount,
                GL46.GL_MAP_READ_BIT | GL46.GL_MAP_WRITE_BIT
        );
        GL46.glCopyBufferSubData(
                GL46.GL_COPY_READ_BUFFER,
                GL46.GL_COPY_WRITE_BUFFER,
                0L,
                0L,
                Float.BYTES * (3 + 3 + 4) * vertexCount
        );

        // fence = GL46.glFenceSync(GL46.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
        GL46.glFlush();
        GL46.glFinish();
        // GL46.glClientWaitSync(fence, 0, 100_000L);

        drawable.configure(GL46.GL_TRIANGLES, vertexCount);

        long resultEnd = System.currentTimeMillis();

        MetricsLogger.logMetrics(
                "GPU Marching cubes (compute shader)",
                Map.of(
                        "Runtime", (System.currentTimeMillis() - start) + " ms",
                        "Initialization time", (initEnd - start) + " ms",
                        "Setup time", (setupEnd - initEnd) + " ms",
                        "Compute time", (computeEnd - setupEnd) + " ms",
                        "Result mapping time", (resultEnd - computeEnd) + " ms",
                        "Generated triangle count", (drawable.getVertexCount() / 3)
                )
        );

        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
        GL46.glBindBuffer(GL46.GL_COPY_WRITE_BUFFER, 0);
        GL46.glBindBuffer(GL46.GL_COPY_READ_BUFFER, 0);
        GL46.glActiveTexture(0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
        GL46.glBindTexture(GL46.GL_TEXTURE_3D, 0);

        return drawable;

    }

    @Override
    public Drawable generate(VoxelStorage voxelStorage) {

        if (voxelStorage instanceof GPUUniformGrid) {
            return doGenerate((GPUUniformGrid) voxelStorage);
        }

        throw new UnsupportedOperationException("Only GPUUniformGrid is supported at the moment!");

    }

    @Override
    public void tearDown() {

        if (edgeTableBuffer != null) { edgeTableBuffer.tearDown(); }
        if (triangleTableBuffer != null) { triangleTableBuffer.tearDown(); }
        if (marchingCubesProgram != null) { marchingCubesProgram.tearDown(); }
        if (vertexCountBuffer != null) { vertexCountBuffer.tearDown(); }
        if (outputDataBuffer != null) { outputDataBuffer.tearDown(); }

        edgeTableBuffer = null;
        triangleTableBuffer = null;
        marchingCubesProgram = null;
        vertexCountBuffer = null;
        outputDataBuffer = null;

    }
}
