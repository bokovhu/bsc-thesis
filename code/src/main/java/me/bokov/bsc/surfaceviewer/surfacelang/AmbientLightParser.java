package me.bokov.bsc.surfaceviewer.surfacelang;

import me.bokov.bsc.surfaceviewer.scene.AmbientLight;
import me.bokov.bsc.surfaceviewer.scene.LightSource;
import me.bokov.bsc.surfaceviewer.scene.World;
import org.joml.Vector3f;

import java.util.*;

public class AmbientLightParser extends LightSourceParser{

    public AmbientLightParser(World world) {
        super(world);
    }

    @Override
    protected List<String> validateLightSource(SurfaceLangParser.LightContext ctx) {
        return Collections.emptyList();
    }

    @Override
    protected LightSource parseLightSource(
            Vector3f energy, SurfaceLangParser.LightContext ctx
    ) {
        return new AmbientLight(getWorld().nextId()).setEnergy(energy);
    }

}
