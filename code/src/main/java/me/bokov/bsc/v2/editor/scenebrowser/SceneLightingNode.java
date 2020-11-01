package me.bokov.bsc.v2.editor.scenebrowser;

import me.bokov.bsc.v2.Scene;
import me.bokov.bsc.v2.SceneLight;

import javax.swing.tree.DefaultMutableTreeNode;

public class SceneLightingNode extends DefaultMutableTreeNode {

    private final Scene scene;

    public SceneLightingNode(Scene scene) {
        super("Lighting", true);
        this.scene = scene;

        for (SceneLight light : scene.getLights()) {
            add(new SceneLightNode(light));
        }
    }
}
