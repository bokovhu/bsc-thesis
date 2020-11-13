package me.bokov.bsc.surfaceviewer;

import lombok.Getter;
import me.bokov.bsc.surfaceviewer.render.Camera;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.IOUtil;
import me.bokov.bsc.surfaceviewer.view.*;
import me.bokov.bsc.surfaceviewer.view.renderer.RendererType;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import java.util.*;

import static org.lwjgl.system.MemoryUtil.*;

public class View implements Runnable {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final boolean FULLSCREEN = false;
    private static final long MONITOR = 0L;
    private static final Object SYNCOBJ = new Object();
    @Getter
    private final App app;
    double lastFrameTime = 0.0;
    private World nextWorld = null;
    @Getter
    private World world = null;
    private RendererType nextRendererType = null;
    @Getter
    private ShaderManager shaderManager = null;
    @Getter
    private InputManager inputManager = null;
    @Getter
    private CameraManager cameraManager = null;
    @Getter
    private Renderer renderer = null;
    @Getter
    private Camera camera = null;
    private int windowCreationWidth = 0, windowCreationHeight = 0;
    @Getter
    private float deltaTime = 0.0f;
    @Getter
    private float appTime = 0.0f;
    @Getter
    private float viewTime = 0.0f;
    @Getter
    private long glfwWindowHandle = NULL;
    private RendererConfig nextRendererConfig = null;

    public View(App app) {
        this.app = app;
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

        this.nextRendererType = RendererType.UniformGridMarchingCubes;

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

            try {

                update();
                render();

            } catch (Exception e) {
                app.onViewReport("MainLoopError", Map.of("exception", e));
            }

            GLFW.glfwSwapBuffers(this.glfwWindowHandle);
            GLFW.glfwPollEvents();

            shouldLoop &= !GLFW.glfwWindowShouldClose(this.glfwWindowHandle) && !this.app.shouldQuit();
        }

    }

    private void update() {

        if (this.nextWorld != null) {
            synchronized (SYNCOBJ) {
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

        if (this.nextRendererConfig != null) {

            if (this.renderer != null) {
                this.renderer.configure(this.nextRendererConfig);
            }

            this.nextRendererConfig = null;

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

    public void changeConfig(ViewConfiguration configuration) {

        if (configuration.getWorld() != null) {
            synchronized (SYNCOBJ) {
                this.nextWorld = configuration.getWorld();
            }
        }
        if (configuration.getRendererType() != null) {
            synchronized (SYNCOBJ) {
                this.nextRendererType = configuration.getRendererType();
            }
        }
        if (configuration.getRendererConfig() != null) {
            synchronized (SYNCOBJ) {
                this.nextRendererConfig = configuration.getRendererConfig();
            }
        }

    }

    public RendererConfig provideRendererConfig() {

        if (renderer != null) {
            return IOUtil.serialize(renderer.getConfig());
        }

        return null;

    }

    @Override
    public void run() {

        init();

        startMainLoop();
        tearDown();

    }
}
