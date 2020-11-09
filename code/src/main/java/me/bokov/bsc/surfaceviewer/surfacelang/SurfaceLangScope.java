package me.bokov.bsc.surfaceviewer.surfacelang;

import java.util.*;

public class SurfaceLangScope {

    private final Map<String, ScopedVariable> variables = new HashMap<>();

    public Optional<ScopedVariable> getVariable(String variableName) {
        return Optional.ofNullable(variables.get(variableName));
    }

    public SurfaceLangScope putVariable(ScopedVariable variable) {
        this.variables.put(variable.getName(), variable);
        return this;
    }

    public SurfaceLangScope branch() {

        var newScope = new SurfaceLangScope();
        newScope.variables.putAll(this.variables);
        return newScope;

    }

}
