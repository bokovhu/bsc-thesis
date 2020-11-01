package me.bokov.bsc.v2.view.renderer;

import me.bokov.bsc.v2.view.Renderer;

public enum RendererType {

    UniformGridMarchingCubes(new UniformGridMarchingCubesRenderer()),
    OctreeMarchingCubes(new OctreeMarchingCubesRenderer()),
    RayMarching(new RayMarchingRenderer());

    public final Renderer instance;

    RendererType(Renderer instance) {this.instance = instance;}
}
