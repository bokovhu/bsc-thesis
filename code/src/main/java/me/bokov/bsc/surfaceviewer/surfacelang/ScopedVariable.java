package me.bokov.bsc.surfaceviewer.surfacelang;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ScopedVariable {

    private final SurfaceLangPrimitive type;
    private final String name;

    public ScopedVariable(SurfaceLangPrimitive type, String name) {
        this.type = type;
        this.name = name;
    }

}
