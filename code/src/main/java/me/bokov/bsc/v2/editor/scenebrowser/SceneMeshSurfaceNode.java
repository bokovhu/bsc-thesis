package me.bokov.bsc.v2.editor.scenebrowser;

import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.v2.SceneMesh;
import me.bokov.bsc.v2.SceneMeshSurface;

import javax.swing.tree.DefaultMutableTreeNode;

public class SceneMeshSurfaceNode extends DefaultMutableTreeNode {

    private final SceneMesh mesh;
    private final SceneMeshSurface surface;

    public SceneMeshSurfaceNode(SceneMesh mesh, SceneMeshSurface surface) {
        super(surface.getDisplayName(), true);
        this.mesh = mesh;
        this.surface = surface;

        final var childSurfaces = surface.getChildSurfaces();
        if (childSurfaces != null) {
            for (var child : childSurfaces) {
                add(new SceneMeshSurfaceNode(mesh, child));
            }
        }

    }

    public Evaluatable<Float, CPUContext, GPUContext> toEvaluatable() {
        return this.surface.toEvaluatable();
    }

    public SceneMeshSurface getSurface() {
        return surface;
    }
}
