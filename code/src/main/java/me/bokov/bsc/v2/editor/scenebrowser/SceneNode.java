package me.bokov.bsc.v2.editor.scenebrowser;

import me.bokov.bsc.v2.Scene;

import javax.swing.tree.DefaultMutableTreeNode;

public class SceneNode extends DefaultMutableTreeNode {

    private final Scene scene;

    public SceneNode(Scene scene) {
        super(scene.getName(), true);
        this.scene = scene;

        add(new SceneLightingNode(scene));
        add(new SceneMeshesNode(scene));
    }

    public SceneNode() {
        super("No scene selected", false);
        this.scene = null;
    }

    public Scene getScene() {
        return scene;
    }


}
