package me.bokov.bsc.surfaceviewer.editor.scenebrowser;

import me.bokov.bsc.surfaceviewer.MeshSurface;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

import javax.swing.tree.DefaultMutableTreeNode;

public class SceneMeshSurfaceNode extends DefaultMutableTreeNode {

    private final MeshSurface surface;

    public SceneMeshSurfaceNode(MeshSurface surface) {
        super(surface.getDisplayName(), true);
        this.surface = surface;

        final var childSurfaces = surface.getChildSurfaces();
        if (childSurfaces != null) {
            for (var child : childSurfaces) {
                add(new SceneMeshSurfaceNode(child));
            }
        }

    }

    public Evaluatable<Float, CPUContext, GPUContext> toEvaluatable() {
        return this.surface.toEvaluatable();
    }

    public MeshSurface getSurface() {
        return surface;
    }
}
