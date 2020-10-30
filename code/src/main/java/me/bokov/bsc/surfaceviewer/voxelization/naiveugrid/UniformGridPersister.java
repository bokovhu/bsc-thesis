package me.bokov.bsc.surfaceviewer.voxelization.naiveugrid;

import me.bokov.bsc.surfaceviewer.voxelization.VoxelStoragePersister;
import org.lwjgl.BufferUtils;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public class UniformGridPersister implements VoxelStoragePersister<UniformGrid> {

    @Override
    public void persist(UniformGrid storage, OutputStream output) {

        ByteBuffer header = BufferUtils.createByteBuffer(
                4 + 4 + 4
                        + (
                        Float.BYTES * (3 + 3 + 1) * (
                                (storage.getWidth() + 1) *
                                        (storage.getHeight() + 1) *
                                        (storage.getDepth() + 1)
                        )
                )
        );

        header.putInt(storage.getWidth())
                .putInt(storage.getHeight())
                .putInt(storage.getDepth());


    }

}
