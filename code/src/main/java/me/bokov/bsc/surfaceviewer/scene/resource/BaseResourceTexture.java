package me.bokov.bsc.surfaceviewer.scene.resource;

import me.bokov.bsc.surfaceviewer.scene.ResourceTexture;

public class BaseResourceTexture implements ResourceTexture {

    private final int id;
    private final String name;
    private final String location;

    public BaseResourceTexture(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String location() {
        return location;
    }
}
