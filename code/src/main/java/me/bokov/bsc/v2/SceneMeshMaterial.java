package me.bokov.bsc.v2;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.io.Serializable;

@Deprecated
@Getter
@Setter
public class SceneMeshMaterial implements Serializable {

    private String name = "New material";
    private Vector3f ambient = new Vector3f();
    private Vector3f diffuse = new Vector3f(0.145f, 0.2f, 0.11f);
    private float shininess = 100.0f;

}
