package me.bokov.bsc.surfaceviewer.render.blinnphong;

import me.bokov.bsc.surfaceviewer.glsl.*;
import me.bokov.bsc.surfaceviewer.scene.LightSource;
import me.bokov.bsc.surfaceviewer.scene.Materializer;
import me.bokov.bsc.surfaceviewer.scene.ResourceTexture;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.sdf.threed.ColorGPUEvaluationContext;
import me.bokov.bsc.surfaceviewer.sdf.threed.GPUEvaluationContext;

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
                new GLSLLayoutStatement(1, "in", "vec3", "v_normal")
        );

        prog.add(
                new GLSLUniformStatement("vec3", "u_eye", null),
                new GLSLUniformStatement("float", "u_shininess", literal(100.0f))
        );

        prog.add(
                new GLSLOutStatement("vec4", "out_finalColor")
        );

        for(ResourceTexture resourceTexture : world.getResourceTextures()) {
            prog.add(
                    new GLSLUniformStatement("sampler2D", resourceTexture.name(), null)
            );
        }

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

    private void addCSGColorAndShininess(GLSLProgram prog) {

        final GLSLFunctionStatement fColor = new GLSLFunctionStatement(
                "vec3",
                "csgColor",
                List.of(
                        new GLSLFunctionStatement.GLSLFunctionParameterStatement("in", "vec3", "P"),
                        new GLSLFunctionStatement.GLSLFunctionParameterStatement("in", "vec3", "N")
                ),
                new ArrayList<>()
        );
        final GLSLFunctionStatement fShininess = new GLSLFunctionStatement(
                "float",
                "csgShininess",
                List.of(
                        new GLSLFunctionStatement.GLSLFunctionParameterStatement("in", "vec3", "P"),
                        new GLSLFunctionStatement.GLSLFunctionParameterStatement("in", "vec3", "N")
                ),
                new ArrayList<>()
        );

        final var c = new ColorGPUEvaluationContext()
                .setNormalVariable("N");
        c.setContextId("Color")
                .setPointVariable("P");

        for (Materializer m : world.getMaterializers()) {

            final var mBoundaryCtx = c.branch(m.getId() + "B");
            final var mBoundaryList = m.getBoundary()
                    .toEvaluable()
                    .gpu().evaluate(mBoundaryCtx);

            final var mColorCtx = c.branch(m.getId() + "C");
            final var mShininessCtx = c.branch(m.getId() + "S");

            final var mColorList = m.getDiffuseColor().gpu().evaluate(mColorCtx);
            final var mShininessList = m.getShininess().gpu().evaluate(mShininessCtx);

            final List<GLSLStatement> colorIfBody = new ArrayList<>();
            final List<GLSLStatement> shininessIfBody = new ArrayList<>();

            colorIfBody.addAll(mColorList);
            colorIfBody.add(
                    new GLSLReturnStatement(ref(mColorCtx.getResult()))
            );

            shininessIfBody.addAll(mShininessList);
            shininessIfBody.add(
                    new GLSLReturnStatement(ref(mShininessCtx.getResult()))
            );

            final var colorIf = new GLSLIfStatement(
                    new GLSLBinaryExpressionStatement(
                            ref(mBoundaryCtx.getResult()),
                            literal(0.0f),
                            "<"
                    ),
                    colorIfBody,
                    null
            );
            final var shininessIf = new GLSLIfStatement(
                    new GLSLBinaryExpressionStatement(
                            ref(mBoundaryCtx.getResult()),
                            literal(0.0f),
                            "<"
                    ),
                    shininessIfBody,
                    null
            );

            mBoundaryList.forEach(fColor::body);
            fColor.body(colorIf);

            mBoundaryList.forEach(fShininess::body);
            fShininess.body(shininessIf);

        }

        fColor.body(new GLSLReturnStatement(vec3(0f, 0f, 0f)));
        fShininess.body(new GLSLReturnStatement(literal(0.0f)));

        prog.add(fColor, fShininess);

    }

    public String generateFragmentSource() {

        GLSLProgram prog = new GLSLProgram();

        addInterfaceTo(prog);

        prog.include("glsl/Ray.common.glsl")
                .include("glsl/Hit.common.glsl")
                .include("glsl/toneMap.common.glsl")
                .include("glsl/bp_shadowScene.glsl");

        addCalculateLighting(prog);
        addCSGColorAndShininess(prog);

        prog.include("glsl/illuminate.common.glsl")
                .include("glsl/bp_main.glsl");

        return prog.render();

    }

}
