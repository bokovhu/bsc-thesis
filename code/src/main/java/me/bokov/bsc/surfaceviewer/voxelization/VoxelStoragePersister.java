package me.bokov.bsc.surfaceviewer.voxelization;

import java.io.OutputStream;

public interface VoxelStoragePersister <TStorage extends VoxelStorage> {

    void persist(TStorage storage, OutputStream output);

}
