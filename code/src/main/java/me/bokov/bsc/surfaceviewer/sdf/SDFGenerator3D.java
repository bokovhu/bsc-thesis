package me.bokov.bsc.surfaceviewer.sdf;

import java.nio.FloatBuffer;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public interface SDFGenerator3D extends CPUEvaluator<Float, Vector3f> {

    default float query(float x, float y, float z) {
        FloatBuffer fb = BufferUtils.createFloatBuffer(1);
        query(fb, new float[]{x, y, z});
        return fb.get(0);
    }

    default float query(Vector3f point) {
        return query(point.x, point.y, point.z);
    }

    default void query(FloatBuffer out, Vector3f[] points) {
        float[] pointArray = new float[points.length * 3];
        for (int i = 0; i < points.length; i++) {
            final var v = points[i];
            pointArray[i * 3] = v.x;
            pointArray[i * 3 + 1] = v.y;
            pointArray[i * 3 + 2] = v.z;
        }
        query(out, pointArray);
    }

    String getKind();

    void query(FloatBuffer out, float[] points);

    @Override
    default Float evaluate(Vector3f p) {
        return query(p);
    }
}
