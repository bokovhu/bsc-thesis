package me.bokov.bsc.surfaceviewer.view;

import me.bokov.bsc.surfaceviewer.Configurable;
import me.bokov.bsc.surfaceviewer.Installable;
import me.bokov.bsc.surfaceviewer.Scene;
import me.bokov.bsc.surfaceviewer.View;

public interface Renderer extends Installable<View>, Configurable {

    void render(Scene scene);

}
