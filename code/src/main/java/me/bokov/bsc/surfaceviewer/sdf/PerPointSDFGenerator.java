package me.bokov.bsc.surfaceviewer.sdf;

import java.nio.FloatBuffer;

public interface PerPointSDFGenerator extends SDFGenerator {

    float getAt(float x, float y, float z);

    @Override
    default void query(FloatBuffer out, float[] points) {

        if (points.length % 3 != 0) {
            throw new IllegalArgumentException("Not a valid list of 3D points.");
        }

        for (int i = 0; i < points.length / 3; i++) {

            out.put(getAt(points[i * 3], points[i * 3 + 1], points[i * 3 + 2]));

        }

    }
}
