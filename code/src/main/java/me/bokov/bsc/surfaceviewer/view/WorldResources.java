package me.bokov.bsc.surfaceviewer.view;

import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.render.Texture;
import me.bokov.bsc.surfaceviewer.scene.ResourceTexture;
import me.bokov.bsc.surfaceviewer.scene.World;

import java.util.*;

public class WorldResources {

    private static final int START_TEX_UNIT = 5;

    private final Map<String, Texture> textureMap = new HashMap<>();
    private final Map<String, Integer> textureUnits = new HashMap<>();
    private int lastUsedTextureUnit = START_TEX_UNIT;

    public void clear() {

        textureUnits.clear();

        lastUsedTextureUnit = START_TEX_UNIT;

        textureMap.values()
                .forEach(Texture::tearDown);
        textureMap.clear();

    }

    private void loadResourceTexture(ResourceTexture resourceTexture) {

        final var loadedTexture = Texture.load(resourceTexture.location());
        textureMap.put(resourceTexture.name(), loadedTexture);

    }

    public void load(World world) {

        for(ResourceTexture tex : world.getResourceTextures()) {

            if(!textureMap.containsKey(tex.name())) {
                loadResourceTexture(tex);
                textureUnits.put(tex.name(), lastUsedTextureUnit);
                lastUsedTextureUnit += 1;
            }

        }

    }

    public void apply(ShaderProgram program) {

        for(String key : textureUnits.keySet()) {

            int unit = textureUnits.get(key);
            Texture texture = textureMap.get(key);

            program.uniform(key)
                    .samp(texture, unit);

        }

    }

}
