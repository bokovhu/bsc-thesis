package me.bokov.bsc.surfaceviewer.view;

import me.bokov.bsc.surfaceviewer.AppScene;
import me.bokov.bsc.surfaceviewer.SurfaceViewerPlatform;
import me.bokov.bsc.surfaceviewer.render.Camera;
import me.bokov.bsc.surfaceviewer.render.UI;
import me.bokov.bsc.surfaceviewer.view.services.CameraController;
import me.bokov.bsc.surfaceviewer.view.services.FontManager;
import me.bokov.bsc.surfaceviewer.view.services.InputManager;
import me.bokov.bsc.surfaceviewer.view.services.ShaderManager;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL46;

@Deprecated
public abstract class AppView {

    protected final Camera camera = new Camera();
    protected final UI ui = new UI();
    protected final SurfaceViewerPlatform platform;
    protected final CameraController cameraController;
    protected final ShaderManager shaderManager;
    protected final InputManager inputManager;
    protected final FontManager fontManager;
    protected AppScene appScene;

    AppView(AppScene appScene, SurfaceViewerPlatform platform) {
        this.appScene = appScene;
        this.platform = platform;
        this.inputManager = new InputManager();
        this.cameraController = new CameraController(this, this.camera, this.platform);
        this.shaderManager = new ShaderManager(this);
        this.fontManager = new FontManager(this);
        ui.update(new Vector2f(platform.config().getWidth(), platform.config().getHeight()));
    }

    public InputManager inputManager() {
        return inputManager;
    }

    public ShaderManager shaderManager() {
        return shaderManager;
    }

    public CameraController cameraController() {
        return cameraController;
    }

    public abstract void init();

    public void tearDown() {
        shaderManager.tearDown();
    }

    public void update(float delta) {
        this.cameraController.update(delta);
    }

    public void draw(float delta) {

        this.render(delta);

    }

    protected abstract void render(float delta);

    public void onResized(int width, int height) {
        this.camera.update(
                (float) width / (float) height
        );

        GL46.glViewport(0, 0, width, height);

        ui.update(new Vector2f(width, height));
    }

    public final boolean onKeyDown(int key, int mods) {
        return inputManager.onKeyDown(key, mods);
    }

    public final boolean onKeyUp(int key, int mods) {
        return inputManager.onKeyUp(key, mods);
    }

    public final boolean onMouseDown(int button, int mods) {
        return inputManager.onMouseDown(button, mods);
    }

    public final boolean onMouseUp(int button, int mods) {
        return inputManager.onMouseUp(button, mods);
    }

    public final boolean onMouseMove(float x, float y) {
        return inputManager.onMouseMove(x, y);
    }

    public void onSceneChanged(AppScene scene) {
        this.appScene = scene;
    }

    public void reloadServices() {
        this.shaderManager.reloadAll();
    }


    protected String stringOpt(String name) {
        return stringOpt(name, null);
    }

    protected String stringOpt(String name, String defaultValue) {
        return platform.config().getViewOpts().getOrDefault(name, defaultValue);
    }

    protected int intOpt(String name) {
        return intOpt(name, -1);
    }

    protected int intOpt(String name, int defaultValue) {
        final var mapValue = platform.config().getViewOpts().getOrDefault(name, "" + defaultValue);
        if (platform.config().getViewOpts().containsKey(name)) {
            return Integer.parseInt(mapValue);
        }
        return defaultValue;
    }

    protected float floatOpt(String name) {
        return intOpt(name, -1);
    }

    protected float floatOpt(String name, float defaultValue) {
        final var mapValue = platform.config().getViewOpts().getOrDefault(name, "" + defaultValue);
        if (platform.config().getViewOpts().containsKey(name)) {
            return Float.parseFloat(mapValue);
        }
        return defaultValue;
    }

}
