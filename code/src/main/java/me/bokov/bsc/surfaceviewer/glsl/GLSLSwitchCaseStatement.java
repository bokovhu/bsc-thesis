package me.bokov.bsc.surfaceviewer.glsl;

import java.util.*;

import static java.util.stream.Collectors.*;

public class GLSLSwitchCaseStatement extends GLSLStatement {

    private GLSLStatement condition;
    private Map<GLSLStatement, List<GLSLStatement>> cases = new HashMap<>();

    public GLSLSwitchCaseStatement(GLSLStatement condition) {
        this.condition = condition;
    }

    public GLSLSwitchCaseStatement switchCase(GLSLStatement label, List<GLSLStatement> body) {
        cases.put(label, body);
        return this;
    }

    @Override
    public String getKind() {
        return "GLSLSwitchCase";
    }

    @Override
    public String render() {
        return String.format(
                "switch(%s) {\n%s\n}",
                condition.render(),
                cases.keySet().stream().map(
                        s -> String.format(
                                "case %s:\n%s\nbreak;",
                                s.render(),
                                cases.get(s).stream().map(
                                        b -> b.render() + ";"
                                ).collect(joining("\n"))
                        )
                ).collect(joining("\n"))
        );
    }
}
