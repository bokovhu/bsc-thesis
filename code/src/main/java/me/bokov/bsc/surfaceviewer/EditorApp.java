package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.event.Event;
import me.bokov.bsc.surfaceviewer.view.renderer.RendererType;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class EditorApp extends AppBase {

    private final long createdTime = System.currentTimeMillis();

    private Editor editor = new Editor(this);
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public Editor getEditor() {
        return editor;
    }

    public synchronized <T extends Event> void fire(T event) {
        if (this.editor != null && this.editor.getEventBus() != null) {
            this.editor.getEventBus().fire(event);
        }
    }

    public synchronized <T extends Event> void fire(Class<T> event) {
        if (this.editor != null && this.editor.getEventBus() != null) {
            this.editor.getEventBus().fire(event);
        }
    }

    @Override
    public void run() {

        this.executor.submit(this.editor);
        getView().run();

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

}
