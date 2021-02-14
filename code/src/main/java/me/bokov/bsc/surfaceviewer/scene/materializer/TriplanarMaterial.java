package me.bokov.bsc.surfaceviewer.scene.materializer;

import me.bokov.bsc.surfaceviewer.glsl.GLSLIfStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.scene.Materializer;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class TriplanarMaterial implements Materializer {

    private final int id;

    private SceneNode boundary;

    private String diffuseMapName;
    private Vector3f diffuseColor;

    private String shininessMapName;
    private Float shininess;

    private float scale = 1.0f;
    private float sharpness = 2.0f;

    private Evaluable<Vector3f, ColorCPUContext, ColorGPUContext> diffuseEval = Evaluable.of(new ColorEvaluator());
    private Evaluable<Float, ColorCPUContext, ColorGPUContext> shininessEval = Evaluable.of(new ShininessEvaluator());


    public TriplanarMaterial(
            int id,
            SceneNode boundary,
            String diffuseMapName,
            Vector3f diffuseColor,
            String shininessMapName,
            Float shininess
    ) {
        this.id = id;
        this.boundary = boundary;
        this.diffuseMapName = diffuseMapName;
        this.diffuseColor = diffuseColor;
        this.shininessMapName = shininessMapName;
        this.shininess = shininess;

        if (this.diffuseColor == null) {
            this.diffuseColor = new Vector3f(1f);
        }
        if (this.shininess == null) {
            this.shininess = 32.0f;
        }
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
        return diffuseEval;
    }

    @Override
    public Evaluable<Float, ColorCPUContext, ColorGPUContext> getShininess() {
        return shininessEval;
    }

    public String diffuseMapName() { return diffuseMapName; }

    public Vector3f diffuseColor() {return diffuseColor;}

    public String shininessMapName() {return shininessMapName;}

    public Float shininess() {return shininess;}

    public float scale() {return scale;}

    public void scale(float s) {
        this.scale = s;
    }

    private List<GLSLStatement> triplanarSampling(
            String resultName,
            String pointName,
            String normalName,
            String textureName
    ) {

        return List.of(
                var("vec2", resultName + "_Y", opMul(literal(scale), ref(pointName, "xz"))),
                var("vec2", resultName + "_X", opMul(literal(scale), ref(pointName, "zy"))),
                var("vec2", resultName + "_Z", opMul(literal(scale), ref(pointName, "xy"))),

                var("vec4", resultName + "_YColor", fn("texture", ref(textureName), ref(resultName + "_Y"))),
                var("vec4", resultName + "_XColor", fn("texture", ref(textureName), ref(resultName + "_X"))),
                var("vec4", resultName + "_ZColor", fn("texture", ref(textureName), ref(resultName + "_Z"))),

                var("vec3", resultName + "_W", pow(abs(ref(normalName)), vec3(sharpness, sharpness, sharpness))),
                opAssign(
                        ref(resultName + "_W"),
                        opDiv(
                                ref(resultName + "_W"),
                                paren(
                                        opPlus(
                                                opPlus(
                                                        ref(resultName + "_W", "x"),
                                                        ref(resultName + "_W", "y")
                                                ),
                                                ref(resultName + "_W", "z")
                                        )
                                )
                        )
                ),

                new GLSLIfStatement(
                        cmpLe(length(ref(resultName + "_W")), literal(0.001f)),
                        List.of(
                                opAssign(
                                        ref(resultName + "_W"),
                                        vec3(0, 1, 0)
                                )
                        ),
                        null
                ),

                var(
                        "vec4",
                        resultName,
                        opPlus(
                                opPlus(
                                        opMul(ref(resultName + "_XColor"), ref(resultName + "_W", "x")),
                                        opMul(ref(resultName + "_YColor"), ref(resultName + "_W", "y"))
                                ),
                                opMul(ref(resultName + "_ZColor"), ref(resultName + "_W", "z"))
                        )
                )
        );

    }

    final class ColorEvaluator implements Serializable,
            CPUEvaluator<Vector3f, ColorCPUContext>,
            GPUEvaluator<ColorGPUContext> {

        @Override
        public Vector3f evaluate(ColorCPUContext context) {
            return diffuseColor;
        }

        @Override
        public List<GLSLStatement> evaluate(ColorGPUContext context) {
            if (diffuseMapName != null) {

                List<GLSLStatement> result = new ArrayList<>();
                result.addAll(
                        triplanarSampling(
                                context.getContextId() + "_Triplanar",
                                context.getPointVariable(),
                                context.getNormalVariable(),
                                diffuseMapName
                        )
                );
                result.add(
                        var("vec3", context.getResult(), ref(context.getContextId() + "_Triplanar", "xyz"))
                );

                return result;
            } else {
                return List.of(
                        var(
                                "vec3",
                                context.getResult(),
                                vec3(diffuseColor)
                        )
                );
            }
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
            if (shininessMapName != null) {
                List<GLSLStatement> result = new ArrayList<>();
                result.addAll(
                        triplanarSampling(
                                context.getContextId() + "_Triplanar",
                                context.getPointVariable(),
                                context.getNormalVariable(),
                                shininessMapName
                        )
                );
                result.add(
                        var(
                                "float",
                                context.getResult(),
                                opMul(literal(200f), ref(context.getContextId() + "_Triplanar", "x"))
                        )
                );

                return result;
            } else {
                return List.of(
                        resultVar(
                                context,
                                literal(shininess)
                        )
                );
            }
        }
    }

}
