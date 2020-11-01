package me.bokov.bsc.v2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public
class Property<T> {
    private final PropertyType type;
    private final String group;
    private final String name;
    private final T defaultValue;
}
