package me.bokov.bsc.surfaceviewer.surfacelang;

import me.bokov.bsc.surfaceviewer.scene.SceneComponent;
import me.bokov.bsc.surfaceviewer.scene.World;

import java.util.*;

public abstract class ComponentParser <CTX, T extends SceneComponent> extends BaseParser {

    public ComponentParser(World world) {
        super(world);
    }

    public abstract List<ParsedComponent<T>> parseComponents(
            List<CTX> parseContexts
    );

}
