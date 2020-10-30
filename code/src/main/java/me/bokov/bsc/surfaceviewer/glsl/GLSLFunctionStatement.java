package me.bokov.bsc.surfaceviewer.glsl;

import java.util.*;

import static java.util.stream.Collectors.*;

public class GLSLFunctionStatement extends GLSLStatement {

    private final List<GLSLFunctionParameterStatement> parameters;
    private final List<GLSLStatement> body;
    private String returnType;
    private String name;

    public GLSLFunctionStatement(
            String returnType,
            String name,
            List<GLSLFunctionParameterStatement> parameters,
            List<GLSLStatement> body
    ) {
        this.returnType = returnType;
        this.name = name;
        this.parameters = new ArrayList<>(parameters);
        this.body = new ArrayList<>(body);
    }

    public GLSLFunctionStatement declare(String returnType, String name) {
        this.returnType = returnType;
        this.name = name;
        return this;
    }

    public GLSLFunctionStatement parameter(GLSLFunctionParameterStatement s) {
        this.parameters.add(s);
        return this;
    }

    public GLSLFunctionStatement parameters(GLSLFunctionParameterStatement... args) {
        for (GLSLFunctionParameterStatement p : args) {
            parameter(p);
        }
        return this;
    }

    public GLSLFunctionStatement body(GLSLStatement... args) {
        for (GLSLStatement s : args) {
            this.body.add(s);
        }
        return this;
    }

    @Override
    public String getKind() {
        return "GLSLFunction";
    }

    @Override
    public String render() {
        return String.format("%s %s (%s) {\n%s\n}", returnType, name,
                parameters.stream().map(p -> p.render()).collect(joining(", ")),
                body.stream().map(e -> e.render() + ";").collect(joining("\n"))
        );
    }

    public static class GLSLFunctionParameterStatement extends GLSLStatement {

        private final String inOut;
        private final String type;
        private final String name;

        public GLSLFunctionParameterStatement(
                String inOut,
                String type,
                String name
        ) {
            this.inOut = inOut;
            this.type = type;
            this.name = name;
        }

        @Override
        public String getKind() {
            return "GLSLFunctionParameter";
        }

        @Override
        public String render() {
            return String.format("%s %s %s", inOut != null ? inOut : "", type, name).trim();
        }
    }

}
