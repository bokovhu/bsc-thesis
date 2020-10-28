package me.bokov.bsc.surfaceviewer.render;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;

// TODO: Error handling
// TODO: Attribute handling
public class ShaderProgram {

    private int programHandle;
    private List<Integer> shaderHandles = new ArrayList<>();
    private Map<String, CachedUniform> uniformCache = new HashMap<>();

    private String vertexShaderSourceCode = null;
    private String fragmentShaderSourceCode = null;
    private String geometryShaderSourceCode = null;
    private String tessellationControlShaderSourceCode = null;
    private String tessellationEvaluationShaderSourceCode = null;

    public ShaderProgram() {
    }

    public void init() {

        this.programHandle = GL46.glCreateProgram();

    }

    private void attachShaderFromSource(String source, int shaderType) {

        int shaderId = GL46.glCreateShader(shaderType);
        GL46.glShaderSource(shaderId, source);
        GL46.glCompileShader(shaderId);

        int compileStatus = GL46.glGetShaderi(shaderId, GL46.GL_COMPILE_STATUS);

        if (compileStatus != GL46.GL_TRUE) {
            throw new RuntimeException(
                    "Could not compile a shader! Info log: \n" +
                            GL46.glGetShaderInfoLog(shaderId)
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

        GL46.glUseProgram(0);
        GL46.glDeleteProgram(programHandle);

        for (int shaderId : shaderHandles) {
            GL46.glDeleteShader(shaderId);
        }

        shaderHandles.clear();
        uniformCache.clear();

    }

    public void attachVertexShaderFromSource(String source) {
        this.vertexShaderSourceCode = source;
        attachShaderFromSource(source, GL46.GL_VERTEX_SHADER);
    }

    public void attachFragmentShaderFromSource(String source) {
        this.fragmentShaderSourceCode = source;
        attachShaderFromSource(source, GL46.GL_FRAGMENT_SHADER);
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

    public static class CachedUniform {

        private final boolean valid;
        private final int location;
        private final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);

        public CachedUniform(boolean valid, int location) {
            this.valid = valid;
            this.location = location;
        }

        public void i1(int i) {
            if (valid) {
                GL46.glUniform1i(location, i);
            }
        }

        public void f1(float v) {
            if (valid) {
                GL46.glUniform1f(location, v);
            }
        }

        public void f2(float x, float y) {
            if (valid) {
                GL46.glUniform2f(location, x, y);
            }
        }

        public void f3(float x, float y, float z) {
            if (valid) {
                GL46.glUniform3f(location, x, y, z);
            }
        }

        public void vec2(Vector2f v) {
            if (valid) {
                GL46.glUniform2f(location, v.x, v.y);
            }
        }

        public void vec3(Vector3f v) {
            if (valid) {
                GL46.glUniform3f(location, v.x, v.y, v.z);
            }
        }

        public void vec4(Vector4f v) {
            if (valid) {
                GL46.glUniform4f(location, v.x, v.y, v.z, v.w);
            }
        }

        public void f4(float x, float y, float z, float w) {
            if (valid) {
                GL46.glUniform4f(location, x, y, z, w);
            }
        }

        public void mat3(Matrix3f m) {
            if (valid) {
                matrixBuffer.clear();
                m.get(matrixBuffer);
                GL46.glUniformMatrix3fv(location, false, matrixBuffer);
            }
        }

        public void mat4(Matrix4f m) {
            if (valid) {
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    FloatBuffer fb = new Matrix4f(m)
                            .get(stack.mallocFloat(16));
                    GL46.glUniformMatrix4fv(location, false, fb);
                }
            }
        }

    }

}
