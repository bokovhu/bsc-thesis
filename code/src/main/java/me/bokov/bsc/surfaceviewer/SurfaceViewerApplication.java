package me.bokov.bsc.surfaceviewer;

@Deprecated
public class SurfaceViewerApplication implements Runnable {

    private final SurfaceViewerPlatform platform;

    public SurfaceViewerApplication(AppConfig config) {
        this.platform = new SurfaceViewerPlatform(config);
    }

    @Override
    public void run() {

        platform.init();
        platform.startMainLoop();
        platform.tearDown();

    }
}
