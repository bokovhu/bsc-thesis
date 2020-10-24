package me.bokov.bsc.surfaceviewer.glsl;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class GLSLFunctionCallStatement extends GLSLStatement {

    private final String functionName;
    private final List<GLSLStatement> parameters;

    public GLSLFunctionCallStatement(
            String functionName,
            List<GLSLStatement> parameters
    ) {
        this.functionName = functionName;
        this.parameters = new ArrayList<>(parameters);
    }

    @Override
    public String getKind() {
        return "GLSLFunctionCall";
    }

    @Override
    public String render() {
        return String.format("%s(%s)", functionName,
                parameters.stream().map(p -> p.render()).collect(joining(", "))
        );
    }
}
