package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.editor.Icons;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

import javax.swing.*;
import java.io.Serializable;
import java.util.*;

public abstract class MeshSurface implements Serializable {

    public abstract String getDisplayName();

    public ImageIcon getImageIcon() {
        return Icons.FA_QUESTION_SOLID_BLACK;
    }

    public abstract Evaluatable<Float, CPUContext, GPUContext> toEvaluatable();

    public List<MeshSurface> getChildSurfaces() {
        return Collections.emptyList();
    }

}
