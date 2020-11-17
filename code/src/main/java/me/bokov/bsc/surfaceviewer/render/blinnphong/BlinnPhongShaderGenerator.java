package me.bokov.bsc.surfaceviewer.render.blinnphong;

import me.bokov.bsc.surfaceviewer.glsl.*;
import me.bokov.bsc.surfaceviewer.scene.LightSource;
import me.bokov.bsc.surfaceviewer.scene.World;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class BlinnPhongShaderGenerator {

    private final World world;

    public BlinnPhongShaderGenerator(World world) {
        this.world = world;
    }

    private void addInterfaceTo(GLSLProgram prog) {

        prog.add(new GLSLRawStatement("precision highp float;"));

        prog.add(
                new GLSLLayoutStatement(0, "in", "vec3", "v_worldPosition"),
                new GLSLLayoutStatement(1, "in", "vec3", "v_normal"),
                new GLSLLayoutStatement(2, "in", "vec4", "v_color")
        );

        prog.add(
                new GLSLUniformStatement("vec3", "u_eye", null),
                new GLSLUniformStatement("float", "u_shininess", literal(100.0f))
        );

        prog.add(
                new GLSLOutStatement("vec4", "out_finalColor")
        );

    }

    private void addCalculateLighting(GLSLProgram prog) {

        final GLSLFunctionStatement calculateLightingFunction = new GLSLFunctionStatement(
                "vec3",
                "calculateLighting",
                List.of(
                        new GLSLFunctionStatement.GLSLFunctionParameterStatement("in", "vec3", "P"),
                        new GLSLFunctionStatement.GLSLFunctionParameterStatement("in", "vec3", "N"),
                        new GLSLFunctionStatement.GLSLFunctionParameterStatement("in", "vec3", "Kd"),
                        new GLSLFunctionStatement.GLSLFunctionParameterStatement("in", "float", "shininess")
                ),
                new ArrayList<>()
        );

        final var calculateLightingContext = new LightSource.ContributionEvaluationContext()
                .setContextId("L")
                .setEye("u_eye").setDiffuseColor("Kd").setShininess("shininess")
                .setHitPoint("P").setHitNormal("N");

        calculateLightingFunction.body(
                new GLSLVariableDeclarationStatement(
                        "vec3", calculateLightingContext.getResult(),
                        vec3(0.0f, 0.0f, 0.0f)
                )
        );

        for (LightSource ls : world.getLightSources()) {
            final var c = calculateLightingContext.branch(ls.getId() + "");
            List<GLSLStatement> lsList = ls.evaluateContribution(
                    c
            );
            lsList.forEach(calculateLightingFunction::body);
            calculateLightingFunction.body(
                    new GLSLBinaryExpressionStatement(
                            ref(calculateLightingContext.getResult()),
                            ref(c.getResult()),
                            "+="
                    )
            );
        }

        calculateLightingFunction.body(
                new GLSLReturnStatement(ref(calculateLightingContext.getResult()))
        );

        prog.add(calculateLightingFunction);

    }

    public String generateFragmentSource() {

        GLSLProgram prog = new GLSLProgram();

        addInterfaceTo(prog);

        prog.include("glsl/Ray.common.glsl")
                .include("glsl/Hit.common.glsl")
                .include("glsl/toneMap.common.glsl")
                .include("glsl/bp_shadowScene.glsl");

        addCalculateLighting(prog);

        prog.include("glsl/illuminate.common.glsl")
                .include("glsl/bp_main.glsl");

        return prog.render();

    }

}
