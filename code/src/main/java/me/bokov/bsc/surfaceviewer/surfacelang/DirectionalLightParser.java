package me.bokov.bsc.surfaceviewer.surfacelang;

import me.bokov.bsc.surfaceviewer.scene.DirectionalLight;
import me.bokov.bsc.surfaceviewer.scene.LightSource;
import me.bokov.bsc.surfaceviewer.scene.World;
import org.joml.Vector3f;

import java.util.*;

public class DirectionalLightParser extends LightSourceParser {

    public DirectionalLightParser(World world) {
        super(world);
    }

    @Override
    protected List<String> validateLightSource(SurfaceLangParser.LightContext ctx) {

        List<String> errors = new ArrayList<>();

        if (!hasLightParam(ctx, lp -> "direction".equals(paramName(lp)) && lp.vec3Value() != null)) {
            errors.add("Direction must be set!");
        }

        return errors;
    }

    @Override
    protected LightSource parseLightSource(
            Vector3f energy, SurfaceLangParser.LightContext ctx
    ) {
        final var light = new DirectionalLight(getWorld().nextId());

        light.dir(
                findLightParam(
                        ctx,
                        lp -> "direction".equals(paramName(lp)) && lp.vec3Value() != null,
                        lp -> vec3Val(lp.vec3Value())
                ).orElseThrow()
        );

        light.setEnergy(energy);

        return light;
    }
}
