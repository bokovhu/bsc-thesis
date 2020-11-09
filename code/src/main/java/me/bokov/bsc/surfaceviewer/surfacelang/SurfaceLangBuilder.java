package me.bokov.bsc.surfaceviewer.surfacelang;

public class SurfaceLangBuilder {

    private final SurfaceLangScope globalScope = new SurfaceLangScope();

    public SurfaceLangBuilder putConstant(ScopedVariable constant) {
        this.globalScope.putVariable(constant);
        return this;
    }

}
