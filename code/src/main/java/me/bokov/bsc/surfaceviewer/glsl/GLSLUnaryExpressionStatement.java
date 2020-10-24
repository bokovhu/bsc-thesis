package me.bokov.bsc.surfaceviewer.glsl;

public class GLSLUnaryExpressionStatement extends GLSLStatement {

    private final GLSLStatement lhs;
    private final String operator;
    private final boolean pre;

    public GLSLUnaryExpressionStatement(GLSLStatement lhs, String operator,
            boolean pre
    ) {
        this.lhs = lhs;
        this.operator = operator;
        this.pre = pre;
    }

    @Override
    public String getKind() {
        return "GLSLUnaryExpression";
    }

    @Override
    public String render() {
        if (pre) {
            return String.format("%s %s", operator, lhs);
        }
        return String.format("%s %s", lhs, operator);
    }
}
