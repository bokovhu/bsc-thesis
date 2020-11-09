package me.bokov.bsc.surfaceviewer.render;

import org.lwjgl.opengl.GL46;

import java.util.*;

public abstract class BaseProgram {

    protected final Map<String, CachedUniform> uniformCache = new HashMap<>();
    protected int programHandle;

    public void init() {

        this.programHandle = GL46.glCreateProgram();

    }

    public void linkAndValidate() {

        GL46.glLinkProgram(this.programHandle);

        int linkStatus = GL46.glGetProgrami(this.programHandle, GL46.GL_LINK_STATUS);

        if (linkStatus != GL46.GL_TRUE) {
            throw new RuntimeException("Could not link program! Info log: \n" + GL46
                    .glGetProgramInfoLog(programHandle));
        }

        GL46.glValidateProgram(this.programHandle);

        int validateStatus = GL46.glGetProgrami(this.programHandle, GL46.GL_VALIDATE_STATUS);

        if (validateStatus != GL46.GL_TRUE) {
            throw new RuntimeException("Could not validate program! Info log: \n" + GL46
                    .glGetProgramInfoLog(programHandle));
        }

    }

    public void tearDown() {

        GL46.glUseProgram(0);
        GL46.glDeleteProgram(programHandle);

        uniformCache.clear();

    }

    public void use() {
        GL46.glUseProgram(programHandle);
    }

    public CachedUniform uniform(String name) {
        return uniformCache.computeIfAbsent(
                name,
                uniformName -> {
                    int l = GL46.glGetUniformLocation(programHandle, uniformName);
                    if (l >= 0) {
                        return new CachedUniform(true, l);
                    }
                    return new CachedUniform(false, -1);
                }
        );
    }

}
