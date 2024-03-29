package me.bokov.bsc.surfaceviewer.render;

import me.bokov.bsc.surfaceviewer.util.MathUtil;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.stream.*;

public interface Drawables {

    static Drawable fullScreenQuad() {

        Drawable d = Drawable.direct();
        d.init();

        FloatBuffer fb = BufferUtils.createFloatBuffer(6 * d.vertexElementCount());

        fb.put(new float[]{
                -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,

                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f
        });

        d.upload(fb.flip(), GL46.GL_TRIANGLES, 6);

        return d;

    }

    static Drawable voxelCloud(VoxelStorage voxelStorage, float threshold) {

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

            float w = Math.abs(v.x111() - v.x000());
            float h = Math.abs(v.y111() - v.y000());
            float d = Math.abs(v.z111() - v.z000());

            Vector3f c000 = new Vector3f(v.x000(), v.y000(), v.z000());
            Vector3f c001 = new Vector3f(v.x000(), v.y000(), v.z000()).add(0, 0, d);
            Vector3f c010 = new Vector3f(v.x000(), v.y000(), v.z000()).add(0, h, 0);
            Vector3f c011 = new Vector3f(v.x000(), v.y000(), v.z000()).add(0, h, d);

            Vector3f c100 = new Vector3f(v.x000(), v.y000(), v.z000()).add(w, 0, 0);
            Vector3f c101 = new Vector3f(v.x000(), v.y000(), v.z000()).add(w, 0, d);
            Vector3f c110 = new Vector3f(v.x000(), v.y000(), v.z000()).add(w, h, 0);
            Vector3f c111 = new Vector3f(v.x000(), v.y000(), v.z000()).add(w, h, d);

            if (usedPoints.add(c000)) {
                resultPoints.add(new Vector4f(
                        c000,
                        MathUtil.threshold(v.v000(), threshold, 1.0f, 0.0f)
                ));
            }
            if (usedPoints.add(c001)) {
                resultPoints.add(new Vector4f(
                        c001,
                        MathUtil.threshold(v.v001(), threshold, 1.0f, 0.0f)
                ));
            }
            if (usedPoints.add(c010)) {
                resultPoints.add(new Vector4f(
                        c010,
                        MathUtil.threshold(v.v010(), threshold, 1.0f, 0.0f)
                ));
            }
            if (usedPoints.add(c011)) {
                resultPoints.add(new Vector4f(
                        c011,
                        MathUtil.threshold(v.v011(), threshold, 1.0f, 0.0f)
                ));
            }

            if (usedPoints.add(c100)) {
                resultPoints.add(new Vector4f(
                        c100,
                        MathUtil.threshold(v.v100(), threshold, 1.0f, 0.0f)
                ));
            }
            if (usedPoints.add(c101)) {
                resultPoints.add(new Vector4f(
                        c101,
                        MathUtil.threshold(v.v101(), threshold, 1.0f, 0.0f)
                ));
            }
            if (usedPoints.add(c110)) {
                resultPoints.add(new Vector4f(
                        c110,
                        MathUtil.threshold(v.v110(), threshold, 1.0f, 0.0f)
                ));
            }
            if (usedPoints.add(c111)) {
                resultPoints.add(new Vector4f(
                        c111,
                        MathUtil.threshold(v.v111(), threshold, 1.0f, 0.0f)
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

    static Drawable createTriangle(List<Face> triangles) {

        FloatBuffer fb = BufferUtils.createFloatBuffer(
                3 * triangles.size() * (3 + 3 + 4)
        );

        for (Face f : triangles) {
            fb.put(
                    new float[]{
                            f.pos1.x, f.pos1.y, f.pos1.z,
                            f.normal1.x, f.normal1.y, f.normal1.z,

                            f.pos2.x, f.pos2.y, f.pos2.z,
                            f.normal2.x, f.normal2.y, f.normal2.z,

                            f.pos3.x, f.pos3.y, f.pos3.z,
                            f.normal3.x, f.normal3.y, f.normal3.z
                    }
            );
        }

        Drawable drawable = Drawable.standard3D();
        drawable.init();

        drawable.upload(fb.flip(), GL46.GL_TRIANGLES, triangles.size() * 3);

        return drawable;

    }

    static Drawable createQuad(List<QuadFace> quads) {

        FloatBuffer fb = BufferUtils.createFloatBuffer(
                4 * quads.size() * (3 + 3 + 4)
        );

        for (QuadFace f : quads) {
            fb.put(
                    new float[]{
                            f.pos1.x, f.pos1.y, f.pos1.z,
                            f.normal1.x, f.normal1.y, f.normal1.z,

                            f.pos2.x, f.pos2.y, f.pos2.z,
                            f.normal2.x, f.normal2.y, f.normal2.z,

                            f.pos3.x, f.pos3.y, f.pos3.z,
                            f.normal3.x, f.normal3.y, f.normal3.z,

                            f.pos4.x, f.pos4.y, f.pos4.z,
                            f.normal4.x, f.normal4.y, f.normal4.z
                    }
            );
        }

        Drawable drawable = Drawable.standard3D();
        drawable.init();

        drawable.upload(fb.flip(), GL46.GL_QUADS, quads.size() * 4);

        return drawable;

    }

    class Face {

        public final Vector3f pos1, pos2, pos3;
        public final Vector3f normal1, normal2, normal3;

        public Face(
                Vector3f pos1, Vector3f pos2, Vector3f pos3, Vector3f normal1,
                Vector3f normal2,
                Vector3f normal3
        ) {
            this.pos1 = new Vector3f(pos1);
            this.pos2 = new Vector3f(pos2);
            this.pos3 = new Vector3f(pos3);
            this.normal1 = new Vector3f(normal1);
            this.normal2 = new Vector3f(normal2);
            this.normal3 = new Vector3f(normal3);
        }
    }

    class QuadFace {

        public final Vector3f pos1, pos2, pos3, pos4;
        public final Vector3f normal1, normal2, normal3, normal4;

        public QuadFace(
                Vector3f pos1,
                Vector3f pos2,
                Vector3f pos3,
                Vector3f pos4,
                Vector3f normal1,
                Vector3f normal2,
                Vector3f normal3,
                Vector3f normal4
        ) {
            this.pos1 = new Vector3f(pos1);
            this.pos2 = new Vector3f(pos2);
            this.pos3 = new Vector3f(pos3);
            this.pos4 = new Vector3f(pos4);
            this.normal1 = new Vector3f(normal1);
            this.normal2 = new Vector3f(normal2);
            this.normal3 = new Vector3f(normal3);
            this.normal4 = new Vector3f(normal4);
        }
    }

}
