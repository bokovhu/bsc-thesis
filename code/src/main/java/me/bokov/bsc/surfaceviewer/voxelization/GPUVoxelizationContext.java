package me.bokov.bsc.surfaceviewer.voxelization;

import lombok.Data;
import me.bokov.bsc.surfaceviewer.render.Texture;

import java.io.Serializable;
import java.util.*;

@Data
public class GPUVoxelizationContext implements VoxelizationContext, Serializable {

    private Map<String, Texture> precomputedTextureMap;

}
