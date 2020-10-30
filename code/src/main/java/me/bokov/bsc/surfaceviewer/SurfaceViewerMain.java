package me.bokov.bsc.surfaceviewer;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.*;

// Intentionally package-private
final class SurfaceViewerMain {

    public static void main(String[] args) {

        System.out.println(ProcessHandle.current().pid());

        // To debug using renderdoc, place a breakpoint on the next line,
        // and inject into the process identified by the printed PID
        // from RenderDoc before allowing the debugger to step inside the
        // function.

        int exitCode = new CommandLine(new Run())
                .execute(args);

    }

    @Command(name = "run",
            mixinStandardHelpOptions = true,
            version = "run 0.0.1",
            description = "Runs the surface viewer application")
    static class Run implements Runnable {

        @Option(names = {"-s", "--scene"}, description = "Name of the scene to show")
        private String scene = "cube-minus-sphere";

        @Option(names = {
                "-ww", "--window-width"
        }, description = "Width of the window to create in pixels")
        private int windowWidth = -1;

        @Option(names = {
                "-wh", "--window-height"
        }, description = "Height of the window to create in pixels")
        private int windowHeight = -1;

        @Option(names = {"-wf", "--full-screen"}, description = "Launch in full screen mods")
        private boolean windowFullscreen = false;

        @Option(names = {
                "-wmptr", "--window-monitor-pointer"
        }, description = "DO NOT USE IF YOU DO NOT KNOW WHAT YOU ARE DOING")
        private long windowMonitorPointer = 0L;

        @Option(names = {
                "-v", "--view"
        }, description = "Name of the view to use for the presentation of the surface")
        private String view = "marching-cubes";

        @Option(names = {
                "-vopt", "--view-opt"
        }, description = "Additional options for the view to create")
        private Map<String, String> viewOpts = new HashMap<>();

        @Override
        public void run() {

            final var cfg = new AppConfig()
                    .setWidth(windowWidth)
                    .setHeight(windowHeight)
                    .setFullscreen(windowFullscreen)
                    .setMonitor(windowMonitorPointer)
                    .setSceneName(scene)
                    .setViewName(view)
                    .setViewOpts(new HashMap<>(viewOpts));

            final var app = new SurfaceViewerApplication(cfg);

            app.run();

        }
    }

}
