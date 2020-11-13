package me.bokov.bsc.surfaceviewer.run;

import javafx.application.Platform;
import lombok.Setter;
import me.bokov.bsc.surfaceviewer.AppBase;
import me.bokov.bsc.surfaceviewer.editorv2.FXEditor;
import me.bokov.bsc.surfaceviewer.view.ViewClient;

import java.util.*;
import java.util.concurrent.*;

public class FXEditorApp extends AppBase {

    public static FXEditorApp INSTANCE = null;
    private final ViewClient viewClient = new ViewClientImpl(view, this);
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    @Setter
    private ViewReportCallback viewReportCallback = (eventType, properties) -> {};

    @Override
    public void run() {

        INSTANCE = this;

        executor.submit(
                () -> FXEditor.main(new String[0])
        );
        view.run();

        Platform.exit();
        this.executor.shutdownNow();

        try {
            this.executor.awaitTermination(3600, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ViewClient getViewClient() {
        return viewClient;
    }

    public interface ViewReportCallback {
        void onViewReport(String eventType, Map<String, Object> properties);
    }

    @Override
    public void onViewReport(String event, Map<String, Object> properties) {
        viewReportCallback.onViewReport(event, properties);
    }
}
