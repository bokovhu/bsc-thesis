package me.bokov.bsc.surfaceviewer.glsl;

public class GLSLLayoutStatement extends GLSLStatement {

    private final int location;
    private final String inOut;
    private final String type;
    private final String name;

    public GLSLLayoutStatement(
            int location,
            String inOut,
            String type,
            String name
    ) {
        this.location = location;
        this.inOut = inOut;
        this.type = type;
        this.name = name;
    }

    @Override
    public String getKind() {
        return "GLSLLayout";
    }

    @Override
    public String render() {
        return String
                .format("layout(location = %d) %s %s %s;", this.location, this.inOut, this.type,
                        this.name
                );
    }

}
