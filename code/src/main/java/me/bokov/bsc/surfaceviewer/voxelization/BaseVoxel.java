package me.bokov.bsc.surfaceviewer.voxelization;

import lombok.EqualsAndHashCode;

import java.nio.FloatBuffer;

@EqualsAndHashCode
public class BaseVoxel implements Voxel, VoxelWithNormal, IndexedVoxel {

    private final int index;
    @EqualsAndHashCode.Exclude
    protected final FloatBuffer buffer;
    @EqualsAndHashCode.Exclude
    protected final FloatBuffer normalBuffer;

    public BaseVoxel(int index, FloatBuffer buffer) {
        this.index = index;
        this.buffer = buffer;
        this.normalBuffer = null;
    }

    public BaseVoxel(int index, FloatBuffer buffer, FloatBuffer normalBuffer) {
        this.index = index;
        this.buffer = buffer;
        this.normalBuffer = normalBuffer;
    }

    @Override
    public int baseIndex() {
        return index;
    }

    @Override
    public float nx000() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 0) + 0);
    }

    @Override
    public float ny000() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 0) + 1);
    }

    @Override
    public float nz000() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 0) + 2);
    }

    @Override
    public float nx001() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 1) + 0);
    }

    @Override
    public float ny001() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 1) + 1);
    }

    @Override
    public float nz001() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 1) + 2);
    }

    @Override
    public float nx010() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 2) + 0);
    }

    @Override
    public float ny010() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 2) + 1);
    }

    @Override
    public float nz010() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 2) + 2);
    }

    @Override
    public float nx011() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 3) + 0);
    }

    @Override
    public float ny011() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 3) + 1);
    }

    @Override
    public float nz011() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 3) + 2);
    }

    @Override
    public float nx100() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 4) + 0);
    }

    @Override
    public float ny100() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 4) + 1);
    }

    @Override
    public float nz100() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 4) + 2);
    }

    @Override
    public float nx101() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 5) + 0);
    }

    @Override
    public float ny101() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 5) + 1);
    }

    @Override
    public float nz101() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 5) + 2);
    }

    @Override
    public float nx110() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 6) + 0);
    }

    @Override
    public float ny110() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 6) + 1);
    }

    @Override
    public float nz110() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 6) + 2);
    }

    @Override
    public float nx111() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 7) + 0);
    }

    @Override
    public float ny111() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 7) + 1);
    }

    @Override
    public float nz111() {
        if (normalBuffer == null) return 0.0f;
        return normalBuffer.get(3 * (index + 7) + 2);
    }

    /**
     * You can quickly generate code like below using the following JS snippet:
     *
     * var c = ["000", "001", "010", "011", "100", "101", "110", "111"];
     * var v = ["x", "y", "z", "v"]
     * console.log(c.map((corn, ci) => v.map((va, vi) => `@Override\npublic float ${va}${corn}() {\n    return buffer.get(4 * (index + ${ci}) + ${vi});\n}`).join("\n\n")).join("\n\n"))
     */

    @Override
    public float x000() {
        return buffer.get(4 * (index + 0) + 0);
    }

    @Override
    public float y000() {
        return buffer.get(4 * (index + 0) + 1);
    }

    @Override
    public float z000() {
        return buffer.get(4 * (index + 0) + 2);
    }

    @Override
    public float v000() {
        return buffer.get(4 * (index + 0) + 3);
    }

    @Override
    public float x001() {
        return buffer.get(4 * (index + 1) + 0);
    }

    @Override
    public float y001() {
        return buffer.get(4 * (index + 1) + 1);
    }

    @Override
    public float z001() {
        return buffer.get(4 * (index + 1) + 2);
    }

    @Override
    public float v001() {
        return buffer.get(4 * (index + 1) + 3);
    }

    @Override
    public float x010() {
        return buffer.get(4 * (index + 2) + 0);
    }

    @Override
    public float y010() {
        return buffer.get(4 * (index + 2) + 1);
    }

    @Override
    public float z010() {
        return buffer.get(4 * (index + 2) + 2);
    }

    @Override
    public float v010() {
        return buffer.get(4 * (index + 2) + 3);
    }

    @Override
    public float x011() {
        return buffer.get(4 * (index + 3) + 0);
    }

    @Override
    public float y011() {
        return buffer.get(4 * (index + 3) + 1);
    }

    @Override
    public float z011() {
        return buffer.get(4 * (index + 3) + 2);
    }

    @Override
    public float v011() {
        return buffer.get(4 * (index + 3) + 3);
    }

    @Override
    public float x100() {
        return buffer.get(4 * (index + 4) + 0);
    }

    @Override
    public float y100() {
        return buffer.get(4 * (index + 4) + 1);
    }

    @Override
    public float z100() {
        return buffer.get(4 * (index + 4) + 2);
    }

    @Override
    public float v100() {
        return buffer.get(4 * (index + 4) + 3);
    }

    @Override
    public float x101() {
        return buffer.get(4 * (index + 5) + 0);
    }

    @Override
    public float y101() {
        return buffer.get(4 * (index + 5) + 1);
    }

    @Override
    public float z101() {
        return buffer.get(4 * (index + 5) + 2);
    }

    @Override
    public float v101() {
        return buffer.get(4 * (index + 5) + 3);
    }

    @Override
    public float x110() {
        return buffer.get(4 * (index + 6) + 0);
    }

    @Override
    public float y110() {
        return buffer.get(4 * (index + 6) + 1);
    }

    @Override
    public float z110() {
        return buffer.get(4 * (index + 6) + 2);
    }

    @Override
    public float v110() {
        return buffer.get(4 * (index + 6) + 3);
    }

    @Override
    public float x111() {
        return buffer.get(4 * (index + 7) + 0);
    }

    @Override
    public float y111() {
        return buffer.get(4 * (index + 7) + 1);
    }

    @Override
    public float z111() {
        return buffer.get(4 * (index + 7) + 2);
    }

    @Override
    public float v111() {
        return buffer.get(4 * (index + 7) + 3);
    }
}
