package me.bokov.bsc.surfaceviewer.scene;

import lombok.Getter;
import org.joml.Vector3f;

public abstract class BaseLightSource implements LightSource {

    @Getter
    private final int id;

    @Getter
    private final Vector3f energy = new Vector3f(1f);

    protected BaseLightSource(int id) {
        this.id = id;
    }

    @Override
    public LightSource setEnergy(Vector3f e) {
        energy.set(e);
        return this;
    }


    @Override
    public LightSource setEnergy(float r, float g, float b) {
        energy.set(r, g, b);
        return this;
    }
}
