package me.bokov.bsc.surfaceviewer.mesh.dccpu;

import me.bokov.bsc.surfaceviewer.mesh.MeshGenerator;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.Drawables;
import me.bokov.bsc.surfaceviewer.util.MetricsLogger;
import me.bokov.bsc.surfaceviewer.voxelization.*;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.util.MathUtil.*;

public class GridDualContouring implements MeshGenerator {

    private final Vector4f tmp000 = new Vector4f();
    private final Vector4f tmp001 = new Vector4f();
    private final Vector4f tmp010 = new Vector4f();
    private final Vector4f tmp011 = new Vector4f();
    private final Vector4f tmp100 = new Vector4f();
    private final Vector4f tmp101 = new Vector4f();
    private final Vector4f tmp110 = new Vector4f();
    private final Vector4f tmp111 = new Vector4f();

    private final Vector4f tmp00 = new Vector4f();
    private final Vector4f tmp01 = new Vector4f();
    private final Vector4f tmp10 = new Vector4f();
    private final Vector4f tmp11 = new Vector4f();

    private final Vector4f tmp0 = new Vector4f();
    private final Vector4f tmp1 = new Vector4f();


    private final Vector3f ntmp000 = new Vector3f();
    private final Vector3f ntmp001 = new Vector3f();
    private final Vector3f ntmp010 = new Vector3f();
    private final Vector3f ntmp011 = new Vector3f();
    private final Vector3f ntmp100 = new Vector3f();
    private final Vector3f ntmp101 = new Vector3f();
    private final Vector3f ntmp110 = new Vector3f();
    private final Vector3f ntmp111 = new Vector3f();

    private final Vector3f ntmp00 = new Vector3f();
    private final Vector3f ntmp01 = new Vector3f();
    private final Vector3f ntmp10 = new Vector3f();
    private final Vector3f ntmp11 = new Vector3f();

    private final Vector3f ntmp0 = new Vector3f();
    private final Vector3f ntmp1 = new Vector3f();

    private Vector4f interpMaterial(VoxelWithColor voxel, float fx, float fy, float fz) {

        final Vector4f c000 = tmp000.set(voxel.r000(), voxel.g000(), voxel.b000(), voxel.s000());
        final Vector4f c001 = tmp001.set(voxel.r001(), voxel.g001(), voxel.b001(), voxel.s001());
        final Vector4f c010 = tmp010.set(voxel.r010(), voxel.g010(), voxel.b010(), voxel.s010());
        final Vector4f c011 = tmp011.set(voxel.r011(), voxel.g011(), voxel.b011(), voxel.s011());
        final Vector4f c100 = tmp100.set(voxel.r100(), voxel.g100(), voxel.b100(), voxel.s100());
        final Vector4f c101 = tmp101.set(voxel.r101(), voxel.g101(), voxel.b101(), voxel.s101());
        final Vector4f c110 = tmp110.set(voxel.r110(), voxel.g110(), voxel.b110(), voxel.s110());
        final Vector4f c111 = tmp111.set(voxel.r111(), voxel.g111(), voxel.b111(), voxel.s111());

        final Vector4f c00 = tmp00.set(c000).lerp(c100, fx);
        final Vector4f c01 = tmp01.set(c001).lerp(c101, fx);
        final Vector4f c10 = tmp10.set(c010).lerp(c110, fx);
        final Vector4f c11 = tmp11.set(c011).lerp(c111, fx);

        final Vector4f c0 = tmp0.set(c00).lerp(c10, fy);
        final Vector4f c1 = tmp1.set(c01).lerp(c11, fy);

        return new Vector4f(c0).lerp(c1, fz);

    }

    private Vector3f interpNormal(VoxelWithNormal voxel, float fx, float fy, float fz) {

        final Vector3f c000 = ntmp000.set(voxel.nx000(), voxel.ny000(), voxel.nz000());
        final Vector3f c001 = ntmp001.set(voxel.nx001(), voxel.ny001(), voxel.nz001());
        final Vector3f c010 = ntmp010.set(voxel.nx010(), voxel.ny010(), voxel.nz010());
        final Vector3f c011 = ntmp011.set(voxel.nx011(), voxel.ny011(), voxel.nz011());
        final Vector3f c100 = ntmp100.set(voxel.nx100(), voxel.ny100(), voxel.nz100());
        final Vector3f c101 = ntmp101.set(voxel.nx101(), voxel.ny101(), voxel.nz101());
        final Vector3f c110 = ntmp110.set(voxel.nx110(), voxel.ny110(), voxel.nz110());
        final Vector3f c111 = ntmp111.set(voxel.nx111(), voxel.ny111(), voxel.nz111());

        final Vector3f c00 = ntmp00.set(c000).lerp(c100, fx);
        final Vector3f c01 = ntmp01.set(c001).lerp(c101, fx);
        final Vector3f c10 = ntmp10.set(c010).lerp(c110, fx);
        final Vector3f c11 = ntmp11.set(c011).lerp(c111, fx);

        final Vector3f c0 = ntmp0.set(c00).lerp(c10, fy);
        final Vector3f c1 = ntmp1.set(c01).lerp(c11, fy);

        return new Vector3f(c0).lerp(c1, fz);

    }

    private List<Edge> makeEdges(GridVoxel voxel) {

        return new ArrayList<>(
                List.of(
                        new Edge(
                                voxel.x000(),
                                voxel.x100(),
                                voxel.y000(),
                                voxel.y100(),
                                voxel.z000(),
                                voxel.z100(),
                                voxel.nx000(),
                                voxel.nx100(),
                                voxel.ny000(),
                                voxel.ny100(),
                                voxel.nz000(),
                                voxel.nz100(),
                                voxel.v000(),
                                voxel.v100(),
                                voxel.r000(),
                                voxel.r100(),
                                voxel.g000(),
                                voxel.g100(),
                                voxel.b000(),
                                voxel.b100(),
                                voxel.s000(),
                                voxel.s100()
                        ),
                        new Edge(
                                voxel.x000(),
                                voxel.x010(),
                                voxel.y000(),
                                voxel.y010(),
                                voxel.z000(),
                                voxel.z010(),
                                voxel.nx000(),
                                voxel.nx010(),
                                voxel.ny000(),
                                voxel.ny010(),
                                voxel.nz000(),
                                voxel.nz010(),
                                voxel.v000(),
                                voxel.v010(),
                                voxel.r000(),
                                voxel.r010(),
                                voxel.g000(),
                                voxel.g010(),
                                voxel.b000(),
                                voxel.b010(),
                                voxel.s000(),
                                voxel.s010()
                        ),
                        new Edge(
                                voxel.x000(),
                                voxel.x001(),
                                voxel.y000(),
                                voxel.y001(),
                                voxel.z000(),
                                voxel.z001(),
                                voxel.nx000(),
                                voxel.nx001(),
                                voxel.ny000(),
                                voxel.ny001(),
                                voxel.nz000(),
                                voxel.nz001(),
                                voxel.v000(),
                                voxel.v001(),
                                voxel.r000(),
                                voxel.r001(),
                                voxel.g000(),
                                voxel.g001(),
                                voxel.b000(),
                                voxel.b001(),
                                voxel.s000(),
                                voxel.s001()
                        ),
                        new Edge(
                                voxel.x111(),
                                voxel.x011(),
                                voxel.y111(),
                                voxel.y011(),
                                voxel.z111(),
                                voxel.z011(),
                                voxel.nx111(),
                                voxel.nx011(),
                                voxel.ny111(),
                                voxel.ny011(),
                                voxel.nz111(),
                                voxel.nz011(),
                                voxel.v111(),
                                voxel.v011(),
                                voxel.r111(),
                                voxel.r011(),
                                voxel.g111(),
                                voxel.g011(),
                                voxel.b111(),
                                voxel.b011(),
                                voxel.s111(),
                                voxel.s011()
                        ),
                        new Edge(
                                voxel.x111(),
                                voxel.x101(),
                                voxel.y111(),
                                voxel.y101(),
                                voxel.z111(),
                                voxel.z101(),
                                voxel.nx111(),
                                voxel.nx101(),
                                voxel.ny111(),
                                voxel.ny101(),
                                voxel.nz111(),
                                voxel.nz101(),
                                voxel.v111(),
                                voxel.v101(),
                                voxel.r111(),
                                voxel.r101(),
                                voxel.g111(),
                                voxel.g101(),
                                voxel.b111(),
                                voxel.b101(),
                                voxel.s111(),
                                voxel.s101()
                        ),
                        new Edge(
                                voxel.x111(),
                                voxel.x110(),
                                voxel.y111(),
                                voxel.y110(),
                                voxel.z111(),
                                voxel.z110(),
                                voxel.nx111(),
                                voxel.nx110(),
                                voxel.ny111(),
                                voxel.ny110(),
                                voxel.nz111(),
                                voxel.nz110(),
                                voxel.v111(),
                                voxel.v110(),
                                voxel.r111(),
                                voxel.r110(),
                                voxel.g111(),
                                voxel.g110(),
                                voxel.b111(),
                                voxel.b110(),
                                voxel.s111(),
                                voxel.s110()
                        ),
                        new Edge(
                                voxel.x110(),
                                voxel.x010(),
                                voxel.y110(),
                                voxel.y010(),
                                voxel.z110(),
                                voxel.z010(),
                                voxel.nx110(),
                                voxel.nx010(),
                                voxel.ny110(),
                                voxel.ny010(),
                                voxel.nz110(),
                                voxel.nz010(),
                                voxel.v110(),
                                voxel.v010(),
                                voxel.r110(),
                                voxel.r010(),
                                voxel.g110(),
                                voxel.g010(),
                                voxel.b110(),
                                voxel.b010(),
                                voxel.s110(),
                                voxel.s010()
                        ),
                        new Edge(
                                voxel.x110(),
                                voxel.x100(),
                                voxel.y110(),
                                voxel.y100(),
                                voxel.z110(),
                                voxel.z100(),
                                voxel.nx110(),
                                voxel.nx100(),
                                voxel.ny110(),
                                voxel.ny100(),
                                voxel.nz110(),
                                voxel.nz100(),
                                voxel.v110(),
                                voxel.v100(),
                                voxel.r110(),
                                voxel.r100(),
                                voxel.g110(),
                                voxel.g100(),
                                voxel.b110(),
                                voxel.b100(),
                                voxel.s110(),
                                voxel.s100()
                        ),
                        new Edge(
                                voxel.x100(),
                                voxel.x101(),
                                voxel.y100(),
                                voxel.y101(),
                                voxel.z100(),
                                voxel.z101(),
                                voxel.nx100(),
                                voxel.nx101(),
                                voxel.ny100(),
                                voxel.ny101(),
                                voxel.nz100(),
                                voxel.nz101(),
                                voxel.v100(),
                                voxel.v101(),
                                voxel.r100(),
                                voxel.r101(),
                                voxel.g100(),
                                voxel.g101(),
                                voxel.b100(),
                                voxel.b101(),
                                voxel.s100(),
                                voxel.s101()
                        ),
                        new Edge(
                                voxel.x101(),
                                voxel.x001(),
                                voxel.y101(),
                                voxel.y001(),
                                voxel.z101(),
                                voxel.z001(),
                                voxel.nx101(),
                                voxel.nx001(),
                                voxel.ny101(),
                                voxel.ny001(),
                                voxel.nz101(),
                                voxel.nz001(),
                                voxel.v101(),
                                voxel.v001(),
                                voxel.r101(),
                                voxel.r001(),
                                voxel.g101(),
                                voxel.g001(),
                                voxel.b101(),
                                voxel.b001(),
                                voxel.s101(),
                                voxel.s001()
                        ),
                        new Edge(
                                voxel.x001(),
                                voxel.x011(),
                                voxel.y001(),
                                voxel.y011(),
                                voxel.z001(),
                                voxel.z011(),
                                voxel.nx001(),
                                voxel.nx011(),
                                voxel.ny001(),
                                voxel.ny011(),
                                voxel.nz001(),
                                voxel.nz011(),
                                voxel.v001(),
                                voxel.v011(),
                                voxel.r001(),
                                voxel.r011(),
                                voxel.g001(),
                                voxel.g011(),
                                voxel.b001(),
                                voxel.b011(),
                                voxel.s001(),
                                voxel.s011()
                        ),
                        new Edge(
                                voxel.x011(),
                                voxel.x010(),
                                voxel.y011(),
                                voxel.y010(),
                                voxel.z011(),
                                voxel.z010(),
                                voxel.nx011(),
                                voxel.nx010(),
                                voxel.ny011(),
                                voxel.ny010(),
                                voxel.nz011(),
                                voxel.nz010(),
                                voxel.v011(),
                                voxel.v010(),
                                voxel.r011(),
                                voxel.r010(),
                                voxel.g011(),
                                voxel.g010(),
                                voxel.b011(),
                                voxel.b010(),
                                voxel.s011(),
                                voxel.s010()
                        )
                )
        );

    }

    public List<Drawables.Face> generateTriangles(GridVoxelStorage voxelStorage) {

        long start = System.currentTimeMillis();

        final List<Drawables.Face> generatedTriangles = new ArrayList<>();

        final Vector3f tmp = new Vector3f();
        // final Vector3f tmpN = new Vector3f();
        final Vector3f tmpO = new Vector3f();

        final Vector3f[] points = new Vector3f[
                voxelStorage.xVoxelCount()
                        * voxelStorage.yVoxelCount()
                        * voxelStorage.zVoxelCount()
                ];
        final Vector3f[] normals = new Vector3f[
                voxelStorage.xVoxelCount()
                        * voxelStorage.yVoxelCount()
                        * voxelStorage.zVoxelCount()
                ];
        final Vector4f[] colors = new Vector4f[
                voxelStorage.xVoxelCount()
                        * voxelStorage.yVoxelCount()
                        * voxelStorage.zVoxelCount()
                ];

        final var unusedVoxelIterator = voxelStorage.voxelIterator();

        long obtainVoxelIteratorEnd = System.currentTimeMillis();

        long lastLayerEnd = System.currentTimeMillis();

        for (int z = 0; z < voxelStorage.zVoxelCount(); z++) {
            for (int y = 0; y < voxelStorage.yVoxelCount(); y++) {
                for (int x = 0; x < voxelStorage.xVoxelCount(); x++) {

                    final GridVoxel voxel = voxelStorage.at(x, y, z);

                    float cellSize = Math.min(
                            Math.min(
                                    voxel.x100() - voxel.x000(),
                                    voxel.y010() - voxel.y000()
                            ),
                            voxel.z001() - voxel.z000()
                    );

                    tmp.set(
                            0.5f * (voxel.x000() + voxel.x100()),
                            0.5f * (voxel.y000() + voxel.y010()),
                            0.5f * (voxel.z000() + voxel.z001())
                    );

                    final var edges = makeEdges(voxel);
                    final List<Edge> intersectedEdges = new ArrayList<>();

                    for (Edge edge : edges) {
                        if (edge.isIntersected()) {
                            intersectedEdges.add(edge);
                        }
                    }

                    if (intersectedEdges.size() >= 2) {

                        Vector3f avg = new Vector3f();
                        for (Edge edge : intersectedEdges) {
                            avg.add(
                                    interpolate(edge.x1, edge.v1, edge.x2, edge.v2, 0.0f),
                                    interpolate(edge.y1, edge.v1, edge.y2, edge.v2, 0.0f),
                                    interpolate(edge.z1, edge.v1, edge.z2, edge.v2, 0.0f)
                            );
                        }
                        avg.mul(1.0f / (float) intersectedEdges.size());
                        tmp.set(avg);

                    }

                    /* tmp.set(
                            clamp(tmp.x, voxel.x000(), voxel.x100()),
                            clamp(tmp.y, voxel.y000(), voxel.y010()),
                            clamp(tmp.z, voxel.z000(), voxel.z001())
                    ); */

                    float fractX = clamp((1.0f / cellSize) * (tmp.x - voxel.x000()), 0, 1);
                    float fractY = clamp((1.0f / cellSize) * (tmp.y - voxel.y000()), 0, 1);
                    float fractZ = clamp((1.0f / cellSize) * (tmp.z - voxel.z000()), 0, 1);
                    final Vector3f tmpN = interpNormal(voxel, fractX, fractY, fractZ).normalize();

                    points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage.xVoxelCount() + x]
                            = new Vector3f(tmp);
                    normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage.xVoxelCount() + x]
                            = tmpN;

                    colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage.xVoxelCount() + x]
                            = interpMaterial(voxel, fractX, fractY, fractZ);


                }

            }

            long doneTime = System.currentTimeMillis();
            System.out.println(z + " layer done in " + (doneTime - lastLayerEnd) + " ms");
            lastLayerEnd = doneTime;

        }

        long placePointsEnd = System.currentTimeMillis();

        for (int z = 0; z < voxelStorage.zVoxelCount() - 1; z++) {
            for (int y = 0; y < voxelStorage.yVoxelCount() - 1; y++) {
                for (int x = 0; x < voxelStorage.xVoxelCount() - 1; x++) {

                    final var vox = voxelStorage.at(x, y, z);

                    if (Math.signum(vox.v110()) != Math.signum(vox.v111())) {

                        generatedTriangles.add(
                                new Drawables.Face(
                                        points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x + 1],
                                        points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x + 1],

                                        normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x + 1],
                                        normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x + 1],

                                        colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x + 1],
                                        colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x + 1]
                                )
                        );

                        generatedTriangles.add(
                                new Drawables.Face(
                                        points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x + 1],
                                        points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x],

                                        normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x + 1],
                                        normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x],

                                        colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x + 1],
                                        colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x]
                                )
                        );

                    }

                    if (Math.signum(vox.v111()) != Math.signum(vox.v101())) {

                        generatedTriangles.add(
                                new Drawables.Face(
                                        points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x + 1],
                                        points[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x + 1],

                                        normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x + 1],
                                        normals[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x + 1],

                                        colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x + 1],
                                        colors[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x + 1]
                                )
                        );
                        generatedTriangles.add(
                                new Drawables.Face(
                                        points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        points[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x + 1],
                                        points[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],

                                        normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        normals[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x + 1],
                                        normals[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],

                                        colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        colors[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x + 1],
                                        colors[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x]
                                )
                        );

                    }

                    if (Math.signum(vox.v011()) != Math.signum(vox.v111())) {

                        generatedTriangles.add(
                                new Drawables.Face(
                                        points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        points[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        points[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x],

                                        normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        normals[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        normals[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x],

                                        colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        colors[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        colors[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x]
                                )
                        );

                        generatedTriangles.add(
                                new Drawables.Face(
                                        points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        points[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x],
                                        points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x],

                                        normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        normals[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x],
                                        normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x],

                                        colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage
                                                .xVoxelCount() + x],
                                        colors[(z + 1) * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x],
                                        colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + (y + 1) * voxelStorage
                                                .xVoxelCount() + x]
                                )
                        );

                    }

                }
            }
        }

        long end = System.currentTimeMillis();

        MetricsLogger.logMetrics(
                "Dual Contouring (CPU)",
                Map.of(
                        "Total runtime", (end - start) + " ms",
                        "Voxel iterator obtain time", (obtainVoxelIteratorEnd - start) + " ms",
                        "Point placement time", (placePointsEnd - obtainVoxelIteratorEnd) + " ms",
                        "Generate triangles time", (end - placePointsEnd) + " ms",
                        "Generated triangle count", generatedTriangles.size()
                )
        );

        return generatedTriangles;

    }

    @Override
    public Drawable generate(VoxelStorage voxelStorage) {

        if (voxelStorage instanceof GridVoxelStorage) {
            return Drawables.createTriangle(generateTriangles((GridVoxelStorage) voxelStorage));
        }

        throw new UnsupportedOperationException();

    }

    class Edge {

        final float x1, x2, y1, y2, z1, z2, n1x, n2x, n1y, n2y, n1z, n2z, v1, v2, r1, r2, g1, g2, b1, b2, s1, s2;

        Edge(
                float x1,
                float x2,
                float y1,
                float y2,
                float z1,
                float z2,
                float n1x,
                float n2x,
                float n1y,
                float n2y,
                float n1z,
                float n2z,
                float v1,
                float v2,
                float r1,
                float r2,
                float g1,
                float g2,
                float b1,
                float b2,
                float s1,
                float s2
        ) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.z1 = z1;
            this.z2 = z2;
            this.n1x = n1x;
            this.n2x = n2x;
            this.n1y = n1y;
            this.n2y = n2y;
            this.n1z = n1z;
            this.n2z = n2z;
            this.v1 = v1;
            this.v2 = v2;
            this.r1 = r1;
            this.r2 = r2;
            this.g1 = g1;
            this.g2 = g2;
            this.b1 = b1;
            this.b2 = b2;
            this.s1 = s1;
            this.s2 = s2;
        }

        boolean isIntersected() {
            return Math.signum(v1) != Math.signum(v2);
        }

    }


}
