package me.bokov.bsc.surfaceviewer.mesh;

import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;

public interface MeshGenerator {

    Drawable generate(World world, VoxelStorage voxelStorage);

    default void tearDown() {
    }

}
