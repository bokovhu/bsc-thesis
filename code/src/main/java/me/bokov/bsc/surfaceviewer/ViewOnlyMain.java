package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.editor.surface.ShapeSurface;
import me.bokov.bsc.surfaceviewer.scene.World;

import java.util.*;

public class ViewOnlyMain {

    private static World testWorld() {

        World w = new World();

        w.setName("Test world");
        w.setMeshes(
                List.of(
                        new ShapeSurface(ShapeSurface.ShapeKind.SPHERE)
                )
        );

        return w;

    }

    public static void main(String [] args) {

        System.out.println("PID: " + ProcessHandle.current().pid());
        var app = new ViewOnlyApp();
        app.sendSceneToView(testWorld());
        app.run();

    }

}
