package me.bokov.bsc.v2.editor.scenebrowser;

import me.bokov.bsc.v2.SceneLight;

import javax.swing.tree.DefaultMutableTreeNode;

public class SceneLightNode extends DefaultMutableTreeNode {

    private final SceneLight light;

    public SceneLightNode(SceneLight light) {
        super(light.getName(), false);
        this.light = light;
    }
}
