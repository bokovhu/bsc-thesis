package me.bokov.bsc.surfaceviewer.editorv2.service;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.App;

import java.awt.image.BufferedImage;

public class RenderSceneTask extends Task<BufferedImage> {

    @Getter
    private final IntegerProperty renderWidthProperty = new SimpleIntegerProperty(1920);

    @Getter
    private final IntegerProperty renderHeightProperty = new SimpleIntegerProperty(1080);

    @Getter
    private final ObjectProperty<App> appProperty = new SimpleObjectProperty<>();


    private final Object waitObj = new Object();
    private BufferedImage result = null;


    private void onRenderResult(BufferedImage result) {

        this.result = result;

        synchronized (waitObj) {
            waitObj.notifyAll();
        }

    }


    private BufferedImage waitForResult() {

        synchronized (waitObj) {
            try {
                waitObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;

    }

    @Override
    protected BufferedImage call() throws Exception {

        final var app = appProperty.get();

        if (app == null) {
            throw new IllegalArgumentException("app cannot be null!");
        }


        app.getViewClient()
                .enqueueRender(
                        renderWidthProperty.get(),
                        renderHeightProperty.get(),
                        this::onRenderResult
                );

        return waitForResult();
    }
}
