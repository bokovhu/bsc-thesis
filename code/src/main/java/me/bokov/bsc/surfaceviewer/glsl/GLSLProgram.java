package me.bokov.bsc.surfaceviewer.glsl;

import me.bokov.bsc.surfaceviewer.util.ResourceUtil;

import java.util.*;

import static java.util.stream.Collectors.*;

public class GLSLProgram extends GLSLStatement {

    private final List<GLSLStatement> statements;
    private String glslVersion;

    public GLSLProgram(
            String glslVersion,
            List<GLSLStatement> statements
    ) {
        this.glslVersion = glslVersion;
        this.statements = new ArrayList<>(statements);
    }

    public GLSLProgram(String glslVersion) {
        this(glslVersion, Collections.emptyList());
    }

    public GLSLProgram() {
        this("460", Collections.emptyList());
    }

    @Override
    public String getKind() {
        return "GLSLProgram";
    }

    @Override
    public String render() {
        return String.format("#version %s\n%s", glslVersion,
                statements.stream().map(s -> s.render()).collect(joining("\n"))
        );
    }

    public GLSLProgram add(GLSLStatement... args) {
        for (GLSLStatement statement : args) {
            this.statements.add(statement);
        }
        return this;
    }

    public GLSLRawStatement raw(String code) {
        return new GLSLRawStatement(code);
    }

    public GLSLProgram include(String resourceName) {
        return add(raw(ResourceUtil.readResource(resourceName)));
    }

    public GLSLProgram init(String glslVersion) {
        this.glslVersion = glslVersion;
        return this;
    }
}
