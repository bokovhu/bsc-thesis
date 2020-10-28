package me.bokov.bsc.surfaceviewer.voxelization;

import java.util.Iterator;
import org.joml.Vector3f;

public interface VoxelStorage {

    Iterator<Voxel> voxelIterator();

    Voxel closestVoxel(Vector3f p);

    default Vector3f globalToLocal(Vector3f global) {
        return new Vector3f(global);
    }

    default Vector3f globalToLocal(Vector3f in, Vector3f out) {
        out.set(in);
        return out;
    }

    default Vector3f localToGlobal(Vector3f local) {
        return new Vector3f(local);
    }

    default Vector3f localToGlobal(Vector3f in, Vector3f out) {
        out.set(in);
        return out;
    }

    default void tearDown() {
    }

}
