package me.bokov.bsc.surfaceviewer.voxelization;

import lombok.Getter;

import java.nio.FloatBuffer;

@Getter
public class VoxelData {

    private final FloatBuffer positionAndValueBuffer;
    private final FloatBuffer normalBuffer;
    private final FloatBuffer colorAndShininessBuffer;
    private final int width, height, depth;

    public VoxelData(
            FloatBuffer positionAndValueBuffer,
            FloatBuffer normalBuffer,
            FloatBuffer colorAndShininessBuffer,
            int width,
            int height,
            int depth
    ) {
        this.positionAndValueBuffer = positionAndValueBuffer;
        this.normalBuffer = normalBuffer;
        this.colorAndShininessBuffer = colorAndShininessBuffer;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
}
