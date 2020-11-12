package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.view.ViewClient;

public interface App {

    ViewClient getViewClient();

    boolean shouldQuit();

    void markShouldQuit();

    long runtimeMilliseconds();

}
