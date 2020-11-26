package me.bokov.bsc.surfaceviewer.mesh.dccpu;

import me.bokov.bsc.surfaceviewer.mesh.MeshGenerator;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.Drawables;
import me.bokov.bsc.surfaceviewer.voxelization.GridVoxel;
import me.bokov.bsc.surfaceviewer.voxelization.GridVoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelWithColor;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

public class GridDualContouring implements MeshGenerator {

    private Vector4f interpMaterial(VoxelWithColor voxel, float fx, float fy, float fz) {

        final Vector4f c000 = new Vector4f(voxel.r000(), voxel.g000(), voxel.b000(), voxel.s000());
        final Vector4f c001 = new Vector4f(voxel.r001(), voxel.g001(), voxel.b001(), voxel.s001());
        final Vector4f c010 = new Vector4f(voxel.r010(), voxel.g010(), voxel.b010(), voxel.s010());
        final Vector4f c011 = new Vector4f(voxel.r011(), voxel.g011(), voxel.b011(), voxel.s011());
        final Vector4f c100 = new Vector4f(voxel.r100(), voxel.g100(), voxel.b100(), voxel.s100());
        final Vector4f c101 = new Vector4f(voxel.r101(), voxel.g101(), voxel.b101(), voxel.s101());
        final Vector4f c110 = new Vector4f(voxel.r110(), voxel.g110(), voxel.b110(), voxel.s110());
        final Vector4f c111 = new Vector4f(voxel.r111(), voxel.g111(), voxel.b111(), voxel.s111());

        final Vector4f c00 = new Vector4f(c000.mul(fx)).sub(c100.mul(1.0f - fx));
        final Vector4f c01 = new Vector4f(c001.mul(fx)).sub(c101.mul(1.0f - fx));
        final Vector4f c10 = new Vector4f(c010.mul(fx)).sub(c110.mul(1.0f - fx));
        final Vector4f c11 = new Vector4f(c011.mul(fx)).sub(c111.mul(1.0f - fx));

        final Vector4f c0 = new Vector4f(c00.mul(fy)).sub(c10.mul(1.0f - fy));
        final Vector4f c1 = new Vector4f(c01.mul(fy)).sub(c11.mul(1.0f - fy));

        return new Vector4f(c0.mul(fz)).sub(c1.mul(1.0f - fz));

    }

    public List<Drawables.QuadFace> generateQuads(GridVoxelStorage voxelStorage) {

        final List<Drawables.QuadFace> generatedQuads = new ArrayList<>();

        final Vector3f tmp = new Vector3f();
        final Vector3f tmpN = new Vector3f();
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

        for (int z = 0; z < voxelStorage.zVoxelCount(); z++) {
            for (int y = 0; y < voxelStorage.yVoxelCount(); y++) {
                for (int x = 0; x < voxelStorage.xVoxelCount(); x++) {

                    final GridVoxel voxel = voxelStorage.at(x, y, z);

                    float cellSize = Math.min(
                            Math.min(
                                    voxel.x100() - voxel.x000(),
                                    voxel.y100() - voxel.y000()
                            ),
                            voxel.z100() - voxel.z000()
                    );

                    tmpN.set(voxel.nx000(), voxel.ny000(), voxel.nz000())
                            .add(voxel.nx100(), voxel.ny100(), voxel.nz100())
                            .add(voxel.nx101(), voxel.ny101(), voxel.nz101())
                            .add(voxel.nx001(), voxel.ny001(), voxel.nz001())

                            .add(voxel.nx010(), voxel.ny010(), voxel.nz010())
                            .add(voxel.nx110(), voxel.ny110(), voxel.nz110())
                            .add(voxel.nx111(), voxel.ny111(), voxel.nz111())
                            .add(voxel.nx011(), voxel.ny011(), voxel.nz011())
                            .normalize();

                    tmp.set(
                            0.5f * (voxel.x000() + voxel.x100()),
                            0.5f * (voxel.y000() + voxel.y100()),
                            0.5f * (voxel.z000() + voxel.z100())
                    ).add(tmpO.set(tmpN).mul(cellSize * 0.5f));

                    points[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage.xVoxelCount() + x]
                            = new Vector3f(tmp);
                    normals[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage.xVoxelCount() + x]
                            = new Vector3f(tmpN);

                    float fractX = (1.0f / cellSize) * (tmp.x - voxel.x000());
                    float fractY = (1.0f / cellSize) * (tmp.y - voxel.y000());
                    float fractZ = (1.0f / cellSize) * (tmp.z - voxel.z000());

                    colors[z * voxelStorage.yVoxelCount() * voxelStorage.xVoxelCount() + y * voxelStorage.xVoxelCount() + x]
                            = interpMaterial(voxel, fractX, fractY, fractZ);

                }
            }
        }

        for (int z = 0; z < voxelStorage.zVoxelCount(); z++) {
            for (int y = 0; y < voxelStorage.yVoxelCount(); y++) {
                for (int x = 0; x < voxelStorage.xVoxelCount(); x++) {

                    final var vox = voxelStorage.at(x, y, z);

                    if (Math.signum(vox.v000()) != Math.signum(vox.v100())) {

                        

                    }

                }
            }
        }

        return generatedQuads;

    }

    @Override
    public Drawable generate(VoxelStorage voxelStorage) {

        if (voxelStorage instanceof GridVoxelStorage) {
            return Drawables.createQuad(generateQuads((GridVoxelStorage) voxelStorage));
        }

        throw new UnsupportedOperationException();

    }

}
