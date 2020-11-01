package me.bokov.bsc.v2.editor.action;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.SceneMesh;
import me.bokov.bsc.v2.SceneMeshSurface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.function.*;

public class NewMeshAction extends AbstractAction {

    private final Editor editor;
    private final Supplier<SceneMeshSurface> rootSurfaceFactory;

    public NewMeshAction(Editor editor, Supplier<SceneMeshSurface> rootSurfaceFactory, String displayName, ImageIcon icon) {
        super(displayName, icon);
        this.editor = editor;
        this.rootSurfaceFactory = rootSurfaceFactory;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        List<SceneMesh> newMeshes = new ArrayList<>(editor.getScene().getMeshes());
        final SceneMesh newMesh = new SceneMesh();
        newMesh.setSurface(this.rootSurfaceFactory.get());
        newMeshes.add(newMesh);

        editor.getScene().setMeshes(newMeshes);
        editor.applySceneChanges();

    }

}
