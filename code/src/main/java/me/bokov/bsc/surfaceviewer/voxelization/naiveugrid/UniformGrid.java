package me.bokov.bsc.surfaceviewer.voxelization.naiveugrid;

import me.bokov.bsc.surfaceviewer.voxelization.GridVoxel;
import me.bokov.bsc.surfaceviewer.voxelization.GridVoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import org.joml.Vector3f;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.*;

public class UniformGrid implements GridVoxelStorage, Serializable {

    private final int width, height, depth;
    private final int vWidth, vHeight, vDepth;
    private final GridVoxel[] voxels;
    private final FloatBuffer positionDistanceBuffer, normalBuffer;

    public UniformGrid(
            int width,
            int height,
            int depth,
            FloatBuffer positionDistanceBuffer,
            FloatBuffer normalBuffer
    ) {
        this.width = width;
        this.height = height;
        this.depth = depth;

        this.vWidth = width - 1;
        this.vHeight = height - 1;
        this.vDepth = depth - 1;

        this.positionDistanceBuffer = positionDistanceBuffer;
        this.normalBuffer = normalBuffer;

        this.voxels = new GridVoxel[vWidth * vHeight * vDepth];
        for (int z = 0; z < vDepth; z++) {
            for (int y = 0; y < vHeight; y++) {
                for (int x = 0; x < vWidth; x++) {
                    this.voxels[voxelXyzToIndex(x, y, z)] = new GridVoxel(
                            x, y, z,
                            positionDistanceBuffer,
                            normalBuffer,
                            this
                    );
                }
            }
        }
    }

    private int voxelXyzToIndex(int vx, int vy, int vz) {
        return Math.min(vDepth - 1, Math.max(0, vz)) * vWidth * vHeight
                + Math.min(vHeight - 1, Math.max(0, vy)) * vWidth
                + Math.min(vWidth - 1, Math.max(0, vx));
    }

    @Override
    public GridVoxel at(int idx) {
        return voxels[idx];
    }

    @Override
    public GridVoxel at(int x, int y, int z) {
        return voxels[voxelXyzToIndex(x, y, z)];
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
    public Iterator<Voxel> voxelIterator() {
        return Arrays.stream(voxels)
                .sequential()
                .map(Voxel.class::cast)
                .iterator();
    }

    @Override
    public Voxel closestVoxel(Vector3f p) {

        final Vector3f pTransformed = globalToLocal(p);

        return voxels[
                xyzToIndex(
                        (int) Math.floor(pTransformed.x),
                        (int) Math.floor(pTransformed.y),
                        (int) Math.floor(pTransformed.z)
                )
                ];
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
}
