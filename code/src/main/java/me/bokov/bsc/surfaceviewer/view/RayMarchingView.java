package me.bokov.bsc.surfaceviewer.view;

import me.bokov.bsc.surfaceviewer.AppScene;
import me.bokov.bsc.surfaceviewer.SurfaceViewerPlatform;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.FullScreenQuad;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.render.raymarcher.RaymarcherShaderGenerator;
import me.bokov.bsc.surfaceviewer.util.ResourceUtil;

import java.util.*;

public class RayMarchingView extends AppView {

    private ShaderProgram rayMarchingProgram = null;
    private Drawable fullScreenQuad = null;

    public RayMarchingView(AppScene appScene, SurfaceViewerPlatform platform) {
        super(appScene, platform);
    }

    @Override
    public void init() {

        this.fullScreenQuad = FullScreenQuad.create();

        final String vertexShaderSource = ResourceUtil
                .readResource("glsl/fullScreenQuad.raymarcher.vertex.glsl");

        final String fragmentShaderSource = new RaymarcherShaderGenerator(
                this.appScene.sdf(),
                new HashMap<>()
        ).generateRaymarcherFragmentSource();

        System.out.println(
                fragmentShaderSource
        );

        this.rayMarchingProgram = new ShaderProgram();
        this.rayMarchingProgram.init();
        this.rayMarchingProgram.attachVertexShaderFromSource(vertexShaderSource);
        this.rayMarchingProgram.attachFragmentShaderFromSource(fragmentShaderSource);
        this.rayMarchingProgram.linkAndValidate();

    }

    @Override
    public void tearDown() {

    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    protected void render(float delta) {

        this.rayMarchingProgram.use();

        final var eye = camera.eye();
        final var Ld = appScene.lighting().Ld();
        final var Le = appScene.lighting().Le();
        final var La = appScene.lighting().La();

        this.rayMarchingProgram.uniform("u_eye")
                .f3(eye.x, eye.y, eye.z);
        this.rayMarchingProgram.uniform("u_aspect")
                .f1(camera.aspectRatio());
        this.rayMarchingProgram.uniform("u_V")
                .mat4(camera.V());
        this.rayMarchingProgram.uniform("u_fovy")
                .f1(camera.fovTan());
        this.rayMarchingProgram.uniform("u_VPinv")
                .mat4(camera.VPinv());
        this.rayMarchingProgram.uniform("u_forward")
                .vec3(camera.forward());
        this.rayMarchingProgram.uniform("u_right")
                .vec3(camera.right());
        this.rayMarchingProgram.uniform("u_up")
                .vec3(camera.up());

        this.rayMarchingProgram.uniform("u_Ld")
                .f3(Ld.x, Ld.y, Ld.z);
        this.rayMarchingProgram.uniform("u_La")
                .f3(La.x, La.y, La.z);
        this.rayMarchingProgram.uniform("u_Le")
                .f3(Le.x, Le.y, Le.z);

        this.fullScreenQuad.draw();

    }

    @Override
    public void onResized(int width, int height) {
        super.onResized(width, height);
    }
}
