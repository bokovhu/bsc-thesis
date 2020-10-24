package me.bokov.bsc.surfaceviewer.glsl;

public class GLSLBinaryExpressionStatement extends GLSLStatement {

    private final GLSLStatement lhs;
    private final GLSLStatement rhs;
    private final String operator;

    public GLSLBinaryExpressionStatement(
            GLSLStatement lhs,
            GLSLStatement rhs,
            String operator
    ) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = operator;
    }

    @Override
    public String getKind() {
        return "GLSLBinaryExpression";
    }

    @Override
    public String render() {
        return String.format("%s %s %s", lhs.render(), operator, rhs.render());
    }
}
