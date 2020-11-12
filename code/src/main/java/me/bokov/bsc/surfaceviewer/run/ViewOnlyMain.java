package me.bokov.bsc.surfaceviewer.run;

import me.bokov.bsc.surfaceviewer.scene.BaseWorld;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.view.ViewConfiguration;

public class ViewOnlyMain {

    private static World testWorld() {

        return new BaseWorld();

    }

    public static void main(String[] args) {

        System.out.println("PID: " + ProcessHandle.current().pid());
        var app = new ViewOnlyApp();
        app.getViewClient()
                .changeConfig(
                        ViewConfiguration.builder()
                                .world(testWorld())
                                .build()
                );
        app.run();

    }

}
