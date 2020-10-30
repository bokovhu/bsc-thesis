package me.bokov.bsc.surfaceviewer.render;

import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.stream.*;

public final class PointCloud {

    private PointCloud() {
    }

    private static float threshold(float v, float th, float out1, float out2) {
        return v <= th ? out1 : out2;
    }

    public static Drawable voxelCloud(VoxelStorage voxelStorage, float threshold) {

        System.out.println("Collecting voxels for voxel cloud ...");

        List<Voxel> voxels = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        voxelStorage.voxelIterator(),
                        Spliterator.ORDERED
                ),
                false
        ).collect(Collectors.toList());

        System.out.println("Collected " + voxels.size() + " voxels.");

        Set<Vector3f> usedPoints = new HashSet<>();
        Set<Vector4f> resultPoints = new HashSet<>();

        for (Voxel v : voxels) {

            float w = Math.abs(v.getP2().x - v.getP1().x);
            float h = Math.abs(v.getP2().y - v.getP1().y);
            float d = Math.abs(v.getP2().z - v.getP1().z);

            Vector3f c000 = new Vector3f(v.getP1());
            Vector3f c001 = new Vector3f(v.getP1()).add(0, 0, d);
            Vector3f c010 = new Vector3f(v.getP1()).add(0, h, 0);
            Vector3f c011 = new Vector3f(v.getP1()).add(0, h, d);

            Vector3f c100 = new Vector3f(v.getP1()).add(w, 0, 0);
            Vector3f c101 = new Vector3f(v.getP1()).add(w, 0, d);
            Vector3f c110 = new Vector3f(v.getP1()).add(w, h, 0);
            Vector3f c111 = new Vector3f(v.getP1()).add(w, h, d);

            if (usedPoints.add(c000)) {
                resultPoints.add(new Vector4f(
                        c000,
                        threshold(v.getC000().getValue(), threshold, 1.0f, 0.0f)
                ));
            }
            if (usedPoints.add(c001)) {
                resultPoints.add(new Vector4f(
                        c001,
                        threshold(v.getC001().getValue(), threshold, 1.0f, 0.0f)
                ));
            }
            if (usedPoints.add(c010)) {
                resultPoints.add(new Vector4f(
                        c010,
                        threshold(v.getC010().getValue(), threshold, 1.0f, 0.0f)
                ));
            }
            if (usedPoints.add(c011)) {
                resultPoints.add(new Vector4f(
                        c011,
                        threshold(v.getC011().getValue(), threshold, 1.0f, 0.0f)
                ));
            }

            if (usedPoints.add(c100)) {
                resultPoints.add(new Vector4f(
                        c100,
                        threshold(v.getC100().getValue(), threshold, 1.0f, 0.0f)
                ));
            }
            if (usedPoints.add(c101)) {
                resultPoints.add(new Vector4f(
                        c101,
                        threshold(v.getC101().getValue(), threshold, 1.0f, 0.0f)
                ));
            }
            if (usedPoints.add(c110)) {
                resultPoints.add(new Vector4f(
                        c110,
                        threshold(v.getC110().getValue(), threshold, 1.0f, 0.0f)
                ));
            }
            if (usedPoints.add(c111)) {
                resultPoints.add(new Vector4f(
                        c111,
                        threshold(v.getC111().getValue(), threshold, 1.0f, 0.0f)
                ));
            }

        }

        System.out.println("Using " + usedPoints.size() + " points for the point cloud.");
        System.out.println(
                "Min v: " + resultPoints.stream().mapToDouble(v -> v.w).min().getAsDouble()
                        + ", max v: " + resultPoints.stream().mapToDouble(v -> v.w).max()
                        .getAsDouble()
        );

        Drawable drawable = Drawable.direct();
        drawable.init();

        FloatBuffer fb = BufferUtils.createFloatBuffer(resultPoints.size() * (4 + 2 + 4));
        for (Vector4f v : resultPoints) {
            fb.put(
                    new float[]{
                            v.x, v.y, v.z, 1.0f,
                            0.0f, 0.0f,
                            v.w, v.w, v.w, v.w
                    }
            );
        }

        drawable.upload(fb.flip(), GL46.GL_POINTS, resultPoints.size());

        System.out.println("Point cloud uploaded");

        return drawable;

    }

}
