package me.bokov.bsc.surfaceviewer.compute;

import me.bokov.bsc.surfaceviewer.glsl.*;
import me.bokov.bsc.surfaceviewer.scene.Materializer;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.sdf.threed.GPUEvaluationContext;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class VoxelizerComputeShaderGenerator {

    private final World world;

    public VoxelizerComputeShaderGenerator(
            World world
    ) {
        this.world = world;
    }

    private void addInterfaceTo(GLSLProgram prog) {

        prog.add(new GLSLRawStatement("precision highp float;"));
        prog.add(
                new GLSLRawStatement("layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;"),
                new GLSLRawStatement("layout(rgba32f, binding = 0) uniform image3D u_positionAndValueOutput;"),
                new GLSLRawStatement("layout(rgba32f, binding = 1) uniform image3D u_normalOutput;"),
                new GLSLRawStatement("layout(rgba32f, binding = 2) uniform image3D u_colorShininessOutput;"),
                new GLSLUniformStatement("mat4", "u_transform", null)
        );
        prog.add(new GLSLUniformStatement("ivec3", "u_voxelOffset", null));
        prog.add(new GLSLUniformStatement("vec3", "u_voxelSize", null));

        prog.include("glsl/rm_sdfOp.glsl")
                .include("glsl/rm_noise.glsl");

    }

    private void addCSGExecute(GLSLProgram prog) {

        final var generator = world.toEvaluable();

        final GLSLFunctionStatement csgExecuteFunction = new GLSLFunctionStatement(
                "float",
                "csgExecute",
                List.of(new GLSLFunctionStatement.GLSLFunctionParameterStatement("", "vec3", "CSG_InputPoint")),
                Collections.emptyList()
        );
        final GPUEvaluationContext expressionEvaluationContext = new GPUEvaluationContext()
                .setPointVariable("CSG_InputPoint")
                .setContextId("CSG_Root");

        if (generator != null) {
            csgExecuteFunction.body(
                    generator.gpu().evaluate(expressionEvaluationContext)
                            .toArray(new GLSLStatement[0])
            );
            csgExecuteFunction.body(
                    new GLSLReturnStatement(
                            new GLSLRawStatement(expressionEvaluationContext.getResult()))
            );
        } else {
            csgExecuteFunction.body(
                    new GLSLReturnStatement(literal(1.0f))
            );
        }

        prog.add(csgExecuteFunction);

    }

    private void addCSGColorAndShininess(GLSLProgram prog) {

        final GLSLFunctionStatement fColor = new GLSLFunctionStatement(
                "vec3",
                "csgColor",
                List.of(
                        new GLSLFunctionStatement.GLSLFunctionParameterStatement("in", "vec3", "P")
                ),
                new ArrayList<>()
        );
        final GLSLFunctionStatement fShininess = new GLSLFunctionStatement(
                "float",
                "csgShininess",
                List.of(
                        new GLSLFunctionStatement.GLSLFunctionParameterStatement("in", "vec3", "P")
                ),
                new ArrayList<>()
        );

        final var c = new GPUEvaluationContext()
                .setContextId("Color").setPointVariable("P");

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

    public String generateVoxelizerComputeShaderSource() {

        GLSLProgram prog = new GLSLProgram("460");

        addInterfaceTo(prog);

        addCSGExecute(prog);

        addCSGColorAndShininess(prog);

        prog.include("glsl/rm_csgNormal.glsl")
                .include("glsl/vox_main.compute.glsl");

        return prog.render();
    }

}
