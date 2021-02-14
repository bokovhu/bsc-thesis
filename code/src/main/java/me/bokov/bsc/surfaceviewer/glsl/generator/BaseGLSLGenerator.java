package me.bokov.bsc.surfaceviewer.glsl.generator;

import me.bokov.bsc.surfaceviewer.glsl.*;
import me.bokov.bsc.surfaceviewer.scene.LightSource;
import me.bokov.bsc.surfaceviewer.scene.Materializer;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.sdf.threed.ColorGPUEvaluationContext;
import me.bokov.bsc.surfaceviewer.sdf.threed.GPUEvaluationContext;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public abstract class BaseGLSLGenerator {

    public static final String GLSL_VERSION = "460";

    private final World world;
    private final GLSLProgram program = new GLSLProgram(GLSL_VERSION);

    protected BaseGLSLGenerator(World world) {
        this.world = world;
    }

    protected World getWorld() {
        return this.world;
    }

    protected GLSLProgram getProgram() {
        return this.program;
    }

    protected final void addCSGExecute() {

        final GLSLFunctionStatement csgExecuteFunction = new GLSLFunctionStatement(
                "float",
                "csgExecute",
                List.of(new GLSLFunctionStatement.GLSLFunctionParameterStatement("", "vec3", "CSG_InputPoint")),
                Collections.emptyList()
        );
        final GPUEvaluationContext expressionEvaluationContext = new GPUEvaluationContext()
                .setPointVariable("CSG_InputPoint")
                .setContextId("CSG_Root");

        csgExecuteFunction.body(
                world.toEvaluable().gpu().evaluate(expressionEvaluationContext)
                        .toArray(new GLSLStatement[0])
        );
        csgExecuteFunction.body(
                new GLSLReturnStatement(
                        new GLSLRawStatement(expressionEvaluationContext.getResult()))
        );

        program.add(csgExecuteFunction);

    }

    protected final void addCalculateLighting() {

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

        for (LightSource ls : getWorld().getLightSources()) {
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

        program.add(calculateLightingFunction);

    }

    protected final void addCSGShininess() {

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
        c.setContextId("Shininess")
                .setPointVariable("P");

        for (Materializer m : getWorld().getMaterializers()) {

            final var mBoundaryCtx = c.branch(m.getId() + "B");
            final var mBoundaryList = m.getBoundary()
                    .toEvaluable()
                    .gpu().evaluate(mBoundaryCtx);

            final var mShininessCtx = c.branch(m.getId() + "S");

            final var mShininessList = m.getShininess().gpu().evaluate(mShininessCtx);

            final List<GLSLStatement> shininessIfBody = new ArrayList<>();

            shininessIfBody.addAll(mShininessList);
            shininessIfBody.add(
                    new GLSLReturnStatement(ref(mShininessCtx.getResult()))
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

            mBoundaryList.forEach(fShininess::body);
            fShininess.body(shininessIf);

        }

        fShininess.body(new GLSLReturnStatement(literal(0.0f)));

        program.add(fShininess);
    }

    protected final void addCSGColor() {

        final GLSLFunctionStatement fColor = new GLSLFunctionStatement(
                "vec3",
                "csgColor",
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

        for (Materializer m : getWorld().getMaterializers()) {

            final var mBoundaryCtx = c.branch(m.getId() + "B");
            final var mBoundaryList = m.getBoundary()
                    .toEvaluable()
                    .gpu().evaluate(mBoundaryCtx);

            final var mColorCtx = c.branch(m.getId() + "C");

            final var mColorList = m.getDiffuseColor().gpu().evaluate(mColorCtx);

            final List<GLSLStatement> colorIfBody = new ArrayList<>();

            colorIfBody.addAll(mColorList);
            colorIfBody.add(
                    new GLSLReturnStatement(ref(mColorCtx.getResult()))
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

            mBoundaryList.forEach(fColor::body);
            fColor.body(colorIf);

        }

        fColor.body(new GLSLReturnStatement(vec3(0f, 0f, 0f)));

        program.add(fColor);

    }

    public abstract String generateShaderSource(GeneratorOptions options);

}
