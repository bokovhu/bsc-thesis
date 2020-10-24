package me.bokov.bsc.surfaceviewer.glsl;

public class GLSLOutStatement extends GLSLStatement {

    private final String type;
    private final String name;

    public GLSLOutStatement(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String getKind() {
        return "GLSLOut";
    }

    @Override
    public String render() {
        return String.format("out %s %s;", type, name);
    }
}
