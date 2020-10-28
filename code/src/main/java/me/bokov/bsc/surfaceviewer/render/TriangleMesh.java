package me.bokov.bsc.surfaceviewer.render;

import java.nio.FloatBuffer;
import java.util.List;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

public class TriangleMesh {

    private TriangleMesh() {
    }

    public static Drawable create(List<Face> triangles) {

        FloatBuffer fb = BufferUtils.createFloatBuffer(
                3 * triangles.size() * (3 + 3 + 4)
        );

        for (Face f : triangles) {
            fb.put(
                    new float[]{
                            f.pos1.x, f.pos1.y, f.pos1.z,
                            f.normal1.x, f.normal1.y, f.normal1.z,
                            f.color1.x, f.color1.y, f.color1.z, f.color1.w,

                            f.pos2.x, f.pos2.y, f.pos2.z,
                            f.normal2.x, f.normal2.y, f.normal2.z,
                            f.color2.x, f.color2.y, f.color2.z, f.color2.w,

                            f.pos3.x, f.pos3.y, f.pos3.z,
                            f.normal3.x, f.normal3.y, f.normal3.z,
                            f.color3.x, f.color3.y, f.color3.z, f.color3.w,
                    }
            );
        }

        Drawable drawable = Drawable.standard3D();
        drawable.init();

        drawable.upload(fb.flip(), GL46.GL_TRIANGLES, triangles.size() * 3);

        return drawable;

    }

    public static class Face {

        private final Vector3f pos1, pos2, pos3;
        private final Vector3f normal1, normal2, normal3;
        private final Vector4f color1, color2, color3;

        public Face(Vector3f pos1, Vector3f pos2, Vector3f pos3, Vector3f normal1,
                Vector3f normal2,
                Vector3f normal3,
                Vector4f color1,
                Vector4f color2,
                Vector4f color3
        ) {
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.pos3 = pos3;
            this.normal1 = normal1;
            this.normal2 = normal2;
            this.normal3 = normal3;
            this.color1 = color1;
            this.color2 = color2;
            this.color3 = color3;
        }
    }

}
