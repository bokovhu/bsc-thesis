package me.bokov.bsc.surfaceviewer.voxelization.gpuugrid;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.render.Texture;
import me.bokov.bsc.surfaceviewer.voxelization.Corner;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGrid;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.*;

public class GPUUniformGrid implements VoxelStorage {

    private final int width, height, depth;
    private final int vWidth, vHeight, vDepth;
    private final FloatBuffer positionAndValueBuffer, normalBuffer;
    private Texture positionAndValueTexture, normalTexture;
    private MeshTransform transform;
    private UniformGrid cpuGrid;
    private boolean preparedTextures = false;

    public GPUUniformGrid(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;

        this.vWidth = width - 1;
        this.vHeight = height - 1;
        this.vDepth = depth - 1;

        this.transform = new MeshTransform(
                new Vector3f(0f, 0f, 0f),
                new Quaternionf(),
                1f
        );

        positionAndValueBuffer = BufferUtils.createFloatBuffer(width * height * depth * 4);
        normalBuffer = BufferUtils.createFloatBuffer(width * height * depth * 4);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int depth() {
        return depth;
    }

    public int vWidth() {
        return vWidth;
    }

    public int vHeight() {
        return vHeight;
    }

    public int vDepth() {
        return vDepth;
    }

    public GPUUniformGrid applyTransform(MeshTransform transform) {
        this.transform = transform;
        return this;
    }

    private Corner<Object> newCorner() {
        return new Corner<Object>(new Vector3f(), 0.0f, new Vector3f());
    }

    private void prepareCPUVoxels() {

        for (int z = 0; z < vDepth; z++) {
            for (int y = 0; y < vHeight; y++) {
                for (int x = 0; x < vWidth; x++) {
                    cpuGrid.putVoxel(
                            x, y, z,
                            new Voxel(
                                    newCorner(), newCorner(), newCorner(), newCorner(),
                                    newCorner(), newCorner(), newCorner(), newCorner(),
                                    new Vector3f(), new Vector3f()
                            )
                    );
                }
            }
        }

    }

    private int idx(int x, int y, int z) {
        return z * width * height + y * width + x;
    }

    private void adjustCorner(Corner c, int x, int y, int z) {
        c.getPoint().set(
                positionAndValueBuffer.get(4 * idx(x, y, z)),
                positionAndValueBuffer.get(4 * idx(x, y, z) + 1),
                positionAndValueBuffer.get(4 * idx(x, y, z) + 2)
        );
        c.getNormal().set(
                normalBuffer.get(4 * idx(x, y, z)),
                normalBuffer.get(4 * idx(x, y, z) + 1),
                normalBuffer.get(4 * idx(x, y, z) + 2)
        );
        c.setValue(positionAndValueBuffer.get(4 * idx(x, y, z) + 3));
    }

    public void downloadToRAMImage() {

        GL46.glFlush();
        GL46.glFinish();
        GL46.glMemoryBarrier(GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);

        positionAndValueTexture.bind();
        positionAndValueBuffer.clear();
        GL46.glGetTexImage(GL46.GL_TEXTURE_3D, 0, GL46.GL_RGBA, GL46.GL_FLOAT, positionAndValueBuffer);


        normalTexture.bind();
        normalBuffer.clear();
        GL46.glGetTexImage(GL46.GL_TEXTURE_3D, 0, GL46.GL_RGBA, GL46.GL_FLOAT, normalBuffer);


    }

    public void downloadToCPUGrid() {

        if (cpuGrid == null) {
            cpuGrid = new UniformGrid(
                    vWidth,
                    vHeight,
                    vDepth
            );
            prepareCPUVoxels();
        }

        downloadToRAMImage();

        for (int z = 0; z < vDepth; z++) {
            for (int y = 0; y < vHeight; y++) {
                for (int x = 0; x < vWidth; x++) {
                    final Voxel v = cpuGrid.at(
                            cpuGrid.idx(x, y, z)
                    );

                    adjustCorner(v.getC000(), x, y, z);
                    adjustCorner(v.getC001(), x, y, z + 1);
                    adjustCorner(v.getC010(), x, y + 1, z);
                    adjustCorner(v.getC011(), x, y + 1, z + 1);

                    adjustCorner(v.getC100(), x + 1, y, z);
                    adjustCorner(v.getC101(), x + 1, y, z + 1);
                    adjustCorner(v.getC110(), x + 1, y + 1, z);
                    adjustCorner(v.getC111(), x + 1, y + 1, z + 1);

                    v.getP1().set(v.getC000().getPoint());
                    v.getP2().set(v.getC111().getPoint());
                }
            }
        }

    }

    public void prepareTexture() {

        if (preparedTextures) {
            return;
        }

        this.positionAndValueTexture = new Texture()
                .init()
                .configure(GL46.GL_TEXTURE_3D, GL46.GL_RGBA, GL46.GL_FLOAT)
                .resize(width, height, depth)
                .setupSampling(
                        GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE,
                        GL46.GL_LINEAR, GL46.GL_LINEAR
                )
                .makeStorage();

        this.normalTexture = new Texture()
                .init()
                .configure(GL46.GL_TEXTURE_3D, GL46.GL_RGBA, GL46.GL_FLOAT)
                .resize(width, height, depth)
                .setupSampling(
                        GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE,
                        GL46.GL_LINEAR, GL46.GL_LINEAR
                )
                .makeStorage();

        preparedTextures = true;

    }

    @Override
    public Iterator<Voxel> voxelIterator() {
        return cpuGrid.voxelIterator();
    }

    @Override
    public Voxel closestVoxel(Vector3f p) {
        return cpuGrid.closestVoxel(p);
    }

    public Matrix4f getTransformationMatrix() {
        return transform.M();
    }

    public Texture getPositionAndValueTexture() {
        return positionAndValueTexture;
    }

    public Texture getNormalTexture() {
        return normalTexture;
    }
}
