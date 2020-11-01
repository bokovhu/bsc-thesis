package me.bokov.bsc.v2.editor.scenebrowser;

import me.bokov.bsc.v2.Scene;
import me.bokov.bsc.v2.SceneMesh;

import javax.swing.tree.DefaultMutableTreeNode;

public class SceneMeshesNode extends DefaultMutableTreeNode {

    private final Scene scene;

    public SceneMeshesNode(Scene scene) {
        super("Meshes", true);
        this.scene = scene;

        for(SceneMesh mesh : this.scene.getMeshes()) {
            add(new SceneMeshNode(mesh));
        }
    }
}
