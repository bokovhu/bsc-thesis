package me.bokov.bsc.v2.editor.scenebrowser;

import me.bokov.bsc.v2.SceneMesh;
import me.bokov.bsc.v2.SceneMeshMaterial;

import javax.swing.tree.DefaultMutableTreeNode;

public class SceneMeshMaterialNode extends DefaultMutableTreeNode {

    private final SceneMesh mesh;
    private final SceneMeshMaterial material;

    public SceneMeshMaterialNode(SceneMesh mesh, SceneMeshMaterial material) {
        super(material.getName(), false);
        this.mesh = mesh;
        this.material = material;
    }
}
