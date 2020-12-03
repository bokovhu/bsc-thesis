package me.bokov.bsc.surfaceviewer.render;

import org.lwjgl.opengl.GL46;

import java.util.*;

public class ShaderProgram extends BaseProgram {

    private static final boolean DEBUG = false;

    private final List<Integer> shaderHandles = new ArrayList<>();
    private String vertexShaderSourceCode = null;
    private String fragmentShaderSourceCode = null;
    private String geometryShaderSourceCode = null;
    private String tessellationControlShaderSourceCode = null;
    private String tessellationEvaluationShaderSourceCode = null;

    private void attachShaderFromSource(String source, int shaderType) {

        int shaderId = GL46.glCreateShader(shaderType);
        GL46.glShaderSource(shaderId, source);
        GL46.glCompileShader(shaderId);

        int compileStatus = GL46.glGetShaderi(shaderId, GL46.GL_COMPILE_STATUS);

        if (compileStatus != GL46.GL_TRUE) {
            throw new RuntimeException(
                    "Could not compile a shader! Info log: \n" +
                            GL46.glGetShaderInfoLog(shaderId) + "\n\nSource:\n\n"
                            + source
            );
        }

        GL46.glAttachShader(this.programHandle, shaderId);

        this.shaderHandles.add(shaderId);

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

        super.tearDown();

        for (int shaderId : shaderHandles) {
            GL46.glDeleteShader(shaderId);
        }

        shaderHandles.clear();

    }

    public void attachVertexShaderFromSource(String source) {
        this.vertexShaderSourceCode = source;
        attachShaderFromSource(source, GL46.GL_VERTEX_SHADER);
        if (DEBUG) {
            System.out.println("Attached vertex shader source to program " + programHandle + "\n" + source + "\n------");
        }
    }

    public void attachFragmentShaderFromSource(String source) {
        this.fragmentShaderSourceCode = source;
        attachShaderFromSource(source, GL46.GL_FRAGMENT_SHADER);
        if (DEBUG) {
            System.out.println("Attached fragment shader source to program " + programHandle + "\n" + source + "\n------");
        }
    }

    public void attachGeometryShaderFromSource(String source) {
        this.geometryShaderSourceCode = source;
        attachShaderFromSource(source, GL46.GL_GEOMETRY_SHADER);
    }

    public void attachTessellationControlShaderFromSource(String source) {
        this.tessellationControlShaderSourceCode = source;
        attachShaderFromSource(source, GL46.GL_TESS_CONTROL_SHADER);
    }

    public void attachTessellationEvaluationShaderFromSource(String source) {
        this.tessellationEvaluationShaderSourceCode = source;
        attachShaderFromSource(source, GL46.GL_TESS_EVALUATION_SHADER);
    }

    public String vertexSource() {
        return this.vertexShaderSourceCode;
    }

    public String fragmentSource() {
        return this.fragmentShaderSourceCode;
    }

    public String geometrySource() {
        return this.geometryShaderSourceCode;
    }

    public String tessellationControlSource() {
        return this.tessellationControlShaderSourceCode;
    }

    public String tessellationEvaluationSource() {
        return this.tessellationEvaluationShaderSourceCode;
    }

}
