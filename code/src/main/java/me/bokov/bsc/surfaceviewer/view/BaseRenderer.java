package me.bokov.bsc.surfaceviewer.view;

import me.bokov.bsc.surfaceviewer.View;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.scene.ResourceTexture;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.IOUtil;

import java.util.*;

public abstract class BaseRenderer implements Renderer {

    protected View view = null;
    protected WorldResources worldResources = new WorldResources();

    protected abstract void tearDown();

    protected void applyWorldResourcesToProgram(ShaderProgram program, World world) {

        if (program == null || world == null) {
            return;
        }

        worldResources.load(world);
        worldResources.apply(program);

    }

    @Override
    public void uninstall() {

        this.tearDown();

        this.worldResources.clear();

        this.view = null;

    }

    @Override
    public void install(View parent) {

        this.view = parent;

        this.view.getApp().onViewReport(
                "RendererInstalled",
                Map.of("config", IOUtil.serialize(this.getConfig()))
        );

    }
}
