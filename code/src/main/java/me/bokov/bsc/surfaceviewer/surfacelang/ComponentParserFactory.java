package me.bokov.bsc.surfaceviewer.surfacelang;

import me.bokov.bsc.surfaceviewer.scene.SceneComponent;
import me.bokov.bsc.surfaceviewer.scene.World;

public interface ComponentParserFactory <CTX, T extends SceneComponent, P extends ComponentParser <CTX, T>> {

    ComponentParser<CTX, T> createParserForWorld(World world);

}
