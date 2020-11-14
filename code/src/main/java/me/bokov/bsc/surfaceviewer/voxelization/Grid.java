package me.bokov.bsc.surfaceviewer.voxelization;

public interface Grid {

    int width();

    int height();

    int depth();

    default int xyzToIndex(int x, int y, int z) {
        return height() * width() * Math.min(depth() - 1, Math.max(0, z))
                + width() * (Math.min(height() - 1, Math.max(0, y)))
                + Math.min(width() - 1, Math.max(0, x));
    }

}
