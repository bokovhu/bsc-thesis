package me.bokov.bsc.surfaceviewer.voxelization.gpuugrid;

import me.bokov.bsc.surfaceviewer.render.Texture;
import me.bokov.bsc.surfaceviewer.voxelization.GridVoxel;
import me.bokov.bsc.surfaceviewer.voxelization.GridVoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelData;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGrid;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.*;

public class GPUUniformGrid implements GridVoxelStorage {

    private final int width, height, depth;
    private final int vWidth, vHeight, vDepth;
    private final FloatBuffer positionAndValueBuffer, normalBuffer;
    private Texture positionAndValueTexture, normalTexture;
    private UniformGrid tmpUniformGrid = null;
    private boolean preparedTextures = false;

    public GPUUniformGrid(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;

        this.vWidth = width - 1;
        this.vHeight = height - 1;
        this.vDepth = depth - 1;

        positionAndValueBuffer = BufferUtils.createFloatBuffer(width * height * depth * 4);
        normalBuffer = BufferUtils.createFloatBuffer(width * height * depth * 3);
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public int depth() {
        return depth;
    }

    public void downloadToRAMImage() {

        GL46.glFlush();
        GL46.glFinish();

        System.out.println("downloadToRamImage() after flush-finish 0");

        positionAndValueTexture.bind(0);
        positionAndValueBuffer.clear();
        GL46.glGetTexImage(GL46.GL_TEXTURE_3D, 0, GL46.GL_RGBA, GL46.GL_FLOAT, positionAndValueBuffer);
        GL46.glBindTexture(GL46.GL_TEXTURE_3D, 0);


        normalTexture.bind(1);
        normalBuffer.clear();
        GL46.glGetTexImage(GL46.GL_TEXTURE_3D, 0, GL46.GL_RGB, GL46.GL_FLOAT, normalBuffer);
        GL46.glBindTexture(GL46.GL_TEXTURE_3D, 0);


        System.out.println("downloadToRamImage() before flush-finish");

        GL46.glFlush();
        GL46.glFinish();

        System.out.println("downloadToRamImage() after flush-finish");
    }

    public void downloadToCPUGrid() {

        downloadToRAMImage();

        tmpUniformGrid = new UniformGrid(
                width, height, depth,
                positionAndValueBuffer,
                normalBuffer
        );

        System.out.println("downloadToCPUGrid() done");

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

    }

    @Override
    public Iterator<Voxel> voxelIterator() {

        downloadToCPUGrid();
        return tmpUniformGrid.voxelIterator();
    }

    @Override
    public Voxel closestVoxel(Vector3f p) {
        downloadToCPUGrid();
        return tmpUniformGrid.closestVoxel(p);
    }

    @Override
    public VoxelData toVoxelData() {
        downloadToCPUGrid();
        return tmpUniformGrid.toVoxelData();
    }

    public Texture getPositionAndValueTexture() {
        return positionAndValueTexture;
    }

    public Texture getNormalTexture() {
        return normalTexture;
    }

    @Override
    public GridVoxel at(int index) {
        // downloadToCPUGrid();
        return tmpUniformGrid.at(index);
    }

    @Override
    public GridVoxel at(int x, int y, int z) {
        // downloadToCPUGrid();
        return tmpUniformGrid.at(x, y, z);
    }

    @Override
    public int xVoxelCount() {
        return vWidth;
    }

    @Override
    public int yVoxelCount() {
        return vHeight;
    }

    @Override
    public int zVoxelCount() {
        return vDepth;
    }

    @Override
    public void tearDown() {

        if (positionAndValueTexture != null) {
            positionAndValueTexture.tearDown();
        }
        if (normalTexture != null) {
            normalTexture.tearDown();
        }

        if (tmpUniformGrid != null) {
            tmpUniformGrid.tearDown();
        }

    }
}
