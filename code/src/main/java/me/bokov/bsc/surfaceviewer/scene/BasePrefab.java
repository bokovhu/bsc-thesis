package me.bokov.bsc.surfaceviewer.scene;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class BasePrefab implements Prefab, Serializable {

    @Getter
    private final int id;

    @Getter @Setter
    private String name;
    @Getter @Setter
    private SceneNode node;

    public BasePrefab(int id) {
        this.id = id;
    }

}
