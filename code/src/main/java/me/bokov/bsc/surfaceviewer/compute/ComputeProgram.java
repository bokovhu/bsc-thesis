package me.bokov.bsc.surfaceviewer.compute;

import me.bokov.bsc.surfaceviewer.render.BaseProgram;
import org.lwjgl.opengl.GL46;

public class ComputeProgram extends BaseProgram {

    private int shaderHandle;
    private String shaderSourceCode = null;

    public void init() {
        super.init();
        this.shaderHandle = GL46.glCreateShader(GL46.GL_COMPUTE_SHADER);
    }

    public void attachSource(String source) {

        GL46.glShaderSource(this.shaderHandle, source);
        this.shaderSourceCode = source;

        GL46.glCompileShader(this.shaderHandle);

        int compileStatus = GL46.glGetShaderi(this.shaderHandle, GL46.GL_COMPILE_STATUS);

        if (compileStatus != GL46.GL_TRUE) {
            throw new RuntimeException(
                    "Could not compile a shader! Info log: \n" +
                            GL46.glGetShaderInfoLog(this.shaderHandle)
            );
        }

        GL46.glAttachShader(this.programHandle, this.shaderHandle);

    }

    public void tearDown() {

        super.tearDown();

        GL46.glDeleteShader(this.shaderHandle);

    }

}
