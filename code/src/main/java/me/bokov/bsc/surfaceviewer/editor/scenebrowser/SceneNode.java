package me.bokov.bsc.surfaceviewer.editor.scenebrowser;

import me.bokov.bsc.surfaceviewer.Scene;
import me.bokov.bsc.surfaceviewer.SceneMeshSurface;

import javax.swing.tree.DefaultMutableTreeNode;

public class SceneNode extends DefaultMutableTreeNode {

    private final Scene scene;

    public SceneNode(Scene scene) {
        super(scene.getName(), true);
        this.scene = scene;

        for (SceneMeshSurface surface : this.scene.getMeshes()) {
            add(new SceneMeshSurfaceNode(surface));
        }
    }

    public SceneNode() {
        super("No scene selected", false);
        this.scene = null;
    }

    public Scene getScene() {
        return scene;
    }


}
