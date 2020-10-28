package me.bokov.bsc.surfaceviewer.glsl;

import static java.util.stream.Collectors.joining;

import java.util.List;

public class GLSLBlockStatement extends GLSLStatement {

    private final List<GLSLStatement> body;

    public GLSLBlockStatement(List<GLSLStatement> body) {
        this.body = body;
    }

    @Override
    public String getKind() {
        return "GLSLBlock";
    }

    @Override
    public String render() {
        return String.format(
                "{\n%s\n}",
                body.stream().map(s -> s.render() + ";").collect(joining("\n"))
        );
    }
}
