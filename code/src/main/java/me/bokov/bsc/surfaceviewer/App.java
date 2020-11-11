package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.event.Event;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.view.renderer.RendererType;

import java.util.*;

public interface App {

    View getView();
    void sendSceneToView(World world);
    void sendConfigurationToView(Map<String, Map<String, Object>> deltaConfiguration);
    void sendRendererChangeToView(RendererType newRendererType);
    List<Property<?>> retrieveViewConfigurableProperties();
    Map<String, Map<String, Object>> retrieveCurrentViewConfiguration();
    boolean shouldQuit();
    void markShouldQuit();
    <T extends Event> void fire(T event);
    <T extends Event> void fire(Class<T> eventClass);
    long runtimeMilliseconds();

}
