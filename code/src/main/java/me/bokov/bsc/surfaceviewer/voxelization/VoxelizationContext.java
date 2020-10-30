package me.bokov.bsc.surfaceviewer.voxelization;

import me.bokov.bsc.surfaceviewer.render.Texture;

import java.util.*;

public interface VoxelizationContext {

    Map<String, Texture> getPrecomputedTextureMap();

}
