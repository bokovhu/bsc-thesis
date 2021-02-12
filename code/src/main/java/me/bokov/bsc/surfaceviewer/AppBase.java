package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.util.IOUtil;
import me.bokov.bsc.surfaceviewer.view.RendererConfig;
import me.bokov.bsc.surfaceviewer.view.ViewClient;
import me.bokov.bsc.surfaceviewer.view.ViewConfiguration;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public abstract class AppBase implements App, Runnable {

    private final long createdTime = System.currentTimeMillis();

    protected View view = new View(this);
    private AtomicBoolean shouldQuit = new AtomicBoolean(false);

    public boolean shouldQuit() {
        return shouldQuit.get();
    }

    public synchronized void markShouldQuit() {
        this.shouldQuit.set(true);
    }

    public long runtimeMilliseconds() {
        return System.currentTimeMillis() - this.createdTime;
    }

    protected static class ViewClientImpl implements ViewClient {

        private final View view;
        private final App app;

        public ViewClientImpl(View view, App app) {
            this.view = view;
            this.app = app;
        }

        @Override
        public synchronized void changeConfig(ViewConfiguration configuration) {

            view.changeConfig(configuration);

        }

        public synchronized RendererConfig reportRendererConfig() {

            return view.provideRendererConfig();

        }

        @Override
        public synchronized void enqueueRender(int w, int h, Consumer<BufferedImage> callback) {

            view.enqueueRender(w, h, callback);

        }

    }

}
