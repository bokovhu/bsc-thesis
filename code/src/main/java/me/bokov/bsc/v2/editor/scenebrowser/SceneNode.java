package me.bokov.bsc.v2.editor.scenebrowser;

import me.bokov.bsc.v2.Scene;
import me.bokov.bsc.v2.SceneMesh;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

public class SceneNode extends DefaultMutableTreeNode {

    private final Scene scene;

    public SceneNode(Scene scene) {
        super(scene.getName(), true);
        this.scene = scene;

        for (SceneMesh mesh : this.scene.getMeshes()) {
            add(new SceneMeshSurfaceNode(mesh, mesh.getSurface()));
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
