package me.bokov.bsc.surfaceviewer.glsl;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class GLSLStructStatement extends GLSLStatement {

    private final String name;
    private final List<GLSLStructMemberStatement> members;

    public GLSLStructStatement(
            String name,
            List<GLSLStructMemberStatement> members
    ) {
        this.name = name;
        this.members = new ArrayList<>(members);
    }

    public GLSLStructStatement members(GLSLStructMemberStatement... args) {
        for (GLSLStructMemberStatement m : args) {
            this.members.add(m);
        }
        return this;
    }

    @Override
    public String getKind() {
        return "GLSLStruct";
    }

    @Override
    public String render() {
        return String.format("struct %s {\n%s\n};", name,
                members.stream().map(m -> m.render()).collect(joining("\n"))
        );
    }

    public static class GLSLStructMemberStatement extends GLSLStatement {

        private final String type;
        private final String name;

        public GLSLStructMemberStatement(
                String type,
                String name
        ) {
            this.type = type;
            this.name = name;
        }

        @Override
        public String getKind() {
            return "GLSLStructMember";
        }

        @Override
        public String render() {
            return String.format("%s %s;", type, name);
        }
    }

}
