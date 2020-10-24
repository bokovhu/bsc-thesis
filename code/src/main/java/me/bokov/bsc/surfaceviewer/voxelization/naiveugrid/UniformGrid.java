package me.bokov.bsc.surfaceviewer.voxelization.naiveugrid;

import java.util.Arrays;
import java.util.Iterator;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.voxelization.SDFVoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class UniformGrid implements SDFVoxelStorage {

    private final int width, height, depth;
    private MeshTransform transform;
    private final Voxel[] voxels;

    public UniformGrid(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.transform = new MeshTransform(
                new Vector3f(0f, 0f, 0f),
                new Quaternionf(),
                new Vector3f(
                        1f, 1f, 1f
                )
        );
        this.voxels = new Voxel[width * height * depth];
    }

    public UniformGrid applyTransform(MeshTransform transform) {
        this.transform = transform;
        return this;
    }

    public UniformGrid putVoxel(int x, int y, int z, Voxel v) {
        final int ix = Math.max(0, Math.min(width - 1, x));
        final int iy = Math.max(0, Math.min(height - 1, y));
        final int iz = Math.max(0, Math.min(depth - 1, z));

        voxels[iz * width * height + iy * width + ix] = v;

        return this;
    }

    public Vector3f localToGlobal(Vector3f local) {
        return this.transform.M().transformPosition(
                new Vector3f(local).mul(1f / (float) width)
        );
    }

    public Vector3f globalToLocal(Vector3f global) {
        return this.transform.Minv().transformPosition(
                new Vector3f(global).mul((float) width)
        );
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
