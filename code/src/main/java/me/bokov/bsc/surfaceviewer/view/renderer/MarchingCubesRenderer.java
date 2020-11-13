package me.bokov.bsc.surfaceviewer.view.renderer;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.View;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.mesh.mccpu.MarchingCubes;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.IOUtil;
import me.bokov.bsc.surfaceviewer.util.Resources;
import me.bokov.bsc.surfaceviewer.view.Renderer;
import me.bokov.bsc.surfaceviewer.view.RendererConfig;
import me.bokov.bsc.surfaceviewer.voxelization.CPUVoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGrid;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGridVoxelizer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

public class MarchingCubesRenderer implements Renderer {

    private static final Matrix4f IDENTITY = new Matrix4f().identity();

    private View view = null;

    private Voxelizer3D<UniformGrid> voxelizer;
    private UniformGrid voxelStorage;
    private MarchingCubes marchingCubes;
    private Drawable mesh;
    private ShaderProgram shaderProgram;
    @Getter
    private Config config = new Config()
            .setGridWidth(64).setGridHeight(64).setGridDepth(64)
            .setGridOffset(new Vector3f(-2f)).setGridScale(new Vector3f(4f))
            .setLightEnergy(new Vector3f(1.0f))
            .setLightAmbient(new Vector3f(0.2f))
            .setLightDirection(new Vector3f(-1.5f, 2.3f, 1.8f).normalize())
            .setIsoLevel(0.0f);

    private void voxelizeScene(World world) {

        if (this.voxelizer != null) {
            this.voxelizer.tearDown();
            this.voxelizer = null;
        }
        if (this.voxelStorage != null) {
            this.voxelStorage.tearDown();
            this.voxelStorage = null;
        }

        final var generator = world.toEvaluable();

        if (generator != null) {
            this.voxelizer = new UniformGridVoxelizer(
                    config.getGridWidth(),
                    config.getGridHeight(),
                    config.getGridDepth()
            );
            this.voxelStorage = this.voxelizer.voxelize(
                    generator,
                    new MeshTransform(
                            config.getGridOffset(),
                            new Vector3f(0f, 1f, 0f),
                            0f,
                            Math.max(
                                    config.getGridScale().x,
                                    Math.max(
                                            config.getGridScale().y,
                                            config.getGridScale().z
                                    )
                            )
                    ), new CPUVoxelizationContext()
            );
        }
    }

    private void executeMarchingCubes() {

        if (this.mesh != null) {
            this.mesh.tearDown();
            this.mesh = null;
        }

        if (this.voxelStorage != null) {

            this.marchingCubes = new MarchingCubes(config.getIsoLevel());
            this.mesh = this.marchingCubes
                    .generate(voxelStorage);

        }

    }

    @Override
    public void render(World world) {

        if (this.mesh == null) {

            this.voxelizeScene(world);
            this.executeMarchingCubes();

        }

        if (this.mesh != null) {

            this.shaderProgram.use();

            this.shaderProgram.uniform("u_Le").vec3(config.getLightEnergy());
            this.shaderProgram.uniform("u_La").vec3(config.getLightAmbient());
            this.shaderProgram.uniform("u_Ld").vec3(config.getLightDirection());
            this.shaderProgram.uniform("u_eye").vec3(this.view.getCamera().eye());
            this.shaderProgram.uniform("u_M").mat4(IDENTITY);
            this.shaderProgram.uniform("u_MVP").mat4(this.view.getCamera().VP());

            this.mesh.draw();

        }

    }

    @Override
    public void install(View parent) {
        this.view = parent;

        this.shaderProgram = parent.getShaderManager().load("default")
                .vertexFromResource(
                        Resources.GLSL_VERTEX_STANDARD_3D_TRANSFORMED)
                .fragmentFromResource(Resources.GLSL_FRAGMENT_BLINN_PHONG)
                .end();

        this.view.getApp().onViewReport(
                "RendererInstalled",
                Map.of("config", IOUtil.serialize(this.getConfig()))
        );

    }

    @Override
    public void configure(RendererConfig config) {
        this.config = (MarchingCubesRenderer.Config) config;
        this.view.getApp().onViewReport(
                "RendererConfigured",
                Map.of("config", IOUtil.serialize(this.getConfig()))
        );

        this.mesh = null;
    }

    @Override
    public void uninstall() {

        if (marchingCubes != null) { marchingCubes.tearDown(); }

        if (voxelizer != null) { voxelizer.tearDown(); }

        if (mesh != null) { mesh.tearDown(); }

        if (voxelStorage != null) { voxelStorage.tearDown(); }

        this.voxelizer = null;
        this.voxelStorage = null;
        this.marchingCubes = null;
        this.mesh = null;

        this.view = null;

    }

    @Data
    @Accessors(chain = true)
    public static class Config implements RendererConfig {

        private Integer gridWidth, gridHeight, gridDepth;
        private Float isoLevel;
        private Vector3f gridOffset, gridScale;
        private Vector3f lightEnergy, lightAmbient, lightDirection;

    }

}
