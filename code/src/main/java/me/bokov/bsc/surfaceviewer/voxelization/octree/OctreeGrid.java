package me.bokov.bsc.surfaceviewer.voxelization.octree;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class OctreeGrid implements VoxelStorage {

    private final int maxDivisions;
    private final OctreeNode root = new OctreeNode();
    private final transient Vector3f tmpTransf = new Vector3f();
    private MeshTransform transform;

    public OctreeGrid(int maxDivisions) {
        this.maxDivisions = maxDivisions;
        this.transform = new MeshTransform(
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0f, 1f, 0f),
                0f,
                1f
        );
    }

    @Override
    public Iterator<Voxel> voxelIterator() {

        List<Voxel> collected = new ArrayList<>();

        Deque<OctreeNode> bfsQueue = new ArrayDeque<>();
        bfsQueue.addLast(root);

        while (!bfsQueue.isEmpty()) {

            OctreeNode curr = bfsQueue.removeFirst();

            if (curr.isLeaf()) {
                collected.add(0, curr.getVoxel());
            }

            if (curr.getN111() != null) { bfsQueue.addLast(curr.getN111()); }
            if (curr.getN110() != null) { bfsQueue.addLast(curr.getN110()); }
            if (curr.getN101() != null) { bfsQueue.addLast(curr.getN101()); }
            if (curr.getN100() != null) { bfsQueue.addLast(curr.getN100()); }

            if (curr.getN011() != null) { bfsQueue.addLast(curr.getN011()); }
            if (curr.getN010() != null) { bfsQueue.addLast(curr.getN010()); }
            if (curr.getN001() != null) { bfsQueue.addLast(curr.getN001()); }
            if (curr.getN000() != null) { bfsQueue.addLast(curr.getN000()); }
        }

        return collected.iterator();
    }

    @Override
    public Voxel closestVoxel(Vector3f p) {
        final OctreeNode node = root.findNode(p);
        return node != null ? node.getVoxel() : null;
    }

    public OctreeGrid applyTransform(MeshTransform transform) {
        this.transform = transform;
        return this;
    }


    @Override
    public Vector3f localToGlobal(Vector3f local) {
        return this.transform.M().transformPosition(
                tmpTransf.set(local)
        );
    }

    @Override
    public Vector3f globalToLocal(Vector3f global) {
        return this.transform.Minv().transformPosition(
                tmpTransf.set(global)
        );
    }


    @Override
    public Vector3f globalToLocal(Vector3f in, Vector3f out) {
        this.transform.Minv().transformPosition(
                tmpTransf.set(in),
                out
        );
        return out;
    }

    @Override
    public Vector3f localToGlobal(Vector3f in, Vector3f out) {
        this.transform.M().transformPosition(
                tmpTransf.set(in),
                out
        );
        return out;
    }

    public OctreeNode root() {
        return root;
    }

}
