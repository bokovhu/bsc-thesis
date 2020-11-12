package me.bokov.bsc.surfaceviewer.compute;

import me.bokov.bsc.surfaceviewer.glsl.*;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.threed.GPUEvaluationContext;

import java.util.*;

public class VoxelizerComputeShaderGenerator {

    private final Evaluable<Float, CPUContext, GPUContext> distanceExpression;

    public VoxelizerComputeShaderGenerator(Evaluable<Float, CPUContext, GPUContext> distanceExpression) {
        this.distanceExpression = distanceExpression;
    }

    private void addInterfaceTo(GLSLProgram prog) {

        prog.add(new GLSLRawStatement("precision highp float;"));
        prog.add(
                new GLSLRawStatement("layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;"),
                new GLSLRawStatement("layout(rgba32f, binding = 0) uniform image3D u_positionAndValueOutput;"),
                new GLSLRawStatement("layout(rgba32f, binding = 1) uniform image3D u_normalOutput;"),
                new GLSLUniformStatement("mat4", "u_transform", null)
        );

    }

    public String generateVoxelizerComputeShaderSource() {

        GLSLProgram prog = new GLSLProgram("460");

        addInterfaceTo(prog);

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
                distanceExpression.gpu().evaluate(expressionEvaluationContext)
                        .toArray(new GLSLStatement[0])
        );
        csgExecuteFunction.body(
                new GLSLReturnStatement(
                        new GLSLRawStatement(expressionEvaluationContext.getResult()))
        );

        prog.add(csgExecuteFunction);

        prog.include("glsl/rm_csgNormal.glsl")
                .include("glsl/vox_main.compute.glsl");

        return prog.render();
    }

}
