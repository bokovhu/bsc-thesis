package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.view.renderer.RendererType;

import java.util.*;
import java.util.concurrent.atomic.*;

public abstract class AppBase implements App, Runnable {

    private final long createdTime = System.currentTimeMillis();

    private View view = new View(this);
    private AtomicBoolean shouldQuit = new AtomicBoolean(false);

    public synchronized void sendSceneToView(World world) {

        view.changeScene(world);

    }

    public synchronized void sendConfigurationToView(Map<String, Map<String, Object>> delta) {

        view.changeConfiguration(delta);

    }

    public synchronized void sendRendererChangeToView(RendererType newRenderer) {

        view.changeRenderer(newRenderer);

    }

    public synchronized List<Property<?>> retrieveViewConfigurableProperties() {

        if (this.view == null) { return Collections.emptyList(); }

        final List<Property<?>> result = new ArrayList<>(
                view.getConfigurableProperties()
        );

        return result;

    }

    public synchronized Map<String, Map<String, Object>> retrieveCurrentViewConfiguration() {

        if (this.view == null) { return Collections.emptyMap(); }

        final Map<String, Map<String, Object>> result = new HashMap<>(
                view.getCurrentConfiguration()
        );

        return result;

    }

    public boolean shouldQuit() {
        return shouldQuit.get();
    }

    public synchronized void markShouldQuit() {
        this.shouldQuit.set(true);
    }

    public View getView() {
        return view;
    }

    public long runtimeMilliseconds() {
        return System.currentTimeMillis() - this.createdTime;
    }

}
