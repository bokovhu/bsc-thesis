package me.bokov.bsc.surfaceviewer.mesh.mccpu;

import me.bokov.bsc.surfaceviewer.mesh.MeshGenerator;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.Drawables;
import me.bokov.bsc.surfaceviewer.render.Drawables.Face;
import me.bokov.bsc.surfaceviewer.util.MetricsLogger;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelWithNormal;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

public class MarchingCubes implements MeshGenerator {

    private static final float EPSILON = 0.0001f;
    private static final Vector4f COLOR = new Vector4f(1f, 1f, 1f, 1f);
    private final float isoLevel;

    private final Vector3f tmp1 = new Vector3f();
    private final Vector3f tmp2 = new Vector3f();

    public MarchingCubes(float isoLevel) {
        this.isoLevel = isoLevel;
    }

    private void interpolatePos(
            float ax, float ay, float az, float av,
            float bx, float by, float bz, float bv,
            float reference
    ) {
        if (Math.abs(reference - av) < EPSILON) {
            tmp1.set(ax, ay, az);
            return;
        }
        if (Math.abs(reference - bv) < EPSILON) {
            tmp1.set(bx, by, bz);
            return;
        }
        if (Math.abs(av - bv) < EPSILON) {
            tmp1.set(ax, ay, az);
            return;
        }

        float alpha = Math.abs((reference - av) / (Math.max(av, bv) - Math.min(av, bv)));
        tmp1.set(ax, ay, az).lerp(tmp2.set(bx, by, bz), alpha);

    }

    public List<Face> generateTriangles(VoxelStorage voxelStorage) {

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
            boolean hasNormal = voxel instanceof VoxelWithNormal;

            int cubeIndex = 0;

            if (voxel.v010() < isoLevel) {
                cubeIndex |= 1;
            }
            if (voxel.v110() < isoLevel) {
                cubeIndex |= 2;
            }
            if (voxel.v111() < isoLevel) {
                cubeIndex |= 4;
            }
            if (voxel.v011() < isoLevel) {
                cubeIndex |= 8;
            }

            if (voxel.v000() < isoLevel) {
                cubeIndex |= 16;
            }
            if (voxel.v100() < isoLevel) {
                cubeIndex |= 32;
            }
            if (voxel.v101() < isoLevel) {
                cubeIndex |= 64;
            }
            if (voxel.v001() < isoLevel) {
                cubeIndex |= 128;
            }

            if (cubeIndex == 0 || cubeIndex == 255) {
                ++numEmptyVoxels;
                continue;
            }

            int edge = EdgeTable.EDGE_TABLE[cubeIndex];

            if (edge == 0) {
                ++numEmptyVoxels;
                continue;
            }

            if ((edge & 1) > 0) {
                interpolatePos(
                        voxel.x010(),
                        voxel.y010(),
                        voxel.z010(),
                        voxel.v010(),
                        voxel.x110(),
                        voxel.y110(),
                        voxel.z110(),
                        voxel.v110(),
                        isoLevel
                );
                vertices[0].pos(tmp1.x, tmp1.y, tmp1.z);
                if (hasNormal) {
                    final var voxelN = (VoxelWithNormal) voxel;
                    interpolatePos(
                            voxelN.nx010(),
                            voxelN.ny010(),
                            voxelN.nz010(),
                            voxelN.v010(),
                            voxelN.nx110(),
                            voxelN.ny110(),
                            voxelN.nz110(),
                            voxelN.v110(),
                            isoLevel
                    );
                    vertices[0].norm(tmp1.x, tmp1.y, tmp1.z);
                }
            }

            if ((edge & 2) > 0) {
                interpolatePos(
                        voxel.x110(),
                        voxel.y110(),
                        voxel.z110(),
                        voxel.v110(),
                        voxel.x111(),
                        voxel.y111(),
                        voxel.z111(),
                        voxel.v111(),
                        isoLevel
                );
                vertices[1].pos(tmp1.x, tmp1.y, tmp1.z);
                if (hasNormal) {
                    final var voxelN = (VoxelWithNormal) voxel;
                    interpolatePos(
                            voxelN.nx110(),
                            voxelN.ny110(),
                            voxelN.nz110(),
                            voxelN.v110(),
                            voxelN.nx111(),
                            voxelN.ny111(),
                            voxelN.nz111(),
                            voxelN.v111(),
                            isoLevel
                    );
                    vertices[1].norm(tmp1.x, tmp1.y, tmp1.z);
                }
            }

            if ((edge & 4) > 0) {
                interpolatePos(
                        voxel.x111(),
                        voxel.y111(),
                        voxel.z111(),
                        voxel.v111(),
                        voxel.x011(),
                        voxel.y011(),
                        voxel.z011(),
                        voxel.v011(),
                        isoLevel
                );
                vertices[2].pos(tmp1.x, tmp1.y, tmp1.z);
                if (hasNormal) {
                    final var voxelN = (VoxelWithNormal) voxel;
                    interpolatePos(
                            voxelN.nx111(),
                            voxelN.ny111(),
                            voxelN.nz111(),
                            voxelN.v111(),
                            voxelN.nx011(),
                            voxelN.ny011(),
                            voxelN.nz011(),
                            voxelN.v011(),
                            isoLevel
                    );
                    vertices[2].norm(tmp1.x, tmp1.y, tmp1.z);
                }
            }

            if ((edge & 8) > 0) {
                interpolatePos(
                        voxel.x011(),
                        voxel.y011(),
                        voxel.z011(),
                        voxel.v011(),
                        voxel.x010(),
                        voxel.y010(),
                        voxel.z010(),
                        voxel.v010(),
                        isoLevel
                );
                vertices[3].pos(tmp1.x, tmp1.y, tmp1.z);
                if (hasNormal) {
                    final var voxelN = (VoxelWithNormal) voxel;
                    interpolatePos(
                            voxelN.nx011(),
                            voxelN.ny011(),
                            voxelN.nz011(),
                            voxelN.v011(),
                            voxelN.nx010(),
                            voxelN.ny010(),
                            voxelN.nz010(),
                            voxelN.v010(),
                            isoLevel
                    );
                    vertices[3].norm(tmp1.x, tmp1.y, tmp1.z);
                }
            }

            if ((edge & 16) > 0) {
                interpolatePos(
                        voxel.x000(),
                        voxel.y000(),
                        voxel.z000(),
                        voxel.v000(),
                        voxel.x100(),
                        voxel.y100(),
                        voxel.z100(),
                        voxel.v100(),
                        isoLevel
                );
                vertices[4].pos(tmp1.x, tmp1.y, tmp1.z);
                if (hasNormal) {
                    final var voxelN = (VoxelWithNormal) voxel;
                    interpolatePos(
                            voxelN.nx000(),
                            voxelN.ny000(),
                            voxelN.nz000(),
                            voxelN.v000(),
                            voxelN.nx100(),
                            voxelN.ny100(),
                            voxelN.nz100(),
                            voxelN.v100(),
                            isoLevel
                    );
                    vertices[4].norm(tmp1.x, tmp1.y, tmp1.z);
                }
            }

            if ((edge & 32) > 0) {
                interpolatePos(
                        voxel.x100(),
                        voxel.y100(),
                        voxel.z100(),
                        voxel.v100(),
                        voxel.x101(),
                        voxel.y101(),
                        voxel.z101(),
                        voxel.v101(),
                        isoLevel
                );
                vertices[5].pos(tmp1.x, tmp1.y, tmp1.z);
                if (hasNormal) {
                    final var voxelN = (VoxelWithNormal) voxel;
                    interpolatePos(
                            voxelN.nx100(),
                            voxelN.ny100(),
                            voxelN.nz100(),
                            voxelN.v100(),
                            voxelN.nx101(),
                            voxelN.ny101(),
                            voxelN.nz101(),
                            voxelN.v101(),
                            isoLevel
                    );
                    vertices[5].norm(tmp1.x, tmp1.y, tmp1.z);
                }
            }

            if ((edge & 64) > 0) {
                interpolatePos(
                        voxel.x101(),
                        voxel.y101(),
                        voxel.z101(),
                        voxel.v101(),
                        voxel.x001(),
                        voxel.y001(),
                        voxel.z001(),
                        voxel.v001(),
                        isoLevel
                );
                vertices[6].pos(tmp1.x, tmp1.y, tmp1.z);
                if (hasNormal) {
                    final var voxelN = (VoxelWithNormal) voxel;
                    interpolatePos(
                            voxelN.nx101(),
                            voxelN.ny101(),
                            voxelN.nz101(),
                            voxelN.v101(),
                            voxelN.nx001(),
                            voxelN.ny001(),
                            voxelN.nz001(),
                            voxelN.v001(),
                            isoLevel
                    );
                    vertices[6].norm(tmp1.x, tmp1.y, tmp1.z);
                }
            }

            if ((edge & 128) > 0) {
                interpolatePos(
                        voxel.x001(),
                        voxel.y001(),
                        voxel.z001(),
                        voxel.v001(),
                        voxel.x000(),
                        voxel.y000(),
                        voxel.z000(),
                        voxel.v000(),
                        isoLevel
                );
                vertices[7].pos(tmp1.x, tmp1.y, tmp1.z);
                if (hasNormal) {
                    final var voxelN = (VoxelWithNormal) voxel;
                    interpolatePos(
                            voxelN.nx001(),
                            voxelN.ny001(),
                            voxelN.nz001(),
                            voxelN.v001(),
                            voxelN.nx000(),
                            voxelN.ny000(),
                            voxelN.nz000(),
                            voxelN.v000(),
                            isoLevel
                    );
                    vertices[7].norm(tmp1.x, tmp1.y, tmp1.z);
                }
            }

            if ((edge & 256) > 0) {
                interpolatePos(
                        voxel.x010(),
                        voxel.y010(),
                        voxel.z010(),
                        voxel.v010(),
                        voxel.x000(),
                        voxel.y000(),
                        voxel.z000(),
                        voxel.v000(),
                        isoLevel
                );
                vertices[8].pos(tmp1.x, tmp1.y, tmp1.z);
                if (hasNormal) {
                    final var voxelN = (VoxelWithNormal) voxel;
                    interpolatePos(
                            voxelN.nx010(),
                            voxelN.ny010(),
                            voxelN.nz010(),
                            voxelN.v010(),
                            voxelN.nx000(),
                            voxelN.ny000(),
                            voxelN.nz000(),
                            voxelN.v000(),
                            isoLevel
                    );
                    vertices[8].norm(tmp1.x, tmp1.y, tmp1.z);
                }
            }

            if ((edge & 512) > 0) {
                interpolatePos(
                        voxel.x110(),
                        voxel.y110(),
                        voxel.z110(),
                        voxel.v110(),
                        voxel.x100(),
                        voxel.y100(),
                        voxel.z100(),
                        voxel.v100(),
                        isoLevel
                );
                vertices[9].pos(tmp1.x, tmp1.y, tmp1.z);
                if (hasNormal) {
                    final var voxelN = (VoxelWithNormal) voxel;
                    interpolatePos(
                            voxelN.nx110(),
                            voxelN.ny110(),
                            voxelN.nz110(),
                            voxelN.v110(),
                            voxelN.nx100(),
                            voxelN.ny100(),
                            voxelN.nz100(),
                            voxelN.v100(),
                            isoLevel
                    );
                    vertices[9].norm(tmp1.x, tmp1.y, tmp1.z);
                }
            }

            if ((edge & 1024) > 0) {
                interpolatePos(
                        voxel.x111(),
                        voxel.y111(),
                        voxel.z111(),
                        voxel.v111(),
                        voxel.x101(),
                        voxel.y101(),
                        voxel.z101(),
                        voxel.v101(),
                        isoLevel
                );
                vertices[10].pos(tmp1.x, tmp1.y, tmp1.z);
                if (hasNormal) {
                    final var voxelN = (VoxelWithNormal) voxel;
                    interpolatePos(
                            voxelN.nx111(),
                            voxelN.ny111(),
                            voxelN.nz111(),
                            voxelN.v111(),
                            voxelN.nx101(),
                            voxelN.ny101(),
                            voxelN.nz101(),
                            voxelN.v101(),
                            isoLevel
                    );
                    vertices[10].norm(tmp1.x, tmp1.y, tmp1.z);
                }
            }

            if ((edge & 2048) > 0) {
                interpolatePos(
                        voxel.x011(),
                        voxel.y011(),
                        voxel.z011(),
                        voxel.v011(),
                        voxel.x001(),
                        voxel.y001(),
                        voxel.z001(),
                        voxel.v001(),
                        isoLevel
                );
                vertices[11].pos(tmp1.x, tmp1.y, tmp1.z);
                if (hasNormal) {
                    final var voxelN = (VoxelWithNormal) voxel;
                    interpolatePos(
                            voxelN.nx011(),
                            voxelN.ny011(),
                            voxelN.nz011(),
                            voxelN.v011(),
                            voxelN.nx001(),
                            voxelN.ny001(),
                            voxelN.nz001(),
                            voxelN.v001(),
                            isoLevel
                    );
                    vertices[11].norm(tmp1.x, tmp1.y, tmp1.z);
                }
            }

            for (int i = 0; TriangleTable.TRIANGLE_TABLE[cubeIndex][i] != -1; i += 3) {

                Vertex v1 = vertices[TriangleTable.TRIANGLE_TABLE[cubeIndex][i]];
                Vertex v2 = vertices[TriangleTable.TRIANGLE_TABLE[cubeIndex][i + 1]];
                Vertex v3 = vertices[TriangleTable.TRIANGLE_TABLE[cubeIndex][i + 2]];

                if (!hasNormal || v1.norm.equals(0f, 0f, 0f) || v2.norm.equals(0f, 0f, 0f) || v3.norm.equals(
                        0f,
                        0f,
                        0f
                )) {

                    Vector3f v1v2 = new Vector3f(v2.pos).sub(v1.pos);
                    Vector3f v2v3 = new Vector3f(v3.pos).sub(v2.pos);

                    Vector3f faceNorm = new Vector3f(v1v2).cross(v2v3).normalize();

                    v1.norm(faceNorm.x, faceNorm.y, faceNorm.z);
                    v2.norm(faceNorm.x, faceNorm.y, faceNorm.z);
                    v3.norm(faceNorm.x, faceNorm.y, faceNorm.z);

                }

                v1.col(COLOR.x, COLOR.y, COLOR.z, COLOR.w);
                v2.col(COLOR.x, COLOR.y, COLOR.z, COLOR.w);
                v3.col(COLOR.x, COLOR.y, COLOR.z, COLOR.w);

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

        return generatedTriangles;
    }

    @Override
    public Drawable generate(VoxelStorage voxelStorage) {
        return Drawables.createTriangle(generateTriangles(voxelStorage));
    }

    private final class Vertex {

        private final Vector3f pos = new Vector3f();
        private final Vector3f norm = new Vector3f();
        private final Vector4f col = new Vector4f();

        public Vertex pos(float x, float y, float z) {
            this.pos.set(x, y, z);
            return this;
        }

        public Vertex norm(float x, float y, float z) {
            this.norm.set(x, y, z).normalize();
            return this;
        }

        public Vertex col(float r, float g, float b, float a) {
            this.col.set(r, g, b, a);
            return this;
        }

    }
}
