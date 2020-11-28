package me.bokov.bsc.surfaceviewer.view.renderer;

import me.bokov.bsc.surfaceviewer.view.Renderer;

public enum RendererType {

    UniformGridMarchingCubes(new MeshRenderer()),
    RayMarching(new RayMarchingRenderer());

    public final Renderer instance;

    RendererType(Renderer instance) {this.instance = instance;}
}
