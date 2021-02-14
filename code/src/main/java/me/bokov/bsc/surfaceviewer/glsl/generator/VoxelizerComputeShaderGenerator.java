package me.bokov.bsc.surfaceviewer.glsl.generator;

import me.bokov.bsc.surfaceviewer.glsl.*;
import me.bokov.bsc.surfaceviewer.glsl.generator.BaseGLSLGenerator;
import me.bokov.bsc.surfaceviewer.glsl.generator.GeneratorOptions;
import me.bokov.bsc.surfaceviewer.scene.Materializer;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.sdf.threed.GPUEvaluationContext;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class VoxelizerComputeShaderGenerator extends BaseGLSLGenerator {

    public VoxelizerComputeShaderGenerator(
            World world
    ) {
        super(world);
    }

    private void addInterfaceTo(GLSLProgram prog) {

        prog.add(new GLSLRawStatement("precision highp float;"));
        prog.add(
                new GLSLRawStatement("layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;"),
                new GLSLRawStatement("layout(rgba32f, binding = 0) uniform image3D u_positionAndValueOutput;"),
                new GLSLRawStatement("layout(rgba32f, binding = 1) uniform image3D u_normalOutput;"),
                new GLSLUniformStatement("mat4", "u_transform", null)
        );
        prog.add(new GLSLUniformStatement("ivec3", "u_voxelOffset", null));
        prog.add(new GLSLUniformStatement("vec3", "u_voxelSize", null));

        prog.include("glsl/rm_sdfOp.glsl")
                .include("glsl/rm_noise.glsl");

    }

    @Override
    public String generateShaderSource(GeneratorOptions options) {

        GLSLProgram prog = getProgram();

        addInterfaceTo(prog);

        addCSGExecute();

        prog.include("glsl/rm_csgNormal.glsl")
                .include("glsl/vox_main.compute.glsl");

        return prog.render();
    }

}
