package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.render.Lighting;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

@Deprecated
public final class AppScene {

    private final Evaluatable<Float, CPUContext, GPUContext> sdfGenerator;
    private final Lighting lighting;
    private final AppResources resources;

    public AppScene(
            Evaluatable<Float, CPUContext, GPUContext> sdfGenerator,
            Lighting lighting
    ) {
        this.sdfGenerator = sdfGenerator;
        this.lighting = lighting;
        this.resources = new AppResources();
    }

    public AppScene(
            Evaluatable<Float, CPUContext, GPUContext> sdfGenerator,
            Lighting lighting,
            AppResources resources
    ) {
        this.sdfGenerator = sdfGenerator;
        this.lighting = lighting;
        this.resources = resources;
    }

    public Evaluatable<Float, CPUContext, GPUContext> sdf() {
        return sdfGenerator;
    }

    public Lighting lighting() {
        return lighting;
    }

    public AppResources resources() {
        return resources;
    }

}
