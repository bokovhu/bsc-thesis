package me.bokov.bsc.surfaceviewer.editor.surface;

import me.bokov.bsc.surfaceviewer.MeshSurface;
import me.bokov.bsc.surfaceviewer.editor.Icons;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.Evaluetables;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

import javax.swing.*;
import java.util.*;
import java.util.stream.*;

public class MultiOperatorSurface extends MeshSurface {

    private final OperatorKind kind;
    private final List<MeshSurface> children = new ArrayList<>();

    public MultiOperatorSurface(OperatorKind kind) {
        this.kind = kind;
    }

    public MultiOperatorSurface addChild(MeshSurface newChild) {
        this.children.add(newChild);
        return this;
    }

    @Override
    public String getDisplayName() {
        return kind.displayName;
    }

    @Override
    public Evaluatable<Float, CPUContext, GPUContext> toEvaluatable() {

        final List<Evaluatable<Float, CPUContext, GPUContext>> evaluatables = children.stream()
                .map(MeshSurface::toEvaluatable)
                .collect(Collectors.toList());

        switch (kind) {
            case UNION:
                return Evaluetables.union(evaluatables);
            case INTERSECT:
                return Evaluetables.intersect(evaluatables);
            case SMOOTH_UNION:
            case SMOOTH_INTERSECT:
            default:
                throw new UnsupportedOperationException("Not yet supported.");
        }
    }

    @Override
    public List<MeshSurface> getChildSurfaces() {
        return Collections.unmodifiableList(children);
    }

    public enum OperatorKind {
        UNION("Union", Icons.FA_CUBES_SOLID_BLACK),
        INTERSECT("Intersect", Icons.FA_CUBES_SOLID_BLACK),
        SMOOTH_UNION("Smooth union", Icons.FA_CUBES_SOLID_BLACK),
        SMOOTH_INTERSECT("Smooth intersect", Icons.FA_CUBES_SOLID_BLACK);
        public final String displayName;
        public final ImageIcon icon;

        OperatorKind(String displayName, ImageIcon icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
    }
}
