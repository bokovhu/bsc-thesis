package me.bokov.bsc.surfaceviewer.voxelization.naiveugrid;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

public class UniformGrid implements VoxelStorage, Serializable {

    private final int width, height, depth;
    private final Voxel[] voxels;
    private final transient Vector3f tmpTransf = new Vector3f();
    private MeshTransform transform;

    public UniformGrid(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.transform = new MeshTransform(
                new Vector3f(0f, 0f, 0f),
                new Quaternionf(),
                1f
        );
        this.voxels = new Voxel[width * height * depth];
    }

    public int idx(int x, int y, int z) {

        final int ix = Math.max(0, Math.min(width - 1, x));
        final int iy = Math.max(0, Math.min(height - 1, y));
        final int iz = Math.max(0, Math.min(depth - 1, z));

        return iz * width * height + iy * width + ix;
    }

    public UniformGrid applyTransform(MeshTransform transform) {
        this.transform = transform;
        return this;
    }

    public UniformGrid putVoxel(int x, int y, int z, Voxel v) {
        voxels[idx(x, y, z)] = v;

        return this;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public Vector3f localToGlobal(Vector3f local) {
        return this.transform.M().transformPosition(
                tmpTransf.set(local).mul(1f / (float) width)
        );
    }

    @Override
    public Vector3f globalToLocal(Vector3f global) {
        return this.transform.Minv().transformPosition(
                tmpTransf.set(global).mul((float) width)
        );
    }


    @Override
    public Vector3f globalToLocal(Vector3f in, Vector3f out) {
        this.transform.Minv().transformPosition(
                tmpTransf.set(in).mul((float) width),
                out
        );
        return out;
    }

    @Override
    public Vector3f localToGlobal(Vector3f in, Vector3f out) {
        this.transform.M().transformPosition(
                tmpTransf.set(in).mul(1f / (float) width),
                out
        );
        return out;
    }

    public Voxel at(int idx) {
        return voxels[idx];
    }

    @Override
    public Iterator<Voxel> voxelIterator() {
        return Arrays.stream(voxels).iterator();
    }

    @Override
    public Voxel closestVoxel(Vector3f p) {

        final Vector3f pTransformed = globalToLocal(p);

        final int ix = Math.max(0, Math.min(width - 1, (int) Math.floor(pTransformed.x)));
        final int iy = Math.max(0, Math.min(height - 1, (int) Math.floor(pTransformed.y)));
        final int iz = Math.max(0, Math.min(height - 1, (int) Math.floor(pTransformed.z)));

        return voxels[iz * width * height + iy * width + ix];
    }

}
