package me.bokov.bsc.surfaceviewer.view;

public interface ViewClient {

    void changeConfig(ViewConfiguration configuration);
    RendererConfig reportRendererConfig();

}
