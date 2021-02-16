package me.bokov.bsc.surfaceviewer.scene.materializer;

import lombok.Builder;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.glsl.GLSLIfStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.scene.Materializer;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

@Builder
@Getter
public class TriplanarMaterial implements Materializer {

    private final int id;

    private SceneNode boundary;

    private String diffuseMapName;

    @Builder.Default
    private Vector3f diffuse = new Vector3f(1f);

    private String shininessMapName;

    @Builder.Default
    private float defaultShininess = 32.0f;

    private String normalMapName;

    @Builder.Default
    private float scale = 1.0f;
    @Builder.Default
    private float sharpness = 2.0f;


    private final Evaluable<Vector3f, ColorCPUContext, ColorGPUContext> diffuseEval = Evaluable.of(new ColorEvaluator());
    private final Evaluable<Float, ColorCPUContext, ColorGPUContext> shininessEval = Evaluable.of(new ShininessEvaluator());

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

    public Evaluable<Float, ColorCPUContext, ColorGPUContext> getShininess() {
        return shininessEval;
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
            return diffuse;
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
                                vec3(diffuse)
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
            return defaultShininess;
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
                                literal(defaultShininess)
                        )
                );
            }
        }
    }

}
