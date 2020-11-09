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
public class World implements Serializable {

    private String name = "New scene";
    private List<MeshSurface> meshes = new ArrayList<>();

    public static World cloneWorld(World world) {

        return IOUtil.serialize(world);

    }

    public Evaluatable<Float, CPUContext, GPUContext> toUnion() {

        return Evaluetables.union(meshes.stream().map(MeshSurface::toEvaluatable)
                .collect(Collectors.toList()));

    }

}
