package me.bokov.bsc.surfaceviewer.view.renderer;

import me.bokov.bsc.surfaceviewer.view.Renderer;

public enum RendererType {

    UniformGridMarchingCubes(new UniformGridMarchingCubesRenderer()),
    OctreeMarchingCubes(new OctreeMarchingCubesRenderer()),
    RayMarching(new RayMarchingRenderer()),
    GPUMarchingCubes(new GPUMarchingCubesRenderer());

    public final Renderer instance;

    RendererType(Renderer instance) {this.instance = instance;}
}
