package me.bokov.bsc.surfaceviewer.mesh.mccpu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.bokov.bsc.surfaceviewer.mesh.SDFMesh;
import me.bokov.bsc.surfaceviewer.mesh.SDFMeshGenerator;
import me.bokov.bsc.surfaceviewer.render.TriangleMesh;
import me.bokov.bsc.surfaceviewer.render.TriangleMesh.Face;
import me.bokov.bsc.surfaceviewer.voxelization.Corner;
import me.bokov.bsc.surfaceviewer.voxelization.SDFVoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MarchingCubes implements SDFMeshGenerator {

    private static final float EPSILON = 0.001f;
    private static final Vector4f COLOR = new Vector4f(0.2f, 0.6f, 0.95f, 1f);
    private final float isoLevel;

    public MarchingCubes(float isoLevel) {
        this.isoLevel = isoLevel;
    }

    private Vertex interpolate(Corner a, Corner b, float reference) {
        if (Math.abs(reference - a.getValue()) < EPSILON) {
            return new Vertex(a.getPoint(), a.getNormal(), COLOR);
        }
        if (Math.abs(reference - b.getValue()) < EPSILON) {
            return new Vertex(b.getPoint(), b.getNormal(), COLOR);
        }
        if (Math.abs(a.getValue() - b.getValue()) < EPSILON) {
            return new Vertex(a.getPoint(), a.getNormal(), COLOR);
        }

        float alpha = Math.abs((reference - a.getValue()) / (b.getValue() - a.getValue()));
        return new Vertex(
                new Vector3f(a.getPoint()).lerp(b.getPoint(), alpha),
                new Vector3f(a.getNormal()).lerp(b.getNormal(), alpha),
                COLOR
        );
    }

    @Override
    public SDFMesh generate(SDFVoxelStorage voxelStorage) {

        List<Face> generatedTriangles = new ArrayList<>();

        Iterator<Voxel> voxelIterator = voxelStorage.voxelIterator();
        final Vertex[] vertices = new Vertex[12];

        while (voxelIterator.hasNext()) {

            final Voxel voxel = voxelIterator.next();

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
                continue;
            }

            vertices[0] = interpolate(voxel.getC000(), voxel.getC100(), isoLevel);
            vertices[1] = interpolate(voxel.getC100(), voxel.getC101(), isoLevel);
            vertices[2] = interpolate(voxel.getC101(), voxel.getC001(), isoLevel);
            vertices[3] = interpolate(voxel.getC001(), voxel.getC000(), isoLevel);

            vertices[4] = interpolate(voxel.getC010(), voxel.getC110(), isoLevel);
            vertices[5] = interpolate(voxel.getC110(), voxel.getC111(), isoLevel);
            vertices[6] = interpolate(voxel.getC111(), voxel.getC011(), isoLevel);
            vertices[7] = interpolate(voxel.getC011(), voxel.getC010(), isoLevel);

            vertices[8] = interpolate(voxel.getC000(), voxel.getC010(), isoLevel);
            vertices[9] = interpolate(voxel.getC100(), voxel.getC110(), isoLevel);
            vertices[10] = interpolate(voxel.getC101(), voxel.getC111(), isoLevel);
            vertices[11] = interpolate(voxel.getC001(), voxel.getC011(), isoLevel);

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

        return new SDFMesh()
                .attachVoxelStorage(voxelStorage)
                .attachDrawable(TriangleMesh.create(generatedTriangles));
    }

    private final class Vertex {

        private final Vector3f pos;
        private final Vector3f norm;
        private final Vector4f col;

        private Vertex(Vector3f pos, Vector3f norm, Vector4f col) {
            this.pos = pos;
            this.norm = norm;
            this.col = col;
        }
    }
}
