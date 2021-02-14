package me.bokov.bsc.surfaceviewer.glsl.generator;

import me.bokov.bsc.surfaceviewer.glsl.*;
import me.bokov.bsc.surfaceviewer.scene.World;

public class MarchingCubesShaderGenerator extends BaseGLSLGenerator {

    public MarchingCubesShaderGenerator(World world) {
        super(world);
    }

    private void addInterfaceTo(GLSLProgram prog) {

        prog.add(new GLSLRawStatement("precision highp float;"));
        prog.add(
                new GLSLRawStatement("layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;")
        );

        prog.include("glsl/OutputVertex.mc.glsl");
        prog.include("glsl/mc_interface.glsl");
        prog.include("glsl/mc_constants.glsl");

    }

    @Override
    public String generateShaderSource(GeneratorOptions options) {

        final GLSLProgram prog = getProgram();

        addInterfaceTo(prog);

        prog.include("glsl/rm_sdfOp.glsl")
                .include("glsl/rm_noise.glsl");

        addCSGExecute();

        prog.include("glsl/rm_csgNormal.glsl");

        prog.include("glsl/mc_main.compute.glsl");

        return prog.render();
    }

}
