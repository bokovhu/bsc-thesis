package me.bokov.bsc.surfaceviewer.view;

import lombok.Builder;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.view.renderer.RendererType;

import java.io.Serializable;

@Getter
@Builder
public class ViewConfiguration implements Serializable {

    private RendererType rendererType;
    private RendererConfig rendererConfig;
    private World world;

}
