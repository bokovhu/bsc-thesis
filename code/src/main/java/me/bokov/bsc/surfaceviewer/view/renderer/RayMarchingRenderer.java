package me.bokov.bsc.surfaceviewer.view.renderer;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.View;
import me.bokov.bsc.surfaceviewer.glsl.generator.GeneratorOptions;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.Drawables;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.glsl.generator.RaymarcherShaderGenerator;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.util.IOUtil;
import me.bokov.bsc.surfaceviewer.util.ResourceUtil;
import me.bokov.bsc.surfaceviewer.view.BaseRenderer;
import me.bokov.bsc.surfaceviewer.view.RendererConfig;

import java.util.*;

public class RayMarchingRenderer extends BaseRenderer {

    private ShaderProgram rayMarchingProgram = null;
    private RaymarcherShaderGenerator shaderGenerator = null;
    private Drawable fullScreenQuad = null;
    @Getter
    private Config config = new Config();

    @Override
    public void tearDown() {

        if (this.rayMarchingProgram != null) { this.rayMarchingProgram.tearDown(); }

        this.rayMarchingProgram = null;
        this.shaderGenerator = null;

    }

    @Override
    public void install(View view) {
        super.install(view);
        fullScreenQuad = Drawables.fullScreenQuad();

    }

    @Override
    public void configure(RendererConfig config) {
        this.config = (RayMarchingRenderer.Config) config;
        this.view.getApp().onViewReport(
                "RendererConfigured",
                Map.of("config", IOUtil.serialize(this.getConfig()))
        );
    }

    @Override
    public void render(World world) {

        if (this.rayMarchingProgram == null) {

            final Evaluable<Float, CPUContext, GPUContext> evaluable = world.toEvaluable();

            if (evaluable != null) {

                this.shaderGenerator = new RaymarcherShaderGenerator(world);
                this.rayMarchingProgram = new ShaderProgram();
                this.rayMarchingProgram.init();
                this.rayMarchingProgram.attachVertexShaderFromSource(
                        ResourceUtil.readResource("glsl/directPassthrough.vertex.glsl")
                );
                this.rayMarchingProgram.attachFragmentShaderFromSource(
                        this.shaderGenerator.generateShaderSource(new GeneratorOptions())
                );

                try {

                    this.rayMarchingProgram.linkAndValidate();

                } catch (Exception ignored) {
                    this.rayMarchingProgram.tearDown();
                    this.rayMarchingProgram = null;
                }

            }

        }

        if (this.rayMarchingProgram != null) {

            this.rayMarchingProgram.use();

            this.rayMarchingProgram.uniform("u_eye").vec3(view.getCamera().eye());
            this.rayMarchingProgram.uniform("u_forward").vec3(view.getCamera().forward());
            this.rayMarchingProgram.uniform("u_right").vec3(view.getCamera().right());
            this.rayMarchingProgram.uniform("u_up").vec3(view.getCamera().up());
            this.rayMarchingProgram.uniform("u_aspect").f1(view.getCamera().aspectRatio());
            this.rayMarchingProgram.uniform("u_fovy").f1(view.getCamera().fovTan());

            applyWorldResourcesToProgram(this.rayMarchingProgram, world);

            this.fullScreenQuad.draw();

        }

    }

    @Data
    @Accessors(chain = true)
    public static class Config implements RendererConfig {

    }

}
