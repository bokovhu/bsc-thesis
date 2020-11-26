package me.bokov.bsc.surfaceviewer.view;

import java.awt.image.BufferedImage;
import java.util.function.*;

public interface ViewClient {

    void changeConfig(ViewConfiguration configuration);
    RendererConfig reportRendererConfig();
    void enqueueRender(int w, int h, Consumer<BufferedImage> callback);

}
