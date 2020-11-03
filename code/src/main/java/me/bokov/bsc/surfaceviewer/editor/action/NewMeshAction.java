package me.bokov.bsc.surfaceviewer.editor.action;

import me.bokov.bsc.surfaceviewer.Editor;
import me.bokov.bsc.surfaceviewer.SceneMeshSurface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.function.*;

public class NewMeshAction extends AbstractAction {

    private final Editor editor;
    private final Supplier<SceneMeshSurface> rootSurfaceFactory;

    public NewMeshAction(
            Editor editor,
            Supplier<SceneMeshSurface> rootSurfaceFactory,
            String displayName,
            ImageIcon icon
    ) {
        super(displayName, icon);
        this.editor = editor;
        this.rootSurfaceFactory = rootSurfaceFactory;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        List<SceneMeshSurface> newMeshes = new ArrayList<>(editor.getScene().getMeshes());
        newMeshes.add(this.rootSurfaceFactory.get());

        editor.getScene().setMeshes(newMeshes);
        editor.applySceneChanges();

    }

}
