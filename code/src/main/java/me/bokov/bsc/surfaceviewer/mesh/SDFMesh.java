package me.bokov.bsc.surfaceviewer.mesh;

import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;

import java.util.*;
import java.util.stream.*;

/**
 * Should be able to use any Drawable instance instead of this
 */
@Deprecated
public class SDFMesh {

    private Drawable drawable;
    private VoxelStorage voxelStorage;

    public SDFMesh attachVoxelStorage(VoxelStorage storage) {
        this.voxelStorage = storage;
        return this;
    }

    public SDFMesh attachDrawable(Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    public List<Voxel> collectVoxels() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                voxelStorage.voxelIterator(),
                Spliterator.ORDERED
        ), false)
                .collect(Collectors.toList());
    }

    public void draw() {

        drawable.draw();

    }

    public void tearDown() {

        drawable.tearDown();
        voxelStorage.tearDown();

    }

}
