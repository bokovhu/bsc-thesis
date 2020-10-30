package me.bokov.bsc.surfaceviewer.sdf;

import me.bokov.bsc.surfaceviewer.sdf.threed.*;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public final class Evaluetables {

    private Evaluetables() {
    }

    public static Evaluatable<Float, CPUContext, GPUContext> box(Vector3f bounds) {
        return Evaluatable.of(new Box(bounds));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> box(
            Vector3f pos,
            Vector3f bounds
    ) {
        return Evaluatable.of(new OpTranslateTo(pos, box(bounds)));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> sphere(float radius) {
        return Evaluatable.of(new Sphere(radius));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> sphere(
            Vector3f pos,
            float radius
    ) {
        return Evaluatable.of(new OpTranslateTo(pos, sphere(radius)));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> cylinder(
            float height,
            float radius
    ) {
        return Evaluatable.of(new CappedCylinder(height, radius));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> cylinder(
            Vector3f pos,
            float height, float radius
    ) {
        return Evaluatable.of(new OpTranslateTo(pos, cylinder(height, radius)));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> torus(Vector2f radius) {
        return Evaluatable.of(new Torus(radius));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> torus(
            Vector3f pos,
            Vector2f radius
    ) {
        return Evaluatable.of(new OpTranslateTo(pos, torus(radius)));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> cone(
            float angle,
            float height
    ) {
        return Evaluatable.of(new Cone(angle, height));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> cone(
            Vector3f pos,
            float angle, float height
    ) {
        return Evaluatable.of(new OpTranslateTo(pos, cone(angle, height)));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> union(
            Evaluatable<Float, CPUContext, GPUContext> first,
            Evaluatable<Float, CPUContext, GPUContext>... rest
    ) {

        if (rest.length == 0) {
            return first;
        }
        final var nextRest = new Evaluatable[rest.length - 1];
        System.arraycopy(rest, 1, nextRest, 0, nextRest.length);
        return Evaluatable.of(
                new OpUnion(
                        first,
                        nextRest.length == 0 ? rest[0] : union(rest[0], nextRest)
                )
        );

    }

    public static Evaluatable<Float, CPUContext, GPUContext> subtract(
            Evaluatable<Float, CPUContext, GPUContext> first,
            Evaluatable<Float, CPUContext, GPUContext> second
    ) {
        return Evaluatable.of(
                new OpSubtract(first, second)
        );
    }

    public static Evaluatable<Float, CPUContext, GPUContext> intersect(
            Evaluatable<Float, CPUContext, GPUContext> first,
            Evaluatable<Float, CPUContext, GPUContext>... rest
    ) {
        if (rest.length == 0) {
            return first;
        }
        final var nextRest = new Evaluatable[rest.length - 1];
        System.arraycopy(rest, 1, nextRest, 0, nextRest.length);
        return Evaluatable.of(
                new OpIntersect(
                        first,
                        nextRest.length == 0 ? rest[0] : union(rest[0], nextRest)
                )
        );
    }

    public static Evaluatable<Float, CPUContext, GPUContext> rotate(
            Quaternionf q, Evaluatable<Float, CPUContext, GPUContext> e
    ) {
        return Evaluatable.of(new OpRotate(q, e));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> translate(
            Vector3f pos,
            Evaluatable<Float, CPUContext, GPUContext> generator
    ) {
        return Evaluatable.of(new OpTranslateTo(pos, generator));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> scale(
            float scale,
            Evaluatable<Float, CPUContext, GPUContext> generator
    ) {
        return Evaluatable.of(new OpScale(scale, generator));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> infiniteRepeat(
            Vector3f period,
            Evaluatable<Float, CPUContext, GPUContext> e
    ) {
        return Evaluatable.of(new OpInifiniteRepetition(period, e));
    }

    public static Evaluatable<Float, CPUContext, GPUContext> gate(
            Evaluatable<Float, CPUContext, GPUContext> boundary,
            Evaluatable<Float, CPUContext, GPUContext> generator
    ) {
        return Evaluatable.of(
                new OpGate(boundary, generator)
        );
    }

}
