package me.bokov.bsc.surfaceviewer.glsl;

public class GLSLVariableDeclarationStatement extends GLSLStatement {

    private final String type;
    private final String name;
    private final GLSLStatement defaultValue;

    public GLSLVariableDeclarationStatement(
            String type,
            String name,
            GLSLStatement defaultValue
    ) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public GLSLVariableDeclarationStatement(
            String type,
            String name
    ) {
        this(type, name, null);
    }

    @Override
    public String getKind() {
        return "GLSLVariableDeclaration";
    }

    @Override
    public String render() {
        if (defaultValue != null) {
            return String.format("%s %s = %s", type, name, defaultValue.render());
        }
        return String.format("%s %s", type, name);
    }
}
