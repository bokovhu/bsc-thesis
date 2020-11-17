package me.bokov.bsc.surfaceviewer.voxelization;

import lombok.EqualsAndHashCode;

import java.nio.FloatBuffer;

@EqualsAndHashCode(callSuper = false)
public class GridVoxel extends BaseVoxel {

    @EqualsAndHashCode.Exclude
    private final Grid grid;

    private final int i000, i001, i010, i011, i100, i101, i110, i111;

    public GridVoxel(int x, int y, int z, FloatBuffer buffer, Grid grid) {
        super(grid.xyzToIndex(x, y, z), buffer);
        this.grid = grid;

        i000 = grid.xyzToIndex(x, y, z);
        i001 = grid.xyzToIndex(x, y, z + 1);
        i010 = grid.xyzToIndex(x, y + 1, z);
        i011 = grid.xyzToIndex(x, y + 1, z + 1);
        i100 = grid.xyzToIndex(x + 1, y, z);
        i101 = grid.xyzToIndex(x + 1, y, z + 1);
        i110 = grid.xyzToIndex(x + 1, y + 1, z);
        i111 = grid.xyzToIndex(x + 1, y + 1, z + 1);
    }

    public GridVoxel(int x, int y, int z, FloatBuffer buffer, FloatBuffer normalBuffer, Grid grid) {
        super(grid.xyzToIndex(x, y, z), buffer, normalBuffer);
        this.grid = grid;

        i000 = grid.xyzToIndex(x, y, z);
        i001 = grid.xyzToIndex(x, y, z + 1);
        i010 = grid.xyzToIndex(x, y + 1, z);
        i011 = grid.xyzToIndex(x, y + 1, z + 1);
        i100 = grid.xyzToIndex(x + 1, y, z);
        i101 = grid.xyzToIndex(x + 1, y, z + 1);
        i110 = grid.xyzToIndex(x + 1, y + 1, z);
        i111 = grid.xyzToIndex(x + 1, y + 1, z + 1);
    }

    public GridVoxel(
            int x,
            int y,
            int z,
            FloatBuffer buffer,
            FloatBuffer normalBuffer,
            FloatBuffer colorBuffer,
            Grid grid
    ) {
        super(grid.xyzToIndex(x, y, z), buffer, normalBuffer, colorBuffer);
        this.grid = grid;

        i000 = grid.xyzToIndex(x, y, z);
        i001 = grid.xyzToIndex(x, y, z + 1);
        i010 = grid.xyzToIndex(x, y + 1, z);
        i011 = grid.xyzToIndex(x, y + 1, z + 1);
        i100 = grid.xyzToIndex(x + 1, y, z);
        i101 = grid.xyzToIndex(x + 1, y, z + 1);
        i110 = grid.xyzToIndex(x + 1, y + 1, z);
        i111 = grid.xyzToIndex(x + 1, y + 1, z + 1);
    }

    @Override
    public float nx000() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i000 + 0);
    }

    @Override
    public float ny000() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i000 + 1);
    }

    @Override
    public float nz000() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i000 + 2);
    }

    @Override
    public float nx001() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i001 + 0);
    }

    @Override
    public float ny001() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i001 + 1);
    }

    @Override
    public float nz001() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i001 + 2);
    }

    @Override
    public float nx010() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i010 + 0);
    }

    @Override
    public float ny010() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i010 + 1);
    }

    @Override
    public float nz010() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i010 + 2);
    }

    @Override
    public float nx011() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i011 + 0);
    }

    @Override
    public float ny011() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i011 + 1);
    }

    @Override
    public float nz011() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i011 + 2);
    }

    @Override
    public float nx100() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i100 + 0);
    }

    @Override
    public float ny100() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i100 + 1);
    }

    @Override
    public float nz100() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i100 + 2);
    }

    @Override
    public float nx101() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i101 + 0);
    }

    @Override
    public float ny101() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i101 + 1);
    }

    @Override
    public float nz101() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i101 + 2);
    }

    @Override
    public float nx110() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i110 + 0);
    }

    @Override
    public float ny110() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i110 + 1);
    }

    @Override
    public float nz110() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i110 + 2);
    }

    @Override
    public float nx111() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i111 + 0);
    }

    @Override
    public float ny111() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i111 + 1);
    }

    @Override
    public float nz111() {
        if (normalBuffer == null) { return 0.0f; }
        return normalBuffer.get(3 * i111 + 2);
    }

    @Override
    public float x000() {
        return buffer.get(4 * i000 + 0);
    }

    @Override
    public float y000() {
        return buffer.get(4 * i000 + 1);
    }

    @Override
    public float z000() {
        return buffer.get(4 * i000 + 2);
    }

    @Override
    public float v000() {
        return buffer.get(4 * i000 + 3);
    }

    @Override
    public float x001() {
        return buffer.get(4 * i001 + 0);
    }

    @Override
    public float y001() {
        return buffer.get(4 * i001 + 1);
    }

    @Override
    public float z001() {
        return buffer.get(4 * i001 + 2);
    }

    @Override
    public float v001() {
        return buffer.get(4 * i001 + 3);
    }

    @Override
    public float x010() {
        return buffer.get(4 * i010 + 0);
    }

    @Override
    public float y010() {
        return buffer.get(4 * i010 + 1);
    }

    @Override
    public float z010() {
        return buffer.get(4 * i010 + 2);
    }

    @Override
    public float v010() {
        return buffer.get(4 * i010 + 3);
    }

    @Override
    public float x011() {
        return buffer.get(4 * i011 + 0);
    }

    @Override
    public float y011() {
        return buffer.get(4 * i011 + 1);
    }

    @Override
    public float z011() {
        return buffer.get(4 * i011 + 2);
    }

    @Override
    public float v011() {
        return buffer.get(4 * i011 + 3);
    }

    @Override
    public float x100() {
        return buffer.get(4 * i100 + 0);
    }

    @Override
    public float y100() {
        return buffer.get(4 * i100 + 1);
    }

    @Override
    public float z100() {
        return buffer.get(4 * i100 + 2);
    }

    @Override
    public float v100() {
        return buffer.get(4 * i100 + 3);
    }

    @Override
    public float x101() {
        return buffer.get(4 * i101 + 0);
    }

    @Override
    public float y101() {
        return buffer.get(4 * i101 + 1);
    }

    @Override
    public float z101() {
        return buffer.get(4 * i101 + 2);
    }

    @Override
    public float v101() {
        return buffer.get(4 * i101 + 3);
    }

    @Override
    public float x110() {
        return buffer.get(4 * i110 + 0);
    }

    @Override
    public float y110() {
        return buffer.get(4 * i110 + 1);
    }

    @Override
    public float z110() {
        return buffer.get(4 * i110 + 2);
    }

    @Override
    public float v110() {
        return buffer.get(4 * i110 + 3);
    }

    @Override
    public float x111() {
        return buffer.get(4 * i111 + 0);
    }

    @Override
    public float y111() {
        return buffer.get(4 * i111 + 1);
    }

    @Override
    public float z111() {
        return buffer.get(4 * i111 + 2);
    }

    @Override
    public float v111() {
        return buffer.get(4 * i111 + 3);
    }

    @Override
    public float r000() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i000 + 0);
    }

    @Override
    public float g000() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i000 + 1);
    }

    @Override
    public float b000() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i000 + 2);
    }

    @Override
    public float s000() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i000 + 3);
    }

    @Override
    public float r001() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i001 + 0);
    }

    @Override
    public float g001() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i001 + 1);
    }

    @Override
    public float b001() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i001 + 2);
    }

    @Override
    public float s001() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i001 + 3);
    }

    @Override
    public float r010() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i010 + 0);
    }

    @Override
    public float g010() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i010 + 1);
    }

    @Override
    public float b010() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i010 + 2);
    }

    @Override
    public float s010() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i010 + 3);
    }

    @Override
    public float r011() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i011 + 0);
    }

    @Override
    public float g011() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i011 + 1);
    }

    @Override
    public float b011() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i011 + 2);
    }

    @Override
    public float s011() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i011 + 3);
    }

    @Override
    public float r100() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i100 + 0);
    }

    @Override
    public float g100() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i100 + 1);
    }

    @Override
    public float b100() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i100 + 2);
    }

    @Override
    public float s100() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i100 + 3);
    }

    @Override
    public float r101() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i101 + 0);
    }

    @Override
    public float g101() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i101 + 1);
    }

    @Override
    public float b101() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i101 + 2);
    }

    @Override
    public float s101() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i101 + 3);
    }

    @Override
    public float r110() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i110 + 0);
    }

    @Override
    public float g110() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i110 + 1);
    }

    @Override
    public float b110() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i110 + 2);
    }

    @Override
    public float s110() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i110 + 3);
    }

    @Override
    public float r111() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i111 + 0);
    }

    @Override
    public float g111() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i111 + 1);
    }

    @Override
    public float b111() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i111 + 2);
    }

    @Override
    public float s111() {
        if(colorBuffer == null) return 0.0f;
        return colorBuffer.get(4 * i111 + 3);
    }

}
