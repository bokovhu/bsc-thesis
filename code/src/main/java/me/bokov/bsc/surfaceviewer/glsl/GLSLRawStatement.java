package me.bokov.bsc.surfaceviewer.glsl;

public class GLSLRawStatement extends GLSLStatement {

    private final String rawCode;

    public GLSLRawStatement(String rawCode) {
        this.rawCode = rawCode;
    }

    @Override
    public String getKind() {
        return "RAW";
    }

    @Override
    public String render() {
        return rawCode;
    }
}
