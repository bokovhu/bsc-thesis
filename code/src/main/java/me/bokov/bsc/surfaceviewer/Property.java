package me.bokov.bsc.surfaceviewer;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class Property<T extends Serializable> implements Serializable {
    private final PropertyType type;
    private final String group;
    private final String name;
    private final T defaultValue;
}
