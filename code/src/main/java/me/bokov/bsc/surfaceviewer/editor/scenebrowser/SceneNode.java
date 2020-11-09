package me.bokov.bsc.surfaceviewer.editor.scenebrowser;

import me.bokov.bsc.surfaceviewer.World;
import me.bokov.bsc.surfaceviewer.MeshSurface;

import javax.swing.tree.DefaultMutableTreeNode;

public class SceneNode extends DefaultMutableTreeNode {

    private final World world;

    public SceneNode(World world) {
        super(world.getName(), true);
        this.world = world;

        for (MeshSurface surface : this.world.getMeshes()) {
            add(new SceneMeshSurfaceNode(surface));
        }
    }

    public SceneNode() {
        super("No scene selected", false);
        this.world = null;
    }

    public World getScene() {
        return world;
    }


}
