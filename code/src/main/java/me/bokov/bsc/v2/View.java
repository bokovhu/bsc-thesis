package me.bokov.bsc.v2;

import me.bokov.bsc.surfaceviewer.AppConfig;
import me.bokov.bsc.surfaceviewer.render.Camera;
import me.bokov.bsc.v2.editor.event.ViewInitialized;
import me.bokov.bsc.v2.view.CameraManager;
import me.bokov.bsc.v2.view.InputManager;
import me.bokov.bsc.v2.view.Renderer;
import me.bokov.bsc.v2.view.ShaderManager;
import me.bokov.bsc.v2.view.renderer.RendererType;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import java.util.*;

import static org.lwjgl.system.MemoryUtil.*;

public class View extends ViewBase implements Runnable {

    private final App app;
    private final AppConfig config;

    private final Object nextSceneSyncObject = new Object();
    private Scene nextScene = null;
    private Scene scene = null;

    private ShaderManager shaderManager = null;
    private InputManager inputManager = null;
    private CameraManager cameraManager = null;
    private Renderer renderer = null;
    private Camera camera = null;

    private int windowCreationWidth = 0, windowCreationHeight = 0;

    private float deltaTime = 0.0f, appTime = 0.0f, viewTime = 0.0f;
    double lastFrameTime = 0.0;

    private long glfwWindowHandle = NULL;

    public View(App app, AppConfig config) {
        this.app = app;
        this.config = config;
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
        ;

        this.changeRenderer(RendererType.UniformGridMarchingCubes);

    }

    private void createWindow() {

        GLFW.glfwDefaultWindowHints();

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);

        final long monitor = config.getMonitor() != 0L
                ? config.getMonitor()
                : GLFW.glfwGetPrimaryMonitor();

        GLFWVidMode monitorVidMode = GLFW.glfwGetVideoMode(monitor);

        this.windowCreationWidth =
                config.getWidth() > 0 ? config.getWidth() : monitorVidMode.width();
        this.windowCreationHeight =
                config.getHeight() > 0 ? config.getHeight() : monitorVidMode.height();

        this.glfwWindowHandle = GLFW
                .glfwCreateWindow(
                        windowCreationWidth, windowCreationHeight,
                        "Surface Viewer",
                        config.isFullscreen() ? monitor : NULL, NULL
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

        if (this.nextScene != null) {
            synchronized (this.nextSceneSyncObject) {
                this.scene = this.nextScene;
                this.nextScene = null;
            }

            if (this.renderer != null) {
                this.renderer.uninstall();
                this.renderer.install(this);
            }
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

        if (this.renderer != null && this.scene != null) {
            this.renderer.render(this.scene);
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

    public synchronized void changeScene(Scene newScene) {

        this.nextScene = newScene;

    }

    public void changeRenderer(RendererType newType) {

        if (this.renderer != null) {
            this.renderer.uninstall();
        }

        this.renderer = newType.instance;
        this.renderer.install(this);

    }

    public void changeConfiguration(Map<String, Map<String, Object>> delta) {

        delta.forEach(
                (key, map) -> {
                    final var group = this.configurationGroupSettings
                            .computeIfAbsent(key, k -> new HashMap<>());
                    map.forEach(group::put);
                }
        );

        this.nextScene = scene;

    }

    // FIXME should be serialized
    public List<Property<?>> getConfigurableProperties() {

        List<Property<?>> result = new ArrayList<>();

        if (renderer != null) { result.addAll(renderer.getConfigurationProperties()); }

        return result;

    }

    // FIXME should be serialized
    public Map<String, Map<String, Object>> getCurrentConfiguration() {

        Map<String, Map<String, Object>> result = new HashMap<>();

        this.configurationGroupSettings.forEach(
                (groupKey, group) -> {
                    final Map<String, Object> map = new HashMap<>();
                    group.forEach(map::put);
                    result.put(groupKey, map);
                }
        );

        return result;

    }

    @Override
    public void run() {

        init();

        this.app.fireEditorEvent(ViewInitialized.class);

        startMainLoop();
        tearDown();

    }
}
