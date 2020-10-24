package me.bokov.bsc.surfaceviewer.glsl;

public class GLSLReturnStatement extends GLSLStatement {

    private final GLSLStatement returnValue;

    public GLSLReturnStatement(GLSLStatement returnValue) {
        this.returnValue = returnValue;
    }


    @Override
    public String getKind() {
        return "GLSLReturn";
    }

    @Override
    public String render() {
        return String.format("return %s", returnValue.render());
    }
}
