package me.bokov.bsc.surfaceviewer;

import lombok.Getter;
import me.bokov.bsc.surfaceviewer.render.Camera;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.IOUtil;
import me.bokov.bsc.surfaceviewer.view.*;
import me.bokov.bsc.surfaceviewer.view.renderer.RendererType;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

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
    @Getter
    private VoxelManager voxelManager = null;
    
    private int windowCreationWidth = 0, windowCreationHeight = 0;
    @Getter
    private float deltaTime = 0.0f;
    @Getter
    private float appTime = 0.0f;
    @Getter
    private float viewTime = 0.0f;
    @Getter
    private long glfwWindowHandle = NULL;
    private int windowWidth, windowHeight;
    private RendererConfig nextRendererConfig = null;
    private BlockingDeque<RenderProductionImageJob> renderJobs = new LinkedBlockingDeque<>();

    private FrameTimer frameTimer = new FrameTimer("Frame timer"), renderTimer = new FrameTimer("Render timer");

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

        this.windowWidth = windowCreationWidth;
        this.windowHeight = windowCreationHeight;

        this.cameraManager = new CameraManager();
        this.cameraManager.install(this);

        this.voxelManager = new VoxelManager();
        this.voxelManager.install(this);

        this.nextRendererType = RendererType.UniformGridMarchingCubes;

    }

    private void createWindow() {

        GLFW.glfwDefaultWindowHints();

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

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
        // GLFW.glfwSwapInterval(0);

        GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glEnable(GL46.GL_BLEND);

    }

    public void startMainLoop() {

        boolean shouldLoop = true;

        while (shouldLoop) {

            frameTimer.update();

            try {

                update();
                render();

            } catch (Exception e) {
                app.onViewReport("MainLoopError", Map.of("exception", e));
            }

            GLFW.glfwSwapBuffers(this.glfwWindowHandle);

            renderTimer.update();

            GLFW.glfwPollEvents();

            shouldLoop &= !GLFW.glfwWindowShouldClose(this.glfwWindowHandle) && !this.app.shouldQuit();
        }

    }

    private void postWorldChanged() {

    }

    private void postRendererChanged() {

    }

    private void postRendererConfigChanged() {

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

        while (!renderJobs.isEmpty()) {

            final var job = renderJobs.removeFirst();
            final var result = renderProductionImage(job.w, job.h);
            job.callback.accept(result);

        }

        renderTimer.catchUp();

        GL46.glClearColor(0.2f, 0.3f, 0.38f, 1f);
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

    public void resize(int w, int h) {

        camera.update((float) w / (float) h);
        GL46.glViewport(0, 0, w, h);

        this.windowWidth = w;
        this.windowHeight = h;

    }

    private BufferedImage renderProductionImage(int width, int height) {

        int color0 = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, color0);
        GL46.glTexImage2D(
                GL46.GL_TEXTURE_2D,
                0,
                GL46.GL_RGBA,
                width,
                height,
                0,
                GL46.GL_RGBA,
                GL46.GL_UNSIGNED_BYTE,
                (ByteBuffer) null
        );

        int depth = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, depth);
        GL46.glTexImage2D(
                GL46.GL_TEXTURE_2D,
                0,
                GL46.GL_DEPTH_COMPONENT,
                width,
                height,
                0,
                GL46.GL_DEPTH_COMPONENT,
                GL46.GL_UNSIGNED_BYTE,
                (ByteBuffer) null
        );

        GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);

        int fbo = GL46.glGenFramebuffers();
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, fbo);
        GL46.glFramebufferTexture2D(
                GL46.GL_FRAMEBUFFER,
                GL46.GL_COLOR_ATTACHMENT0,
                GL46.GL_TEXTURE_2D,
                color0,
                0
        );
        GL46.glFramebufferTexture2D(
                GL46.GL_FRAMEBUFFER,
                GL46.GL_DEPTH_ATTACHMENT,
                GL46.GL_TEXTURE_2D,
                depth,
                0
        );


        int fboStatus = GL46.glCheckFramebufferStatus(GL46.GL_FRAMEBUFFER);
        if (fboStatus != GL46.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Framebuffer incomplete for production rendering!");
        }


        GL46.glViewport(0, 0, width, height);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);

        if (this.renderer != null) {
            if (this.world != null || this.renderer.supportsNoWorldRendering()) {
                this.camera.update((float) width / (float) height);
                this.renderer.render(this.world);
            }
        }

        GL46.glFlush();
        GL46.glFinish();

        final IntBuffer pixelBuffer = BufferUtils.createIntBuffer(4 * width * height);

        GL46.glReadPixels(0, 0, width, height, GL46.GL_RGBA, GL46.GL_UNSIGNED_INT, pixelBuffer);


        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
        GL46.glDeleteFramebuffers(fbo);
        /* fboTexture.tearDown();
        fboDepthStencilTexture.tearDown(); */
        GL46.glDeleteTextures(new int[]{color0, depth});


        GL46.glViewport(0, 0, windowWidth, windowHeight);
        this.camera.update((float) windowWidth / (float) windowHeight);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);


        pixelBuffer.rewind();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = pixelBuffer.get();
                int g = pixelBuffer.get();
                int b = pixelBuffer.get();
                int a = pixelBuffer.get();

                img.setRGB(x, height - y - 1, ((a & 0xFF) << 24) |
                        ((r & 0xFF) << 16) |
                        ((g & 0xFF) << 8)  |
                        ((b & 0xFF)));
            }
        }


        return img;

    }

    public void enqueueRender(int w, int h, Consumer<BufferedImage> callback) {
        renderJobs.add(new RenderProductionImageJob(w, h, callback));
    }

    @Override
    public void run() {

        init();

        startMainLoop();
        tearDown();

    }

    static class RenderProductionImageJob {

        final int w, h;
        final Consumer<BufferedImage> callback;

        RenderProductionImageJob(int w, int h, Consumer<BufferedImage> callback) {
            this.w = w;
            this.h = h;
            this.callback = callback;
        }
    }

}
