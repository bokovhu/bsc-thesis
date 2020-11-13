package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.view.ViewClient;

import java.util.*;

public interface App {

    ViewClient getViewClient();
    boolean shouldQuit();
    void markShouldQuit();
    long runtimeMilliseconds();
    default void onViewReport(String event, Map<String, Object> properties) {}

}
