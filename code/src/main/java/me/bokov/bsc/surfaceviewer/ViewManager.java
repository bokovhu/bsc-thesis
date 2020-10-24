package me.bokov.bsc.surfaceviewer;

import java.util.Map;
import java.util.function.BiFunction;
import me.bokov.bsc.surfaceviewer.view.AppView;
import me.bokov.bsc.surfaceviewer.view.MarchingCubesView;
import me.bokov.bsc.surfaceviewer.view.RayMarchingView;
import me.bokov.bsc.surfaceviewer.view.VoxelsView;

// Intentionally package-private
final class ViewManager {

    private static final Map<String, BiFunction<AppScene, SurfaceViewerPlatform, AppView>> VIEW_FACTORIES = Map
            .of(
                    "marching-cubes", MarchingCubesView::new,
                    "ray-marching", RayMarchingView::new,
                    "voxel-point-cloud", VoxelsView::new
            );

    private final SurfaceViewerPlatform platform;

    private AppView currentView;
    private AppScene currentScene;

    ViewManager(SurfaceViewerPlatform platform) {
        this.platform = platform;
    }

    public void initInitialView() {
        initInitialView(VIEW_FACTORIES.keySet().stream().findFirst().get());
    }

    public void initInitialView(String viewName) {
        this.currentView = VIEW_FACTORIES.get(viewName)
                .apply(this.currentScene, this.platform);
        this.currentView.init();
    }

    public AppView view() {
        return this.currentView;
    }

    public AppScene scene() {
        return this.currentScene;
    }

    public void changeView(String newViewName) {
        this.currentView.tearDown();
        this.currentView = VIEW_FACTORIES.get(newViewName)
                .apply(this.currentScene, this.platform);
        this.currentView.init();
    }

    public void changeScene(AppScene newScene) {
        changeScene(newScene, true);
    }

    public void changeScene(AppScene newScene, boolean notify) {
        this.currentScene = newScene;
        if (notify) {
            this.currentView.onSceneChanged(newScene);
        }
    }

}