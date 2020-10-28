package me.bokov.bsc.surfaceviewer.sdf;

import me.bokov.bsc.surfaceviewer.sdf.threed.Box;
import me.bokov.bsc.surfaceviewer.sdf.threed.CappedCylinder;
import me.bokov.bsc.surfaceviewer.sdf.threed.Cone;
import me.bokov.bsc.surfaceviewer.sdf.threed.ExpressionEvaluationContext;
import me.bokov.bsc.surfaceviewer.sdf.threed.OpGate;
import me.bokov.bsc.surfaceviewer.sdf.threed.OpInifiniteRepetition;
import me.bokov.bsc.surfaceviewer.sdf.threed.OpIntersect;
import me.bokov.bsc.surfaceviewer.sdf.threed.OpRotate;
import me.bokov.bsc.surfaceviewer.sdf.threed.OpScale;
import me.bokov.bsc.surfaceviewer.sdf.threed.OpSubtract;
import me.bokov.bsc.surfaceviewer.sdf.threed.OpTranslateTo;
import me.bokov.bsc.surfaceviewer.sdf.threed.OpUnion;
import me.bokov.bsc.surfaceviewer.sdf.threed.Sphere;
import me.bokov.bsc.surfaceviewer.sdf.threed.Torus;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public final class Evaluetables {

    private Evaluetables() {
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> box(Vector3f bounds) {
        return Evaluatable.of(new Box(bounds));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> box(Vector3f pos,
            Vector3f bounds
    ) {
        return Evaluatable.of(new OpTranslateTo(pos, box(bounds)));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> sphere(float radius) {
        return Evaluatable.of(new Sphere(radius));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> sphere(Vector3f pos,
            float radius
    ) {
        return Evaluatable.of(new OpTranslateTo(pos, sphere(radius)));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> cylinder(float height,
            float radius
    ) {
        return Evaluatable.of(new CappedCylinder(height, radius));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> cylinder(Vector3f pos,
            float height, float radius
    ) {
        return Evaluatable.of(new OpTranslateTo(pos, cylinder(height, radius)));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> torus(Vector2f radius) {
        return Evaluatable.of(new Torus(radius));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> torus(Vector3f pos,
            Vector2f radius
    ) {
        return Evaluatable.of(new OpTranslateTo(pos, torus(radius)));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> cone(float angle,
            float height
    ) {
        return Evaluatable.of(new Cone(angle, height));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> cone(Vector3f pos,
            float angle, float height
    ) {
        return Evaluatable.of(new OpTranslateTo(pos, cone(angle, height)));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> union(
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> first,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext>... rest
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

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> subtract(
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> first,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> second
    ) {
        return Evaluatable.of(
                new OpSubtract(first, second)
        );
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> intersect(
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> first,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext>... rest
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

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> rotate(
            Quaternionf q, Evaluatable<Float, Vector3f, ExpressionEvaluationContext> e
    ) {
        return Evaluatable.of(new OpRotate(q, e));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> translate(Vector3f pos,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator
    ) {
        return Evaluatable.of(new OpTranslateTo(pos, generator));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> scale(float scale,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator
    ) {
        return Evaluatable.of(new OpScale(scale, generator));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> infiniteRepeat(
            Vector3f period,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> e
    ) {
        return Evaluatable.of(new OpInifiniteRepetition(period, e));
    }

    public static Evaluatable<Float, Vector3f, ExpressionEvaluationContext> gate(
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> boundary,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator
    ) {
        return Evaluatable.of(
                new OpGate(boundary, generator)
        );
    }

}
