package me.bokov.bsc.surfaceviewer.scene;

import java.io.Serializable;

public interface ResourceTexture extends Serializable {

    int getId();
    String name();
    String location();
    
}
