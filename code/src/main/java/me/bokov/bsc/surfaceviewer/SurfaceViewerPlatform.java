package me.bokov.bsc.surfaceviewer;

import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

public class SurfaceViewerPlatform {

    private final ViewManager viewManager;
    private final SceneManager sceneManager;
    private final AppConfig config;

    private int windowCreationWidth = 0, windowCreationHeight = 0;

    private long glfwWindowHandle = NULL;

    public SurfaceViewerPlatform(AppConfig config) {
        this.sceneManager = new SceneManager(this);
        this.viewManager = new ViewManager(this);
        this.config = config;
    }

    public void init() {

        if (!GLFW.glfwInit()) {
            throw new RuntimeException("Could not initialize GLFW!");
        }

        this.createWindow();
        this.registerListeners();
        this.createOpenGLContext();
        this.viewManager.changeScene(this.sceneManager.makeScene(config.getSceneName()), false);
        this.viewManager.initInitialView(config.getViewName());

        this.viewManager.view().onResized(windowCreationWidth, windowCreationHeight);

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

        double lastFrameTime = GLFW.glfwGetTime();

        while (shouldLoop) {

            double now = GLFW.glfwGetTime();
            float delta = (float) (now - lastFrameTime);
            lastFrameTime = now;

            GL46.glClearColor(0f, 0f, 0f, 1f);
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);

            this.viewManager.view().update(delta);
            this.viewManager.view().render(delta);

            GLFW.glfwSwapBuffers(this.glfwWindowHandle);
            GLFW.glfwPollEvents();

            shouldLoop &= !GLFW.glfwWindowShouldClose(this.glfwWindowHandle);
        }

    }

    private void registerListeners() {

        GLFW.glfwSetKeyCallback(
                glfwWindowHandle,
                this::onGLFWKeyEvent
        );
        GLFW.glfwSetMouseButtonCallback(
                glfwWindowHandle,
                this::onGLFWMouseButtonEvent
        );
        GLFW.glfwSetCursorPosCallback(
                glfwWindowHandle,
                this::onGLFWCursorPosEvent
        );
        GLFW.glfwSetWindowSizeCallback(
                glfwWindowHandle,
                this::onGLFWWindowResized
        );

    }

    private void onGLFWKeyEvent(long wnd, int key, int scancode, int action, int mods) {

        switch (action) {
            case GLFW.GLFW_PRESS:
                boolean platformDidHandleKeyDown = onPlatformKeyDown(key, mods);
                if (!platformDidHandleKeyDown) {
                    this.viewManager.view().onKeyDown(key, mods);
                }
                break;
            case GLFW.GLFW_RELEASE:
                boolean platformDidHandleKeyUp = onPlatformKeyUp(key, mods);
                if (!platformDidHandleKeyUp) {
                    this.viewManager.view().onKeyUp(key, mods);
                }
                break;
        }

    }

    private boolean onPlatformKeyUp(int key, int mods) {

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            GLFW.glfwSetWindowShouldClose(glfwWindowHandle, true);
            return true;
        } else if (key == GLFW.GLFW_KEY_R && (mods & GLFW.GLFW_MOD_SHIFT) > 0) {
            this.viewManager.view().reloadServices();
            return true;
        }

        return false;
    }

    private boolean onPlatformKeyDown(int key, int mods) {

        return false;
    }

    private boolean onPlatformMouseDown(int button, int mods) {

        return false;
    }

    private boolean onPlatformMouseUp(int button, int mods) {

        return false;
    }

    private void onGLFWMouseButtonEvent(long wnd, int button, int action, int mods) {

        switch (action) {
            case GLFW.GLFW_PRESS:
                boolean platformDidHandleMouseDown = onPlatformMouseDown(button, mods);
                if (!platformDidHandleMouseDown) {
                    this.viewManager.view().onMouseDown(button, mods);
                }
                break;
            case GLFW.GLFW_RELEASE:
                boolean platformDidHandleMouseUp = onPlatformMouseUp(button, mods);
                if (!platformDidHandleMouseUp) {
                    this.viewManager.view().onMouseUp(button, mods);
                }
                break;
        }

    }

    private void onGLFWCursorPosEvent(long wnd, double xpos, double ypos) {

        this.viewManager.view().onMouseMove((float) xpos, (float) ypos);

    }

    private void onGLFWWindowResized(long wnd, int w, int h) {
        this.viewManager.view().onResized(w, h);
    }

    public void tearDown() {

        this.viewManager.view().tearDown();

        GLFW.glfwDestroyWindow(this.glfwWindowHandle);
        GL.destroy();

        GLFW.glfwTerminate();

    }

    public void grabCursor() {

        GLFW.glfwSetInputMode(glfwWindowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

    }

    public void releaseCursor() {

        GLFW.glfwSetInputMode(glfwWindowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);

    }

    public AppConfig config() {
        return this.config;
    }

}
