package me.bokov.bsc.surfaceviewer.platform;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.system.MemoryUtil.*;

public class PlatformWindow {

    private long handle;
    private boolean created = false;
    private boolean subscribed = false;
    private int width, height;
    private String title;
    private long monitor = NULL;
    private double lastFrameTime = 0.0;
    private Logic logic = (delta, client) -> {};
    private Renderer renderer = (delta, client) -> {};

    public void init() {

        if (!created) {
            handle = GLFW.glfwCreateWindow(
                    width, height,
                    title,
                    monitor,
                    NULL
            );
            created = handle != NULL;
        }

        if (created && !subscribed) {

            GLFW.glfwSetWindowSizeCallback(
                    handle,
                    (wnd, w, h) -> {
                        width = w;
                        height = h;
                    }
            );

        }

    }

    public void tearDown() {

        if (created) {
            GLFW.glfwDestroyWindow(handle);
            created = false;
        }

    }

    public interface LogicClient {

    }

    @FunctionalInterface
    public interface Logic {

        void update(float delta, LogicClient client);

    }

    public interface RendererClient {

    }

    @FunctionalInterface
    public interface Renderer {

        void render(float delta, RendererClient client);

    }

}
