package me.bokov.bsc.surfaceviewer;

import org.lwjgl.glfw.GLFW;

public class Platform {

    private static final Platform INSTANCE = new Platform();

    private boolean initialized = false;

    public void init() {
        if (initialized) {
            // Already initialized
            return;
        }

        if (!GLFW.glfwInit()) {
            throw new RuntimeException()
        }

        initialized = true;
    }

}
