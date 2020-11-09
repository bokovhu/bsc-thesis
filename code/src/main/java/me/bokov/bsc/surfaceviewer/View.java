package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.event.ViewInitialized;
import me.bokov.bsc.surfaceviewer.render.Camera;
import me.bokov.bsc.surfaceviewer.util.IOUtil;
import me.bokov.bsc.surfaceviewer.view.CameraManager;
import me.bokov.bsc.surfaceviewer.view.InputManager;
import me.bokov.bsc.surfaceviewer.view.Renderer;
import me.bokov.bsc.surfaceviewer.view.ShaderManager;
import me.bokov.bsc.surfaceviewer.view.renderer.RendererType;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import java.io.Serializable;
import java.util.*;

import static org.lwjgl.system.MemoryUtil.*;

public class View extends ViewBase implements Runnable {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final boolean FULLSCREEN = false;
    private static final long MONITOR = 0L;
    private final App app;
    private final Object nextSceneSyncObject = new Object();
    double lastFrameTime = 0.0;
    private World nextWorld = null;
    private World world = null;
    private RendererType nextRendererType = null;
    private ShaderManager shaderManager = null;
    private InputManager inputManager = null;
    private CameraManager cameraManager = null;
    private Renderer renderer = null;
    private Camera camera = null;
    private int windowCreationWidth = 0, windowCreationHeight = 0;
    private float deltaTime = 0.0f, appTime = 0.0f, viewTime = 0.0f;
    private long glfwWindowHandle = NULL;

    public View(App app) {
        this.app = app;
    }

    public long getGlfwWindowHandle() {
        return glfwWindowHandle;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public float getAppTime() {
        return appTime;
    }

    public float getViewTime() {
        return viewTime;
    }

    public InputManager getInputManager() {
        return this.inputManager;
    }

    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public App getApp() {
        return app;
    }

    public void init() {

        if (!GLFW.glfwInit()) {
            throw new RuntimeException("Could not initialize GLFW!");
        }

        this.createWindow();

        this.inputManager = new InputManager();
        this.inputManager.install(this);

        this.createOpenGLContext();

        this.shaderManager = new ShaderManager();
        this.shaderManager.install(this);

        this.camera = new Camera()
                .update((float) windowCreationWidth / (float) windowCreationHeight)
                .lookAt(new Vector3f(5f, 5f, 5f), new Vector3f(0f, 0f, 0f), new Vector3f(0f, 1f, 0f))
                .update();

        this.cameraManager = new CameraManager();
        this.cameraManager.install(this);

        this.changeRenderer(RendererType.GPUMarchingCubes);

    }

    private void createWindow() {

        GLFW.glfwDefaultWindowHints();

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);

        final long monitor = MONITOR != 0L
                ? MONITOR
                : GLFW.glfwGetPrimaryMonitor();

        GLFWVidMode monitorVidMode = GLFW.glfwGetVideoMode(monitor);

        this.windowCreationWidth = WIDTH;
        this.windowCreationHeight = HEIGHT;

        this.glfwWindowHandle = GLFW
                .glfwCreateWindow(
                        windowCreationWidth, windowCreationHeight,
                        "Surface Viewer",
                        FULLSCREEN ? monitor : NULL, NULL
                );

        if (this.glfwWindowHandle == NULL) {
            throw new RuntimeException("Could not create GLFW window!");
        }

        GLFW.glfwShowWindow(this.glfwWindowHandle);

    }

    private void createOpenGLContext() {

        GLFW.glfwMakeContextCurrent(this.glfwWindowHandle);
        GL.createCapabilities();

        GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glEnable(GL46.GL_BLEND);

    }

    public void startMainLoop() {

        boolean shouldLoop = true;

        while (shouldLoop) {

            update();
            render();

            GLFW.glfwSwapBuffers(this.glfwWindowHandle);
            GLFW.glfwPollEvents();

            shouldLoop &= !GLFW.glfwWindowShouldClose(this.glfwWindowHandle) && !this.app.shouldQuit();
        }

    }

    private void update() {

        if (this.nextWorld != null) {
            synchronized (this.nextSceneSyncObject) {
                this.world = this.nextWorld;
                this.nextWorld = null;
            }

            if (this.renderer != null) {
                this.renderer.uninstall();
                this.renderer.install(this);
            }
        }

        if (this.nextRendererType != null) {

            if (this.renderer != null) {
                this.renderer.uninstall();
            }

            this.renderer = nextRendererType.instance;
            this.renderer.install(this);

            this.nextRendererType = null;

        }

        double now = GLFW.glfwGetTime();
        deltaTime = (float) (now - lastFrameTime);
        lastFrameTime = now;

        viewTime += deltaTime;
        appTime = 0.001f * app.runtimeMilliseconds();

        this.cameraManager.update();

    }

    private void render() {

        GL46.glClearColor(0f, 0f, 0f, 1f);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);

        if (this.renderer != null) {
            if (this.world != null || this.renderer.supportsNoWorldRendering()) {
                this.renderer.render(this.world);
            }
        }

    }

    public void markShouldQuit() {
        GLFW.glfwSetWindowShouldClose(glfwWindowHandle, true);
    }

    public void tearDown() {

        this.inputManager.uninstall();

        GLFW.glfwDestroyWindow(this.glfwWindowHandle);
        GL.destroy();

        GLFW.glfwTerminate();

    }

    public synchronized void changeScene(World newWorld) {

        this.nextWorld = newWorld;

    }

    public void changeRenderer(RendererType newType) {

        this.nextRendererType = newType;

    }

    public void changeConfiguration(Map<String, Map<String, Object>> delta) {

        delta.forEach(
                (key, map) -> {
                    final var group = this.configurationGroupSettings
                            .computeIfAbsent(key, k -> new HashMap<>());
                    map.forEach(group::put);
                }
        );

        this.nextWorld = world;

    }

    public List<Property<?>> getConfigurableProperties() {

        // MUST use the ArrayList type, because List does not extend Serializable
        ArrayList<Property<? extends Serializable>> result = new ArrayList<>();

        if (renderer != null) { result.addAll(renderer.getConfigurationProperties()); }

        return IOUtil.serialize(result);

    }

    public Map<String, Map<String, Object>> getCurrentConfiguration() {

        // MUST use the HashMap type, because Map does not extend Serializable
        HashMap<String, Map<String, Object>> result = new HashMap<>();

        this.configurationGroupSettings.forEach(
                (groupKey, group) -> {
                    final Map<String, Object> map = new HashMap<>();
                    group.forEach(map::put);
                    result.put(groupKey, map);
                }
        );

        return IOUtil.serialize(result);

    }

    @Override
    public void run() {

        init();

        this.app.fire(ViewInitialized.class);

        startMainLoop();
        tearDown();

    }
}
