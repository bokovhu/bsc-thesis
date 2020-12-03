package me.bokov.bsc.surfaceviewer.render;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class CachedUniform {

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

    public void i2(int i, int j) {
        if (valid) {
            GL46.glUniform2i(location, i, j);
        }
    }

    public void i2(Vector2i v) {
        if (valid) {
            GL46.glUniform2i(location, v.x, v.y);
        }
    }

    public void i3(int i, int j, int k) {
        if (valid) {
            GL46.glUniform3i(location, i, j, k);
        }
    }

    public void i3(Vector3i v) {
        if (valid) {
            GL46.glUniform3i(location, v.x, v.y, v.z);
        }
    }

    public void i4(int i, int j, int k, int l) {
        if (valid) {
            GL46.glUniform4i(location, i, j, k, l);
        }
    }

    public void i4(Vector4i v) {
        if (valid) {
            GL46.glUniform4i(location, v.x, v.y, v.z, v.w);
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

    public void samp(Texture t, int unit) {
        if (valid) {
            t.bind(unit);
            GL46.glUniform1i(location, unit);
        }
    }

}
