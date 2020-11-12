package me.bokov.bsc.surfaceviewer.scene;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class NodeDisplay implements Serializable {

    private String name;

}
