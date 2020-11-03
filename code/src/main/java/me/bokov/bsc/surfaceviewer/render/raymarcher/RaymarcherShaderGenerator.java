package me.bokov.bsc.surfaceviewer.render.raymarcher;

import me.bokov.bsc.surfaceviewer.glsl.*;
import me.bokov.bsc.surfaceviewer.glsl.GLSLFunctionStatement.GLSLFunctionParameterStatement;
import me.bokov.bsc.surfaceviewer.render.Texture;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.threed.GPUEvaluationContext;

import java.util.*;

public class RaymarcherShaderGenerator {

    private final Evaluatable<Float, CPUContext, GPUContext> distanceExpression;
    private final Map<String, Texture> textureMap;

    public RaymarcherShaderGenerator(
            Evaluatable<Float, CPUContext, GPUContext> distanceExpression,
            Map<String, Texture> textureMap
    ) {
        this.distanceExpression = distanceExpression;
        this.textureMap = textureMap;
    }

    private void addInterfaceTo(GLSLProgram prog) {

        prog.add(new GLSLRawStatement("precision highp float;"));

        prog.add(
                new GLSLVaryingStatement("vec2", "v_UV"),
                new GLSLVaryingStatement("vec3", "v_rayDir")
        );

        prog.add(
                new GLSLUniformStatement("vec3", "u_Le", prog.raw("vec3(1.0)")),
                new GLSLUniformStatement("vec3", "u_Ls", prog.raw("vec3(1.0)")),
                new GLSLUniformStatement("vec3", "u_La", prog.raw("vec3(0.2)")),
                new GLSLUniformStatement(
                        "vec3", "u_Ld", prog.raw("normalize(vec3(-1.5, 2.5, 2.5))")),
                new GLSLUniformStatement("vec3", "u_eye", prog.raw("vec3(5.0)")),
                new GLSLUniformStatement(
                        "vec3", "u_forward", prog.raw("normalize(vec3(-1.0, -1.0, -1.0))")),
                new GLSLUniformStatement(
                        "vec3", "u_right", prog.raw(
                        "normalize(cross(vec3(0.0, 1.0, 0.0), normalize(vec3(-1.0, -1.0, -1.0))))")),
                new GLSLUniformStatement(
                        "vec3", "u_up", prog.raw(
                        "normalize(cross( normalize(cross(vec3(0.0, 1.0, 0.0), normalize(vec3(-1.0, -1.0, -1.0)))), normalize(vec3(-1.0, -1.0, -1.0)) ))")),
                new GLSLUniformStatement("float", "u_aspect", prog.raw("1.0")),
                new GLSLUniformStatement("float", "u_fovy", prog.raw("2.0"))
        );

        prog.add(
                new GLSLOutStatement("vec4", "out_finalColor")
        );

    }

    public String generateRaymarcherFragmentSource() {

        GLSLProgram prog = new GLSLProgram();

        addInterfaceTo(prog);

        prog.include("glsl/rm_Ray.glsl")
                .include("glsl/rm_Hit.glsl")
                .include("glsl/rm_sdfOp.glsl")
                .include("glsl/rm_noise.glsl");

        final GLSLFunctionStatement csgExecuteFunction = new GLSLFunctionStatement(
                "float",
                "csgExecute",
                List.of(new GLSLFunctionParameterStatement("", "vec3", "CSG_InputPoint")),
                Collections.emptyList()
        );
        final GPUEvaluationContext expressionEvaluationContext = new GPUEvaluationContext()
                .setPointVariable("CSG_InputPoint")
                .setContextId("CSG_Root");

//        for (var e : textureMap.entrySet()) {
//
//            final String uniformName = "u_texture" + e.getKey();
//
//            expressionEvaluationContext.withTexture(e.getKey(), e.getValue());
//            expressionEvaluationContext.withTextureUniform(e.getKey(), uniformName);
//
//            final var t = e.getValue();
//
//            switch (t.textureType()) {
//                case Tex1D:
//                    prog.add(new GLSLUniformStatement("sampler1D", uniformName, null));
//                    break;
//                case Tex2D:
//                    prog.add(new GLSLUniformStatement("sampler2D", uniformName, null));
//                    break;
//                case Tex3D:
//                    prog.add(new GLSLUniformStatement("sampler3D", uniformName, null));
//                    break;
//            }
//
//        }

        csgExecuteFunction.body(
                distanceExpression.gpu().evaluate(expressionEvaluationContext)
                        .toArray(new GLSLStatement[0])
        );
        csgExecuteFunction.body(
                new GLSLReturnStatement(
                        new GLSLRawStatement(expressionEvaluationContext.getResult()))
        );

        prog.add(csgExecuteFunction);

        prog.include("glsl/rm_rayDir.glsl")
                .include("glsl/rm_missed.glsl")
                .include("glsl/rm_toneMap.glsl")
                .include("glsl/rm_csgNormal.glsl")
                .include("glsl/rm_csgColor.glsl")
                .include("glsl/rm_csgShininess.glsl")
                .include("glsl/rm_hitScene.glsl")
                .include("glsl/rm_illuminate.glsl")
                .include("glsl/rm_main.glsl");

        return prog.render();

    }

}
