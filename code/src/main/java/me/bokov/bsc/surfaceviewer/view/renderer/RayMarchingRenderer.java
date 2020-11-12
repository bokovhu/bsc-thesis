package me.bokov.bsc.surfaceviewer.view.renderer;

import lombok.Data;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.View;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.Drawables;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.render.raymarcher.RaymarcherShaderGenerator;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.util.ResourceUtil;
import me.bokov.bsc.surfaceviewer.view.Renderer;
import me.bokov.bsc.surfaceviewer.view.RendererConfig;
import org.joml.Vector3f;

import java.util.*;

public class RayMarchingRenderer implements Renderer {

    private ShaderProgram rayMarchingProgram = null;
    private RaymarcherShaderGenerator shaderGenerator = null;
    private Drawable fullScreenQuad = null;
    private Config config = new Config();
    private View view = null;

    @Override
    public void uninstall() {

        if (this.rayMarchingProgram != null) { this.rayMarchingProgram.tearDown(); }

        this.rayMarchingProgram = null;
        this.shaderGenerator = null;

    }

    @Override
    public void install(View view) {

        this.view = view;

        fullScreenQuad = Drawables.fullScreenQuad();

    }

    @Override
    public void configure(RendererConfig config) {
        this.config = (RayMarchingRenderer.Config) config;
    }

    @Override
    public void render(World world) {

        if (this.rayMarchingProgram == null) {

            final Evaluable<Float, CPUContext, GPUContext> evaluable = world.toEvaluable();

            if (evaluable != null) {

                this.shaderGenerator = new RaymarcherShaderGenerator(evaluable, new HashMap<>());
                this.rayMarchingProgram = new ShaderProgram();
                this.rayMarchingProgram.init();
                this.rayMarchingProgram.attachVertexShaderFromSource(
                        ResourceUtil.readResource("glsl/directPassthrough.vertex.glsl")
                );
                this.rayMarchingProgram.attachFragmentShaderFromSource(
                        this.shaderGenerator.generateRaymarcherFragmentSource()
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

            this.rayMarchingProgram.uniform("u_Le").vec3(config.getLightEnergy());
            this.rayMarchingProgram.uniform("u_Ls").vec3(config.getLightSpecular());
            this.rayMarchingProgram.uniform("u_La").vec3(config.getLightAmbient());
            this.rayMarchingProgram.uniform("u_Ld").vec3(config.getLightDirection());

            this.rayMarchingProgram.uniform("u_eye").vec3(view.getCamera().eye());
            this.rayMarchingProgram.uniform("u_forward").vec3(view.getCamera().forward());
            this.rayMarchingProgram.uniform("u_right").vec3(view.getCamera().right());
            this.rayMarchingProgram.uniform("u_up").vec3(view.getCamera().up());
            this.rayMarchingProgram.uniform("u_aspect").f1(view.getCamera().aspectRatio());
            this.rayMarchingProgram.uniform("u_fovy").f1(view.getCamera().fovTan());

            this.fullScreenQuad.draw();

        }

    }

    @Data
    @Accessors(chain = true)
    public static class Config implements RendererConfig {

        private Vector3f lightEnergy = new Vector3f(1.0f);
        private Vector3f lightSpecular = new Vector3f(1.0f);
        private Vector3f lightAmbient = new Vector3f(0.2f);
        private Vector3f lightDirection = new Vector3f(-1.5f, 2.5f, 2.5f).normalize();

    }

}
