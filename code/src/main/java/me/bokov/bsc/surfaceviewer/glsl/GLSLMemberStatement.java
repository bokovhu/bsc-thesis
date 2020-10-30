package me.bokov.bsc.surfaceviewer.glsl;

import java.util.*;

public class GLSLMemberStatement extends GLSLStatement {

    private final List<String> path;

    public GLSLMemberStatement(List<String> path) {
        this.path = new ArrayList<>(path);
    }

    @Override
    public String getKind() {
        return "GLSLMember";
    }

    @Override
    public String render() {
        return String.join(".", path);
    }
}
