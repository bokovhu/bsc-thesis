package me.bokov.bsc.surfaceviewer.view;

import me.bokov.bsc.surfaceviewer.View;
import me.bokov.bsc.surfaceviewer.scene.World;

public interface Renderer {

    void uninstall();
    void install(View view);
    void configure(RendererConfig config);
    void render(World world);
    default boolean supportsNoWorldRendering() {return false;}

}
