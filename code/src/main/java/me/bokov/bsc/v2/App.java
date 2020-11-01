package me.bokov.bsc.v2;

import me.bokov.bsc.surfaceviewer.AppConfig;
import me.bokov.bsc.v2.editor.event.Event;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class App implements Runnable {

    private final long createdTime = System.currentTimeMillis();
    private View view;
    private Editor editor;
    private ExecutorService executor;
    private AtomicBoolean shouldQuit = new AtomicBoolean(false);

    public App() {
        this.view = new View(this, new AppConfig());
        this.editor = new Editor(this);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public View getView() {
        return view;
    }

    public Editor getEditor() {
        return editor;
    }

    public synchronized void sendSceneToView(Scene scene) {

        view.changeScene(scene);

    }

    public synchronized void sendConfigurationToView(Map<String, Map<String, Object>> delta) {

        view.changeConfiguration(delta);

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

    public synchronized <T extends Event> void fireEditorEvent(T event) {
        if (this.editor != null && this.editor.getEventBus() != null) {
            this.editor.getEventBus().fire(event);
        }
    }

    public synchronized <T extends Event> void fireEditorEvent(Class<T> event) {
        if (this.editor != null && this.editor.getEventBus() != null) {
            this.editor.getEventBus().fire(event);
        }
    }

    @Override
    public void run() {

        this.executor.submit(this.editor);
        this.view.run();

        SwingUtilities.invokeLater(
                () -> {
                    this.editor.close();
                }
        );

        this.executor.shutdownNow();

        try {
            this.executor.awaitTermination(3600, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public long runtimeMilliseconds() {
        return System.currentTimeMillis() - this.createdTime;
    }

}
