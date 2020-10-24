package me.bokov.bsc.surfaceviewer.mesh;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.voxelization.SDFVoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;

public class SDFMesh {

    private Drawable drawable;
    private SDFVoxelStorage voxelStorage;

    public SDFMesh attachVoxelStorage(SDFVoxelStorage storage) {
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
