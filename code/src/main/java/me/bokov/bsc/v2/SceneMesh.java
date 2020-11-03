package me.bokov.bsc.v2;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Deprecated
@Getter
@Setter
public class SceneMesh implements Serializable {

    private String name = "New mesh";
    private SceneMeshMaterial material = new SceneMeshMaterial();
    private SceneMeshSurface surface = null;

}
