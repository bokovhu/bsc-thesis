package me.bokov.bsc.surfaceviewer.voxelization;

import java.io.Serializable;
import java.nio.FloatBuffer;

/** @formatter:off
 * Voxels are numbered like this:
 *
 *
 *      001 / ------------ / 101
 *         /|             /|
 *        / |            / |
 *       /  |           /  |
 *      /   |          /   |
 * 000 / ------------ /100 |
 *     |011 / --------|--- / 111
 *     |   /          |   /
 *     |  /           |  /
 *     | /            | /
 *     |/             |/
 *     / ------------ /
 *    010            110
 *
 * Corners in voxel buffers are stored in lexicographical order:
 *
 * 1: 000
 * 2: 001
 * 3: 010
 * 4: 011
 * 5: 100
 * 6: 101
 * 7: 110
 * 8: 111
 *
 * In the Marching Cubes algorithm, the numbering is as follows:
 *
 *       8  / ------------ /  7
 *         /|             /|
 *        / |            / |
 *       /  |           /  |
 *      /   |          /   |
 *  5  / ------------ / 6  |
 *     | 4  / --------|--- /  3
 *     |   /          |   /
 *     |  /           |  /
 *     | /            | /
 *     |/             |/
 *     / ------------ /
 *   1                2
 *
 * The indexing of the edges is the same in both the marching cubes, and the domain interpretation:
 *
 * 1: 000 - 100
 * 2: 100 - 101
 * 3: 101 - 001
 * 4: 001 - 000
 * 5: 010 - 110
 * 6: 110 - 111
 * 7: 111 - 011
 * 8: 011 - 010
 * 9: 000 - 010
 * 10: 100 - 110
 * 11: 101 - 111
 * 12: 001 - 011
 *
 * Indexing of the faces is:
 *
 * 1: Front
 * 2: Right
 * 3: Back
 * 4: Left
 * 5: Top
 * 6: Bottom
 *
 */
public interface Voxel extends Serializable {

    int I000 = 0;
    int I001 = 1;
    int I010 = 2;
    int I011 = 3;
    int I100 = 4;
    int I101 = 5;
    int I110 = 6;
    int I111 = 7;

    float x000();
    float y000();
    float z000();
    float v000();

    float x001();
    float y001();
    float z001();
    float v001();

    float x010();
    float y010();
    float z010();
    float v010();

    float x011();
    float y011();
    float z011();
    float v011();

    float x100();
    float y100();
    float z100();
    float v100();

    float x101();
    float y101();
    float z101();
    float v101();

    float x110();
    float y110();
    float z110();
    float v110();

    float x111();
    float y111();
    float z111();
    float v111();

    default void putToInterleaved(FloatBuffer out) {
        out.put(x000()).put(y000()).put(z000()).put(v000())
                .put(x001()).put(y001()).put(z001()).put(v001())
                .put(x010()).put(y010()).put(z010()).put(v010())
                .put(x011()).put(y011()).put(z011()).put(v011())

                .put(x100()).put(y100()).put(z100()).put(v100())
                .put(x101()).put(y101()).put(z101()).put(v101())
                .put(x110()).put(y110()).put(z110()).put(v110())
                .put(x111()).put(y111()).put(z111()).put(v111());
    }

}
