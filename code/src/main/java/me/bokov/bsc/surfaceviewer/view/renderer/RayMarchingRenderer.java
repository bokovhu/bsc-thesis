package me.bokov.bsc.surfaceviewer.view.renderer;

import me.bokov.bsc.surfaceviewer.Property;
import me.bokov.bsc.surfaceviewer.World;
import me.bokov.bsc.surfaceviewer.View;
import me.bokov.bsc.surfaceviewer.view.Renderer;

import java.util.*;

public class RayMarchingRenderer implements Renderer {

    private View view = null;

    @Override
    public void render(World world) {

    }

    @Override
    public void install(View parent) {
        this.view = parent;
    }

    @Override
    public void uninstall() {

    }

    @Override
    public List<Property<?>> getConfigurationProperties() {
        return Collections.emptyList();
    }
}
