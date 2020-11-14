package me.bokov.bsc.surfaceviewer.mesh.mcgpu;

import me.bokov.bsc.surfaceviewer.compute.ComputeProgram;
import me.bokov.bsc.surfaceviewer.mesh.MeshGenerator;
import me.bokov.bsc.surfaceviewer.mesh.mccpu.EdgeTable;
import me.bokov.bsc.surfaceviewer.mesh.mccpu.TriangleTable;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.Texture;
import me.bokov.bsc.surfaceviewer.util.ResourceUtil;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.gpuugrid.GPUUniformGrid;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.IntBuffer;

public class GPUMarchingCubes implements MeshGenerator {

    private ComputeProgram marchingCubesProgram;
    private int vertexCountBufferHandle;

    private Texture edgeTableLUTTexture;
    private Texture triangleTableLUTTexture;

    private Drawable doGenerate(GPUUniformGrid gpuGrid) {

        if (marchingCubesProgram == null) {

            marchingCubesProgram = new ComputeProgram();
            marchingCubesProgram.init();
            marchingCubesProgram.attachSource(
                    ResourceUtil.readResource("glsl/mc.compute.glsl")
            );

            marchingCubesProgram.linkAndValidate();


            vertexCountBufferHandle = GL46.glGenBuffers();
            GL46.glBindBuffer(GL46.GL_ATOMIC_COUNTER_BUFFER, vertexCountBufferHandle);
            GL46.glBufferData(GL46.GL_ATOMIC_COUNTER_BUFFER, Integer.BYTES, GL46.GL_DYNAMIC_DRAW);

            final IntBuffer edgeTableData = BufferUtils.createIntBuffer(256);
            edgeTableData.put(EdgeTable.EDGE_TABLE);

            final IntBuffer triangleTableData = BufferUtils.createIntBuffer(256 * 16);
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

            edgeTableLUTTexture = new Texture()
                    .resize(256)
                    .configure(GL46.GL_TEXTURE_1D, GL46.GL_RED, GL46.GL_UNSIGNED_INT)
                    .setupSampling(GL46.GL_CLAMP_TO_EDGE, GL46.GL_NEAREST, GL46.GL_NEAREST)
                    .uploadInt(edgeTableData.flip(), false);

            triangleTableLUTTexture = new Texture()
                    .resize(16, 256)
                    .configure(GL46.GL_TEXTURE_2D, GL46.GL_RED, GL46.GL_INT)
                    .setupSampling(GL46.GL_CLAMP_TO_EDGE, GL46.GL_NEAREST, GL46.GL_NEAREST)
                    .uploadInt(triangleTableData.flip(), true);

        }

        Drawable drawable = Drawable.standard3D();
        drawable.init();

        drawable.allocate(
                drawable.vertexElementCount()
                        * (5 * 3 * gpuGrid.xVoxelCount() * gpuGrid.yVoxelCount() * gpuGrid.zVoxelCount())
                        * Float.BYTES,
                GL46.GL_DYNAMIC_DRAW
        );

        GL46.glBindVertexArray(0);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);

        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, 0, drawable.getVboHandle());
        GL46.glBindBufferBase(GL46.GL_ATOMIC_COUNTER_BUFFER, 0, vertexCountBufferHandle);

        gpuGrid.getPositionAndValueTexture().bind(0);
        gpuGrid.getPositionAndValueTexture().bindImage(0, true, false);

        gpuGrid.getNormalTexture().bind(1);
        gpuGrid.getNormalTexture().bindImage(1, true, false);

        triangleTableLUTTexture.bind(2)
                .bindImage(2, true, false);
        edgeTableLUTTexture.bind(3)
                .bindImage(3, true, false);

        marchingCubesProgram.use();

        GL46.glDispatchCompute(
                gpuGrid.xVoxelCount(),
                gpuGrid.yVoxelCount(),
                gpuGrid.zVoxelCount()
        );
        GL46.glMemoryBarrier(GL46.GL_ALL_BARRIER_BITS);

        final var mappedCounterBuffer = GL46.glMapBuffer(GL46.GL_ATOMIC_COUNTER_BUFFER, GL46.GL_READ_ONLY);
        drawable.configure(GL46.GL_TRIANGLES, mappedCounterBuffer.getInt());
        GL46.glUnmapBuffer(GL46.GL_ATOMIC_COUNTER_BUFFER);

        return drawable;

    }

    @Override
    public Drawable generate(VoxelStorage voxelStorage) {

        if (voxelStorage instanceof GPUUniformGrid) {
            return doGenerate((GPUUniformGrid) voxelStorage);
        }

        throw new UnsupportedOperationException("Only GPUUniformGrid is supported at the moment!");

    }
}
