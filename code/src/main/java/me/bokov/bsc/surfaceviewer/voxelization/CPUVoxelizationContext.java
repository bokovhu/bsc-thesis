package me.bokov.bsc.surfaceviewer.voxelization;

import lombok.Data;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.render.Texture;

import java.util.*;

@Data
@Accessors(chain = true)
public class CPUVoxelizationContext implements VoxelizationContext {

    private final Map<String, Texture> precomputedTextureMap = new HashMap<>();

    public CPUVoxelizationContext withPrecomputedTexture(String name, Texture texture) {
        this.precomputedTextureMap.put(name, texture);
        return this;
    }

}
