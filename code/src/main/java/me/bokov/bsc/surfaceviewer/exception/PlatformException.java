package me.bokov.bsc.surfaceviewer.exception;

import lombok.Getter;

public class PlatformException extends RuntimeException {

    public PlatformException(Kind kind) {
        super(kind.message);
        this.kind = kind;
    }

    public PlatformException(Kind kind, Throwable cause) {
        super(kind.message, cause);
        this.kind = kind;
    }

    public enum Kind {
        GLFWInitializationError("Could not initialize GLFW"),
        NotYetInitialized("Platform was not yet initialized!");
        public final String message;

        Kind(String message) {this.message = message;}
    }

    @Getter
    private final Kind kind;

}
