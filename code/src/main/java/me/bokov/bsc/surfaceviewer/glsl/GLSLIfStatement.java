package me.bokov.bsc.surfaceviewer.glsl;

import java.util.*;

import static java.util.stream.Collectors.*;

public class GLSLIfStatement extends GLSLStatement {

    private final GLSLStatement test;
    private final List<GLSLStatement> body;
    private final GLSLStatement ifElse;

    public GLSLIfStatement(
            GLSLStatement test, List<GLSLStatement> body,
            GLSLStatement ifElse
    ) {
        this.test = test;
        this.body = body;
        this.ifElse = ifElse;
    }

    @Override
    public String getKind() {
        return "GLSLIf";
    }

    @Override
    public String render() {
        if (ifElse != null) {
            return String.format("if (%s) { \n %s \n } else %s", test.render(),
                    body.stream().map(s -> s.render() + ";").collect(joining("\n")),
                    ifElse.render()
            );
        }
        return String.format("if (%s) { \n %s \n }", test.render(),
                body.stream().map(s -> s.render() + ";").collect(joining("\n"))
        );
    }
}
