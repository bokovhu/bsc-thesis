package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.exception.PlatformException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import static org.lwjgl.system.MemoryUtil.*;

public class Platform {

    private static final Platform INSTANCE = new Platform();

    private boolean initialized = false;

    public static Platform getInstance() {
        return INSTANCE;
    }

    public boolean checkForGLContext() {

        if (!initialized) {
            throw new PlatformException(PlatformException.Kind.NotYetInitialized);
        }

        final long currentContextHolder = GLFW.glfwGetCurrentContext();
        if (currentContextHolder == NULL || currentContextHolder == GLFW.GLFW_NO_CURRENT_CONTEXT) {
            return false;
        }

        return true;

    }

    public void init() {
        if (initialized) {
            // Already initialized
            return;
        }

        if (!GLFW.glfwInit()) {
            throw new PlatformException(PlatformException.Kind.GLFWInitializationError);
        }

        initialized = true;
    }

    public void tearDown() {

        if (initialized) {

            GLFW.glfwTerminate();

            initialized = false;
        }

    }

}
