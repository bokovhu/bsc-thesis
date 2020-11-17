package me.bokov.bsc.surfaceviewer.scene;

import lombok.Getter;
import me.bokov.bsc.surfaceviewer.glsl.GLSLBinaryExpressionStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLIfStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class DirectionalLight extends BaseLightSource {

    @Getter
    private final Vector3f direction = new Vector3f();

    public DirectionalLight(int id) {
        super(id);
    }

    public DirectionalLight dir(float x, float y, float z) {
        direction.set(x, y, z).normalize();
        return this;
    }

    public DirectionalLight dir(Vector3f v) {
        direction.set(v).normalize();
        return this;
    }

    @Override
    public List<GLSLStatement> evaluateContribution(ContributionEvaluationContext context) {
        return List.of(
                new GLSLVariableDeclarationStatement(
                        "vec3", context.getContextId() + "_Contrib",
                        vec3(0.0f, 0.0f, 0.0f)
                ),
                new GLSLVariableDeclarationStatement(
                        "vec3", context.getContextId() + "_ViewDir",
                        normalize(opMinus(ref(context.getEye()), ref(context.getHitPoint())))
                ),
                new GLSLVariableDeclarationStatement(
                        "float", context.getContextId() + "_CosTheta",
                        dot(ref(context.getHitNormal()), vec3(direction))
                ),
                new GLSLVariableDeclarationStatement(
                        "float", context.getContextId() + "_Shadow",
                        fn(
                                "shadowScene",
                                opPlus(ref(context.getHitPoint()), opMul(ref(context.getHitNormal()), literal(0.01f))),
                                vec3(direction)
                        )
                ),
                new GLSLIfStatement(
                        new GLSLBinaryExpressionStatement(
                                ref(context.getContextId() + "_CosTheta"),
                                literal(0.0f),
                                ">"
                        ),
                        List.of(
                                new GLSLBinaryExpressionStatement(
                                        ref(context.getContextId() + "_Contrib"),
                                        opMul(
                                                opMul(vec3(getEnergy()), ref(context.getDiffuseColor())),
                                                ref(context.getContextId() + "_CosTheta")
                                        ),
                                        "+="
                                ),
                                new GLSLVariableDeclarationStatement(
                                        "vec3", context.getContextId() + "_HalfwayDir",
                                        normalize(opPlus(vec3(direction), ref(context.getContextId() + "_ViewDir")))
                                ),
                                new GLSLVariableDeclarationStatement(
                                        "float", context.getContextId() + "_CosDelta",
                                        dot(ref(context.getHitNormal()), ref(context.getContextId() + "_HalfwayDir"))
                                ),
                                new GLSLIfStatement(
                                        new GLSLBinaryExpressionStatement(
                                                ref(context.getContextId() + "_CosDelta"),
                                                literal(0.0f),
                                                ">"
                                        ),
                                        List.of(
                                                new GLSLBinaryExpressionStatement(
                                                        ref(context.getContextId() + "_Contrib"),
                                                        opMul(
                                                                opMul(
                                                                        vec3(getEnergy()),
                                                                        ref(context.getDiffuseColor())
                                                                ),
                                                                pow(
                                                                        ref(context.getContextId() + "_CosDelta"),
                                                                        ref(context.getShininess())
                                                                )
                                                        ),
                                                        "+="
                                                )
                                        ),
                                        null
                                )
                        ),
                        null
                ),
                new GLSLVariableDeclarationStatement(
                        "vec3", context.getResult(),
                        opMul(
                                ref(context.getContextId() + "_Contrib"),
                                ref(context.getContextId() + "_Shadow")
                        )
                )
        );
    }
}
