package me.bokov.bsc.surfaceviewer.voxelization;

import java.io.InputStream;

public interface VoxelStorageLoader<TStorage extends VoxelStorage> {

    TStorage load(InputStream input);

}
