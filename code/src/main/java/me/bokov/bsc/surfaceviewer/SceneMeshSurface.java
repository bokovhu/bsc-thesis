package me.bokov.bsc.surfaceviewer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.editor.Icons;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

import javax.swing.*;
import java.io.Serializable;
import java.util.*;

// TODO: Mutator functions, and cleanup
@Getter
@EqualsAndHashCode
public abstract class SceneMeshSurface implements Serializable {

    public abstract String getDisplayName();

    public ImageIcon getImageIcon() {
        return Icons.FA_QUESTION_SOLID_BLACK;
    }

    public abstract Evaluatable<Float, CPUContext, GPUContext> toEvaluatable();

    public List<SceneMeshSurface> getChildSurfaces() {
        return Collections.emptyList();
    }

}
