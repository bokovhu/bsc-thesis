package me.bokov.bsc.v2;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.v2.editor.Icons;

import javax.swing.*;
import java.io.Serializable;
import java.util.*;

@Getter
@EqualsAndHashCode
public abstract class SceneMeshSurface implements Serializable {

    private final String id;

    protected SceneMeshSurface(String id) {
        this.id = id;
    }

    public abstract String getDisplayName();

    public ImageIcon getImageIcon() {
        return Icons.FA_QUESTION_SOLID_BLACK;
    }

    public abstract Evaluatable<Float, CPUContext, GPUContext> toEvaluatable();

    public List<SceneMeshSurface> getChildSurfaces() {
        return Collections.emptyList();
    }

}
