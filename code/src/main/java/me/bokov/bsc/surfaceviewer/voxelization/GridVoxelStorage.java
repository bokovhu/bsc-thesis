package me.bokov.bsc.surfaceviewer.voxelization;

public interface GridVoxelStorage extends VoxelStorage, Grid {

    GridVoxel at(int index);
    GridVoxel at(int x, int y, int z);

    int xVoxelCount();
    int yVoxelCount();
    int zVoxelCount();

}
