package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.render.Lighting;
import me.bokov.bsc.surfaceviewer.sdf.SDFGenerator;

public final class AppScene {

    private final SDFGenerator sdfGenerator;
    private final Lighting lighting;

    public AppScene(SDFGenerator sdfGenerator,
            Lighting lighting
    ) {
        this.sdfGenerator = sdfGenerator;
        this.lighting = lighting;
    }

    public SDFGenerator sdf() {
        return sdfGenerator;
    }

    public Lighting lighting() {
        return lighting;
    }

}
