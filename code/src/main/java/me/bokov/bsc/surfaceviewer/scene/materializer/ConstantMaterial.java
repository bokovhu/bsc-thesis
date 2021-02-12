package me.bokov.bsc.surfaceviewer.scene.materializer;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.scene.Materializer;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class ConstantMaterial implements Materializer {

    private final int id;

    private final SceneNode boundary;

    private final Evaluable<Vector3f, ColorCPUContext, ColorGPUContext> diffuseColorEval = Evaluable.of(new ColorEvaluator());
    private final Evaluable<Float, ColorCPUContext, ColorGPUContext> shininessEval = Evaluable.of(new ShininessEvaluator());

    private final Vector3f diffuse;
    private final float shininess;

    public ConstantMaterial(
            int id,
            SceneNode boundary,
            Vector3f diffuse,
            float shininess
    ) {
        this.id = id;
        this.boundary = boundary;
        this.diffuse = diffuse;
        this.shininess = shininess;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public SceneNode getBoundary() {
        return boundary;
    }

    @Override
    public Evaluable<Vector3f, ColorCPUContext, ColorGPUContext> getDiffuseColor() {
        return diffuseColorEval;
    }

    @Override
    public Evaluable<Float, ColorCPUContext, ColorGPUContext> getShininess() {
        return shininessEval;
    }

    public Vector3f diffuse() {
        return diffuse;
    }

    public float shininess() {
        return shininess;
    }

    final class ColorEvaluator implements Serializable,
            CPUEvaluator<Vector3f, ColorCPUContext>,
            GPUEvaluator<ColorGPUContext> {

        @Override
        public Vector3f evaluate(ColorCPUContext context) {
            return diffuse;
        }

        @Override
        public List<GLSLStatement> evaluate(ColorGPUContext context) {
            return List.of(
                    var("vec3", context.getResult(), vec3(diffuse))
            );
        }
    }

    final class ShininessEvaluator implements Serializable,
            CPUEvaluator<Float, ColorCPUContext>,
            GPUEvaluator<ColorGPUContext> {

        @Override
        public Float evaluate(ColorCPUContext context) {
            return shininess;
        }

        @Override
        public List<GLSLStatement> evaluate(ColorGPUContext context) {
            return List.of(
                    resultVar(context, literal(shininess))
            );
        }
    }

}
