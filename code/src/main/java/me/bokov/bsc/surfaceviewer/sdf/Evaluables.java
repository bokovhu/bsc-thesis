package me.bokov.bsc.surfaceviewer.sdf;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.threed.*;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public final class Evaluables {

    private Evaluables() {
    }

    public static Evaluable<Float, CPUContext, GPUContext> box(Vector3f bounds) {
        return Evaluable.of(new Box(bounds));
    }

    public static Evaluable<Float, CPUContext, GPUContext> box(
            Vector3f pos,
            Vector3f bounds
    ) {
        return Evaluable.of(new OpTranslateTo(pos, box(bounds)));
    }

    public static Evaluable<Float, CPUContext, GPUContext> sphere(float radius) {
        return Evaluable.of(new Sphere(radius));
    }

    public static Evaluable<Float, CPUContext, GPUContext> sphere(
            Vector3f pos,
            float radius
    ) {
        return Evaluable.of(new OpTranslateTo(pos, sphere(radius)));
    }

    public static Evaluable<Float, CPUContext, GPUContext> cylinder(
            float height,
            float radius
    ) {
        return Evaluable.of(new CappedCylinder(height, radius));
    }

    public static Evaluable<Float, CPUContext, GPUContext> cylinder(
            Vector3f pos,
            float height, float radius
    ) {
        return Evaluable.of(new OpTranslateTo(pos, cylinder(height, radius)));
    }

    public static Evaluable<Float, CPUContext, GPUContext> torus(Vector2f radius) {
        return Evaluable.of(new Torus(radius));
    }

    public static Evaluable<Float, CPUContext, GPUContext> torus(
            Vector3f pos,
            Vector2f radius
    ) {
        return Evaluable.of(new OpTranslateTo(pos, torus(radius)));
    }

    public static Evaluable<Float, CPUContext, GPUContext> cone(
            float angle,
            float height
    ) {
        return Evaluable.of(new Cone(angle, height));
    }

    public static Evaluable<Float, CPUContext, GPUContext> cone(
            Vector3f pos,
            float angle, float height
    ) {
        return Evaluable.of(new OpTranslateTo(pos, cone(angle, height)));
    }

    public static Evaluable<Float, CPUContext, GPUContext> union(
            Evaluable<Float, CPUContext, GPUContext> first,
            Evaluable<Float, CPUContext, GPUContext>... rest
    ) {

        if (rest.length == 0) {
            return first;
        }
        final var nextRest = new Evaluable[rest.length - 1];
        System.arraycopy(rest, 1, nextRest, 0, nextRest.length);
        return Evaluable.of(
                new OpUnion(
                        first,
                        nextRest.length == 0 ? rest[0] : union(rest[0], nextRest)
                )
        );

    }

    public static Evaluable<Float, CPUContext, GPUContext> union(List<Evaluable<Float, CPUContext, GPUContext>> list) {
        if (list.size() == 0) {
            return Evaluable.of(context -> 0.0f, context -> List.of(resultVar(context, literal(0.0f))));
        }
        if (list.size() == 1) { return union(list.get(0)); }
        if (list.size() == 2) { return union(list.get(0), list.get(1)); }
        return union(list.get(0), list.subList(1, list.size()).toArray(new Evaluable[0]));
    }

    public static Evaluable<Float, CPUContext, GPUContext> subtract(
            Evaluable<Float, CPUContext, GPUContext> first,
            Evaluable<Float, CPUContext, GPUContext> second
    ) {
        return Evaluable.of(
                new OpSubtract(first, second)
        );
    }

    public static Evaluable<Float, CPUContext, GPUContext> intersect(
            Evaluable<Float, CPUContext, GPUContext> first,
            Evaluable<Float, CPUContext, GPUContext>... rest
    ) {
        if (rest.length == 0) {
            return first;
        }
        final var nextRest = new Evaluable[rest.length - 1];
        System.arraycopy(rest, 1, nextRest, 0, nextRest.length);
        return Evaluable.of(
                new OpIntersect(
                        first,
                        nextRest.length == 0 ? rest[0] : union(rest[0], nextRest)
                )
        );
    }

    public static Evaluable<Float, CPUContext, GPUContext> intersect(List<Evaluable<Float, CPUContext, GPUContext>> list) {
        if (list.size() == 0) { return null; }
        if (list.size() == 1) { return intersect(list.get(0)); }
        if (list.size() == 2) { return intersect(list.get(0), list.get(1)); }
        return intersect(list.get(0), list.subList(1, list.size()).toArray(new Evaluable[0]));
    }

    public static Evaluable<Float, CPUContext, GPUContext> rotate(
            Quaternionf q, Evaluable<Float, CPUContext, GPUContext> e
    ) {
        return Evaluable.of(new OpRotate(q, e));
    }

    public static Evaluable<Float, CPUContext, GPUContext> translate(
            Vector3f pos,
            Evaluable<Float, CPUContext, GPUContext> generator
    ) {
        return Evaluable.of(new OpTranslateTo(pos, generator));
    }

    public static Evaluable<Float, CPUContext, GPUContext> scale(
            float scale,
            Evaluable<Float, CPUContext, GPUContext> generator
    ) {
        return Evaluable.of(new OpScale(scale, generator));
    }

    public static Evaluable<Float, CPUContext, GPUContext> infiniteRepeat(
            Vector3f period,
            Evaluable<Float, CPUContext, GPUContext> e
    ) {
        return Evaluable.of(new OpInifiniteRepetition(period, e));
    }

    public static Evaluable<Float, CPUContext, GPUContext> gate(
            Evaluable<Float, CPUContext, GPUContext> boundary,
            Evaluable<Float, CPUContext, GPUContext> generator
    ) {
        return Evaluable.of(
                new OpGate(boundary, generator)
        );
    }

    public static Evaluable<Float, CPUContext, GPUContext> transform(
            MeshTransform M,
            Evaluable<Float, CPUContext, GPUContext> generator
    ) {
        return Evaluable.of(
                new OpTranslateTo(
                        M.position(),
                        Evaluable.of(
                                new OpScale(
                                        M.scale(),
                                        Evaluable.of(
                                                new OpRotate(
                                                        M.orientation(),
                                                        generator
                                                )
                                        )
                                )
                        )
                )
        );
    }

}
