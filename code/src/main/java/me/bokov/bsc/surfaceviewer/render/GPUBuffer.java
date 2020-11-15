package me.bokov.bsc.surfaceviewer.render;

import lombok.Getter;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.*;

public class GPUBuffer {

    @Getter
    private int handle;
    private int lastTarget = 0;
    private Set<Integer> boundToTargets = new HashSet<>();
    private Map<Integer, Set<Integer>> boundToTargetsAtIndex = new HashMap<>();

    public GPUBuffer init() {
        this.handle = GL46.glGenBuffers();
        return this;
    }

    public GPUBuffer bind(int target) {
        GL46.glBindBuffer(target, handle);
        boundToTargets.add(target);
        this.lastTarget = target;
        return this;
    }

    public GPUBuffer unbind(int target) {
        GL46.glBindBuffer(target, 0);
        boundToTargets.remove(target);
        this.lastTarget = boundToTargets.stream().findFirst().orElse(0);
        return this;
    }

    public GPUBuffer bind(int target, int index) {
        GL46.glBindBufferBase(target, index, handle);
        boundToTargets.add(target);
        boundToTargetsAtIndex.computeIfAbsent(
                target,
                k -> new HashSet<>()
        ).add(index);
        this.lastTarget = target;
        return this;
    }

    public GPUBuffer unbind(int target, int index) {
        GL46.glBindBufferBase(target, index, 0);
        boundToTargetsAtIndex.computeIfAbsent(
                target,
                k -> new HashSet<>()
        ).remove(index);
        boundToTargets.removeIf(t -> t == target && boundToTargetsAtIndex.get(target).isEmpty());
        this.lastTarget = boundToTargets.stream().findFirst().orElse(0);
        return this;
    }

    public GPUBuffer unbindAll() {
        boundToTargetsAtIndex.forEach(
                (target, indices) ->
                        indices.forEach(index -> unbind(target, index))
        );
        boundToTargets.forEach(this::unbind);
        this.lastTarget = 0;
        return this;
    }

    public GPUBuffer upload(ByteBuffer buffer, int usage) {
        if (lastTarget != 0) {
            GL46.glBufferData(lastTarget, buffer, usage);
        } else {
            bind(GL46.GL_SHADER_STORAGE_BUFFER);
            GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, buffer, usage);
        }
        return this;
    }

    public GPUBuffer upload(ShortBuffer buffer, int usage) {
        if (lastTarget != 0) {
            GL46.glBufferData(lastTarget, buffer, usage);
        } else {
            bind(GL46.GL_SHADER_STORAGE_BUFFER);
            GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, buffer, usage);
        }
        return this;
    }

    public GPUBuffer upload(IntBuffer buffer, int usage) {
        if (lastTarget != 0) {
            GL46.glBufferData(lastTarget, buffer, usage);
        } else {
            bind(GL46.GL_SHADER_STORAGE_BUFFER);
            GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, buffer, usage);
        }
        return this;
    }

    public GPUBuffer upload(FloatBuffer buffer, int usage) {
        if (lastTarget != 0) {
            GL46.glBufferData(lastTarget, buffer, usage);
        } else {
            bind(GL46.GL_SHADER_STORAGE_BUFFER);
            GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, buffer, usage);
        }
        return this;
    }

    public GPUBuffer storage(long size, int flags) {
        if (lastTarget != 0) {
            GL46.glBufferStorage(lastTarget, size, flags);
        } else {
            bind(GL46.GL_SHADER_STORAGE_BUFFER);
            GL46.glBufferStorage(GL46.GL_SHADER_STORAGE_BUFFER, size, flags);
        }
        return this;
    }

    public GPUBuffer tearDown() {

        unbindAll();
        GL46.glDeleteBuffers(handle);

        return this;

    }

}
