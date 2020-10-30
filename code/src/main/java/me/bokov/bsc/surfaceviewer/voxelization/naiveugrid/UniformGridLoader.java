package me.bokov.bsc.surfaceviewer.voxelization.naiveugrid;

import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorageLoader;

import java.io.InputStream;
import java.io.ObjectInputStream;

public class UniformGridLoader implements VoxelStorageLoader<UniformGrid> {

    @Override
    public UniformGrid load(InputStream input) {

        try (ObjectInputStream objectInput = new ObjectInputStream(input)) {

            final Object object = objectInput.readObject();
            if (object instanceof UniformGrid) {
                return (UniformGrid) object;
            }
            throw new IllegalStateException(
                    "Did not receive a UniformGrid object from the input stream.");

        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }

    }
}
