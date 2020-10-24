package me.bokov.bsc.surfaceviewer.glsl;

public class GLSLVaryingStatement extends GLSLStatement {

    private final String type;
    private final String name;

    public GLSLVaryingStatement(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String getKind() {
        return "GLSLVarying";
    }

    @Override
    public String render() {
        return String.format("varying %s %s;", type, name);
    }
}
