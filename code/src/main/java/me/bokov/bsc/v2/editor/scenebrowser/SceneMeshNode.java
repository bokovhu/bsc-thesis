package me.bokov.bsc.v2.editor.scenebrowser;

import me.bokov.bsc.v2.SceneMesh;

import javax.swing.tree.DefaultMutableTreeNode;

public class SceneMeshNode extends DefaultMutableTreeNode {

    private final SceneMesh mesh;

    public SceneMeshNode(SceneMesh mesh) {
        super(mesh.getName(), true);
        this.mesh = mesh;

        add(new SceneMeshSurfaceNode(mesh, mesh.getSurface()));
        add(new SceneMeshMaterialNode(mesh, mesh.getMaterial()));
    }

}
