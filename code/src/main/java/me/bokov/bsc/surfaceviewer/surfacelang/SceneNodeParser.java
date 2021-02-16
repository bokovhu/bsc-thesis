package me.bokov.bsc.surfaceviewer.surfacelang;

import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.scene.World;

import java.util.*;
import java.util.stream.*;

public class SceneNodeParser extends ComponentParser<SurfaceLangParser.ExpressionContext, SceneNode> {

    public SceneNodeParser(World world) {
        super(world);
    }

    @Override
    public List<ParsedComponent<SceneNode>> parseComponents(List<SurfaceLangParser.ExpressionContext> parseContexts) {
        return parseContexts.stream().map(this::parseSceneNode)
                .collect(Collectors.toList());
    }

}
