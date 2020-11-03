package me.bokov.bsc.surfaceviewer;

import lombok.Getter;
import lombok.Setter;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.Evaluetables;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.util.IOUtil;

import java.io.Serializable;
import java.util.*;
import java.util.stream.*;

@Getter
@Setter
public class Scene implements Serializable {

    private String name = "New scene";
    private List<SceneMeshSurface> meshes = new ArrayList<>();

    public static Scene cloneScene(Scene scene) {

        return IOUtil.serialize(scene);

    }

    public Evaluatable<Float, CPUContext, GPUContext> toUnion() {

        return Evaluetables.union(meshes.stream().map(SceneMeshSurface::toEvaluatable)
                .collect(Collectors.toList()));

    }

}
