package me.bokov.bsc.v2.view.renderer;

import me.bokov.bsc.v2.Property;
import me.bokov.bsc.v2.Scene;
import me.bokov.bsc.v2.View;
import me.bokov.bsc.v2.view.Renderer;

import java.util.*;

public class RayMarchingRenderer implements Renderer {

    private View view = null;

    @Override
    public void render(Scene scene) {

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
