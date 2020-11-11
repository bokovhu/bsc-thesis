package me.bokov.bsc.surfaceviewer.view;

import me.bokov.bsc.surfaceviewer.Configurable;
import me.bokov.bsc.surfaceviewer.Installable;
import me.bokov.bsc.surfaceviewer.View;
import me.bokov.bsc.surfaceviewer.scene.World;

public interface Renderer extends Installable<View>, Configurable {

    void render(World world);

    default boolean supportsNoWorldRendering() {return false;}

}
