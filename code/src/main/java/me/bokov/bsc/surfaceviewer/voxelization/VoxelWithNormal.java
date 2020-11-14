package me.bokov.bsc.surfaceviewer.voxelization;

import java.nio.FloatBuffer;

/** @formatter:off */
public interface VoxelWithNormal extends Voxel {

    float nx000();
    float ny000();
    float nz000();

    float nx001();
    float ny001();
    float nz001();

    float nx010();
    float ny010();
    float nz010();

    float nx011();
    float ny011();
    float nz011();

    float nx100();
    float ny100();
    float nz100();

    float nx101();
    float ny101();
    float nz101();

    float nx110();
    float ny110();
    float nz110();

    float nx111();
    float ny111();
    float nz111();

    @Override
    default void putToInterleaved(FloatBuffer out) {
        out.put(x000()).put(y000()).put(z000()).put(v000()).put(nx000()).put(ny000()).put(nz000())
                .put(x001()).put(y001()).put(z001()).put(v001()).put(nx001()).put(ny001()).put(nz001())
                .put(x010()).put(y010()).put(z010()).put(v010()).put(nx010()).put(ny010()).put(nz010())
                .put(x011()).put(y011()).put(z011()).put(v011()).put(nx011()).put(ny011()).put(nz011())

                .put(x100()).put(y100()).put(z100()).put(v100()).put(nx100()).put(ny100()).put(nz100())
                .put(x101()).put(y101()).put(z101()).put(v101()).put(nx101()).put(ny101()).put(nz101())
                .put(x110()).put(y110()).put(z110()).put(v110()).put(nx110()).put(ny110()).put(nz110())
                .put(x111()).put(y111()).put(z111()).put(v111()).put(nx111()).put(ny111()).put(nz111());
    }

}
