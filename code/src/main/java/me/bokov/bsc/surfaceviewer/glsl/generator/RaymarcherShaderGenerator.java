package me.bokov.bsc.surfaceviewer.glsl.generator;

import me.bokov.bsc.surfaceviewer.glsl.*;
import me.bokov.bsc.surfaceviewer.glsl.GLSLFunctionStatement.GLSLFunctionParameterStatement;
import me.bokov.bsc.surfaceviewer.glsl.generator.BaseGLSLGenerator;
import me.bokov.bsc.surfaceviewer.glsl.generator.GeneratorOptions;
import me.bokov.bsc.surfaceviewer.scene.LightSource;
import me.bokov.bsc.surfaceviewer.scene.Materializer;
import me.bokov.bsc.surfaceviewer.scene.ResourceTexture;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.sdf.threed.ColorGPUEvaluationContext;
import me.bokov.bsc.surfaceviewer.sdf.threed.GPUEvaluationContext;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class RaymarcherShaderGenerator extends BaseGLSLGenerator {

    public RaymarcherShaderGenerator(
            World world
    ) {
        super(world);
    }

    private void addInterfaceTo(GLSLProgram prog) {

        prog.add(new GLSLRawStatement("precision highp float;"));

        prog.add(
                new GLSLLayoutStatement(0, "in", "vec2", "v_UV")
        );

        prog.add(
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

        for(ResourceTexture resourceTexture : getWorld().getResourceTextures()) {
            prog.add(
                    new GLSLUniformStatement("sampler2D", resourceTexture.name(), null)
            );
        }

    }

    @Override
    public String generateShaderSource(GeneratorOptions options) {

        GLSLProgram prog = getProgram();

        addInterfaceTo(prog);

        prog.include("glsl/Ray.common.glsl")
                .include("glsl/Hit.common.glsl")
                .include("glsl/rm_sdfOp.glsl")
                .include("glsl/rm_noise.glsl")
                .include("glsl/toneMap.common.glsl")
                .include("glsl/rm_constants.glsl");

        addCSGExecute();

        prog.include("glsl/rm_shadowScene.glsl");
        prog.add(new GLSLRawStatement("#define ILLUM_FIND_MATERIAL 1"));
        addCSGColor();
        addCSGShininess();

        prog.include("glsl/rm_rayDir.glsl")
                .include("glsl/rm_missed.glsl")
                .include("glsl/rm_csgNormal.glsl")
                .include("glsl/rm_hitScene.glsl");

        addCalculateLighting();

        prog.include("glsl/illuminate.common.glsl")
                .include("glsl/rm_main.glsl");

        return prog.render();

    }

}
