package me.bokov.bsc.surfaceviewer.mesh.mccpu;

import me.bokov.bsc.surfaceviewer.mesh.MeshGenerator;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.Drawables;
import me.bokov.bsc.surfaceviewer.render.Drawables.Face;
import me.bokov.bsc.surfaceviewer.util.MetricsLogger;
import me.bokov.bsc.surfaceviewer.voxelization.Corner;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

public class MarchingCubes implements MeshGenerator {

    private static final float EPSILON = 0.001f;
    private static final Vector4f COLOR = new Vector4f(0.2f, 0.6f, 0.95f, 1f);
    private final float isoLevel;

    public MarchingCubes(float isoLevel) {
        this.isoLevel = isoLevel;
    }

    private Vertex interpolate(Vertex out, Corner a, Corner b, float reference) {
        if (Math.abs(reference - a.getValue()) < EPSILON) {
            out.set(a.getPoint(), a.getNormal(), COLOR);
            return out;
        }
        if (Math.abs(reference - b.getValue()) < EPSILON) {
            out.set(b.getPoint(), b.getNormal(), COLOR);
            return out;
        }
        if (Math.abs(a.getValue() - b.getValue()) < EPSILON) {
            out.set(a.getPoint(), a.getNormal(), COLOR);
            return out;
        }

        float alpha = Math.abs((reference - a.getValue()) / (b.getValue() - a.getValue()));
        out.set(
                new Vector3f(a.getPoint()).lerp(b.getPoint(), alpha),
                new Vector3f(a.getNormal()).lerp(b.getNormal(), alpha),
                COLOR
        );
        return out;
    }

    @Override
    public Drawable generate(VoxelStorage voxelStorage) {

        List<Face> generatedTriangles = new ArrayList<>();
        final long startTime = System.currentTimeMillis();
        int numProcessedVoxels = 0;
        int numEmptyVoxels = 0;

        Iterator<Voxel> voxelIterator = voxelStorage.voxelIterator();
        final Vertex[] vertices = new Vertex[12];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vertex();
        }

        while (voxelIterator.hasNext()) {

            final Voxel voxel = voxelIterator.next();
            ++numProcessedVoxels;

            int cubeIndex = 0;

            if (voxel.getC000().getValue() < isoLevel) {
                cubeIndex |= 1;
            }
            if (voxel.getC100().getValue() < isoLevel) {
                cubeIndex |= 2;
            }
            if (voxel.getC101().getValue() < isoLevel) {
                cubeIndex |= 4;
            }
            if (voxel.getC001().getValue() < isoLevel) {
                cubeIndex |= 8;
            }

            if (voxel.getC010().getValue() < isoLevel) {
                cubeIndex |= 16;
            }
            if (voxel.getC110().getValue() < isoLevel) {
                cubeIndex |= 32;
            }
            if (voxel.getC111().getValue() < isoLevel) {
                cubeIndex |= 64;
            }
            if (voxel.getC011().getValue() < isoLevel) {
                cubeIndex |= 128;
            }

            if (cubeIndex == 0 || cubeIndex == 255) {
                ++numEmptyVoxels;
                continue;
            }

            interpolate(vertices[0], voxel.getC000(), voxel.getC100(), isoLevel);
            interpolate(vertices[1], voxel.getC100(), voxel.getC101(), isoLevel);
            interpolate(vertices[2], voxel.getC101(), voxel.getC001(), isoLevel);
            interpolate(vertices[3], voxel.getC001(), voxel.getC000(), isoLevel);

            interpolate(vertices[4], voxel.getC010(), voxel.getC110(), isoLevel);
            interpolate(vertices[5], voxel.getC110(), voxel.getC111(), isoLevel);
            interpolate(vertices[6], voxel.getC111(), voxel.getC011(), isoLevel);
            interpolate(vertices[7], voxel.getC011(), voxel.getC010(), isoLevel);

            interpolate(vertices[8], voxel.getC000(), voxel.getC010(), isoLevel);
            interpolate(vertices[9], voxel.getC100(), voxel.getC110(), isoLevel);
            interpolate(vertices[10], voxel.getC101(), voxel.getC111(), isoLevel);
            interpolate(vertices[11], voxel.getC001(), voxel.getC011(), isoLevel);

            for (int i = 0; TriangleTable.TRIANGLE_TABLE[cubeIndex][i] != -1; i += 3) {

                Vertex v1 = vertices[TriangleTable.TRIANGLE_TABLE[cubeIndex][i]];
                Vertex v2 = vertices[TriangleTable.TRIANGLE_TABLE[cubeIndex][i + 1]];
                Vertex v3 = vertices[TriangleTable.TRIANGLE_TABLE[cubeIndex][i + 2]];

                generatedTriangles.add(
                        new Face(
                                v1.pos, v2.pos, v3.pos,
                                v1.norm, v2.norm, v3.norm,
                                v1.col, v2.col, v3.col
                        )
                );

            }

        }

        final long endTime = System.currentTimeMillis();
        MetricsLogger.logMetrics(
                "Marching cubes",
                Map.of(
                        "Runtime", (endTime - startTime) + " ms",
                        "Number of voxels processed", numProcessedVoxels,
                        "Number of empty voxels", numEmptyVoxels,
                        "Number of generated triangles", generatedTriangles.size()
                )
        );

        return Drawables.create(generatedTriangles);
    }

    private final class Vertex {

        private final Vector3f pos = new Vector3f();
        private final Vector3f norm = new Vector3f();
        private final Vector4f col = new Vector4f();

        public Vertex set(Vector3f pos, Vector3f norm, Vector4f col) {
            this.pos.set(pos);
            this.norm.set(norm);
            this.col.set(col);
            return this;
        }

    }
}
