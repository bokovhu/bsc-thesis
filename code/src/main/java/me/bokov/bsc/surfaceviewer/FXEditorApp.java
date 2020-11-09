package me.bokov.bsc.surfaceviewer;

import javafx.application.Platform;
import me.bokov.bsc.surfaceviewer.editorv2.FXEditor;
import me.bokov.bsc.surfaceviewer.event.Event;

import java.util.concurrent.*;

public class FXEditorApp extends AppBase {

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void run() {

        executor.submit(
                () -> FXEditor.main(new String[0])
        );
        getView().run();

        Platform.exit();
        this.executor.shutdownNow();

        try {
            this.executor.awaitTermination(3600, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public <T extends Event> void fire(T event) {

    }

    @Override
    public <T extends Event> void fire(Class<T> eventClass) {

    }

}
