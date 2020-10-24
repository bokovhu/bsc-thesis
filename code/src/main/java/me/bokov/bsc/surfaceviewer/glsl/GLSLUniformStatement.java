package me.bokov.bsc.surfaceviewer.glsl;

public class GLSLUniformStatement extends GLSLStatement {

    private final String type;
    private final String name;
    private final GLSLStatement defaultValue;

    public GLSLUniformStatement(
            String type,
            String name,
            GLSLStatement defaultValue
    ) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getKind() {
        return "GLSLUniform";
    }

    @Override
    public String render() {

        if (defaultValue != null) {
            return String.format("uniform %s %s = %s;", type, name, defaultValue.render());
        }
        return String.format("uniform %s %s;", type, name);
    }
}
