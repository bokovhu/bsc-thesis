package me.bokov.bsc.surfaceviewer.glsl.generator;

import me.bokov.bsc.surfaceviewer.glsl.*;
import me.bokov.bsc.surfaceviewer.glsl.generator.BaseGLSLGenerator;
import me.bokov.bsc.surfaceviewer.glsl.generator.GeneratorOptions;
import me.bokov.bsc.surfaceviewer.scene.ResourceTexture;
import me.bokov.bsc.surfaceviewer.scene.World;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class BlinnPhongShaderGenerator extends BaseGLSLGenerator {

    public BlinnPhongShaderGenerator(World world) {
        super(world);
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

        for (ResourceTexture resourceTexture : getWorld().getResourceTextures()) {
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
                .include("glsl/toneMap.common.glsl")
                .include("glsl/bp_shadowScene.glsl");

        addCSGColor();
        addCSGShininess();
        addCalculateLighting();

        prog.include("glsl/illuminate.common.glsl")
                .include("glsl/bp_main.glsl");

        return prog.render();

    }

}
