package me.bokov.bsc.surfaceviewer.scene;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class AmbientLight extends BaseLightSource {

    public AmbientLight(int id) {
        super(id);
    }

    @Override
    public List<GLSLStatement> evaluateContribution(ContributionEvaluationContext context) {
        return List.of(
                new GLSLVariableDeclarationStatement(
                        "vec3", context.getResult(),
                        opMul(
                                ref(context.getDiffuseColor()),
                                vec3(getEnergy())
                        )
                )
        );
    }
}
