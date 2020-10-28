package me.bokov.bsc.surfaceviewer.glsl;

public class GLSLParenthesesStatement extends GLSLStatement {

    private final GLSLStatement statement;

    public GLSLParenthesesStatement(GLSLStatement statement) {
        this.statement = statement;
    }

    @Override
    public String getKind() {
        return "GLSLParentheses";
    }

    @Override
    public String render() {
        return String.format("( %s )", statement.render());
    }
}
