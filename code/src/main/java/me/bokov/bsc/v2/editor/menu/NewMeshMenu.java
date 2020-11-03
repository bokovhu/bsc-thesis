package me.bokov.bsc.v2.editor.menu;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.editor.Icons;
import me.bokov.bsc.v2.editor.action.NewMeshAction;
import me.bokov.bsc.v2.editor.surface.MultiOperatorSurface;
import me.bokov.bsc.v2.editor.surface.ShapeSurface;

import javax.swing.*;
import java.util.*;

public class NewMeshMenu extends JMenu {

    private final Editor editor;

    public NewMeshMenu(Editor editor) {
        super("New mesh");
        this.editor = editor;
    }

    public NewMeshMenu create() {

        add(
                new NewMeshAction(
                        editor,
                        () -> new ShapeSurface(UUID.randomUUID().toString(), ShapeSurface.ShapeKind.BOX),
                        "Box",
                        Icons.FA_CUBES_SOLID_BLACK
                )
        );

        add(
                new NewMeshAction(
                        editor,
                        () -> new ShapeSurface(UUID.randomUUID().toString(), ShapeSurface.ShapeKind.SPHERE),
                        "Sphere",
                        Icons.FA_CUBES_SOLID_BLACK
                )
        );

        add(
                new NewMeshAction(
                        editor,
                        () -> new MultiOperatorSurface(UUID.randomUUID().toString(), MultiOperatorSurface.OperatorKind.UNION),
                        "Union",
                        Icons.FA_CUBES_SOLID_BLACK
                )
        );

        return this;
    }

}
