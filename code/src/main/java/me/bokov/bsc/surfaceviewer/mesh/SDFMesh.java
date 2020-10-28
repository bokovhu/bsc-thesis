package me.bokov.bsc.surfaceviewer.mesh;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;

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
