package me.bokov.bsc.surfaceviewer.glsl;

import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import org.joml.*;

import java.util.*;

public class GLSLPoet {

    private static Vector4f tmpMC0 = new Vector4f();
    private static Vector4f tmpMC1 = new Vector4f();
    private static Vector4f tmpMC2 = new Vector4f();
    private static Vector4f tmpMC3 = new Vector4f();

    public static GLSLStatement abs(GLSLStatement arg) {
        return new GLSLFunctionCallStatement(
                "abs",
                List.of(arg)
        );
    }

    public static GLSLStatement pow(GLSLStatement base, GLSLStatement power) {
        return new GLSLFunctionCallStatement(
                "pow",
                List.of(base, power)
        );
    }

    public static GLSLStatement dot(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLFunctionCallStatement(
                "dot",
                List.of(v1, v2)
        );
    }

    public static GLSLStatement mod(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLFunctionCallStatement(
                "mod",
                List.of(v1, v2)
        );
    }

    public static GLSLStatement min(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLFunctionCallStatement(
                "min",
                List.of(v1, v2)
        );
    }

    public static GLSLStatement min(List<GLSLStatement> list) {
        if (list.size() == 0) { return null; }
        if (list.size() == 1) { return list.get(0); }
        if (list.size() == 2) { return min(list.get(0), list.get(1)); }
        return min(list.get(0), min(list.subList(1, list.size())));
    }

    public static GLSLStatement max(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLFunctionCallStatement(
                "max",
                List.of(v1, v2)
        );
    }

    public static GLSLStatement clamp(GLSLStatement v, GLSLStatement min, GLSLStatement max) {
        return new GLSLFunctionCallStatement(
                "clamp",
                List.of(v, min, max)
        );
    }

    public static GLSLStatement exp(GLSLStatement v) {
        return new GLSLFunctionCallStatement(
                "exp",
                List.of(v)
        );
    }

    public static GLSLStatement inverse(GLSLStatement v) {
        return new GLSLFunctionCallStatement(
                "inverse",
                List.of(v)
        );
    }

    public static GLSLStatement transpose(GLSLStatement v) {
        return new GLSLFunctionCallStatement(
                "transpose",
                List.of(v)
        );
    }

    public static GLSLStatement sin(GLSLStatement v) {
        return new GLSLFunctionCallStatement("sin", List.of(v));
    }

    public static GLSLStatement cos(GLSLStatement v) {
        return new GLSLFunctionCallStatement("cos", List.of(v));
    }

    public static GLSLStatement tan(GLSLStatement v) {
        return new GLSLFunctionCallStatement("tan", List.of(v));
    }

    public static GLSLStatement length(GLSLStatement v) {
        return new GLSLFunctionCallStatement("length", List.of(v));
    }

    public static GLSLStatement cross(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLFunctionCallStatement("cross", List.of(v1, v2));
    }

    public static GLSLStatement normalize(GLSLStatement v) {
        return new GLSLFunctionCallStatement("normalize", List.of(v));
    }

    public static GLSLStatement mix(GLSLStatement v1, GLSLStatement v2, GLSLStatement alpha) {
        return new GLSLFunctionCallStatement("mix", List.of(v1, v2, alpha));
    }

    public static GLSLStatement opPlus(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLBinaryExpressionStatement(v1, v2, "+");
    }

    public static GLSLStatement opMinus(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLBinaryExpressionStatement(v1, v2, "-");
    }

    public static GLSLStatement opMul(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLBinaryExpressionStatement(v1, v2, "*");
    }

    public static GLSLStatement opDiv(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLBinaryExpressionStatement(v1, v2, "/");
    }

    public static GLSLStatement opAssign(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLBinaryExpressionStatement(v1, v2, "=");
    }

    public static GLSLStatement cmpLt(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLBinaryExpressionStatement(v1, v2, "<");
    }

    public static GLSLStatement cmpLe(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLBinaryExpressionStatement(v1, v2, "<=");
    }

    public static GLSLStatement cmpGt(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLBinaryExpressionStatement(v1, v2, ">");
    }

    public static GLSLStatement cmpGe(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLBinaryExpressionStatement(v1, v2, ">=");
    }

    public static GLSLStatement cmpEq(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLBinaryExpressionStatement(v1, v2, "==");
    }

    public static GLSLStatement cmpNe(GLSLStatement v1, GLSLStatement v2) {
        return new GLSLBinaryExpressionStatement(v1, v2, "!=");
    }

    public static GLSLStatement ternary(
            GLSLStatement test, GLSLStatement vTrue,
            GLSLStatement vFalse
    ) {
        return new GLSLBinaryExpressionStatement(
                test,
                new GLSLBinaryExpressionStatement(
                        vTrue,
                        vFalse,
                        ":"
                ),
                "?"
        );
    }

    public static GLSLStatement literal(float v) {
        return new GLSLRawStatement(String.format(Locale.ENGLISH, "%.4f", v));
    }

    public static GLSLStatement literal(int i) {
        return new GLSLRawStatement(String.format("%d", i));
    }

    public static GLSLStatement literal(double v) {
        return new GLSLRawStatement(String.format(Locale.ENGLISH, "%.4f", v));
    }

    public static GLSLStatement literal(boolean b) {
        return b ? new GLSLRawStatement("true") : new GLSLRawStatement("false");
    }

    public static GLSLStatement vec2(float x, float y) {
        return new GLSLFunctionCallStatement(
                "vec2",
                List.of(
                        literal(x),
                        literal(y)
                )
        );
    }

    public static GLSLStatement vec2(GLSLStatement x, GLSLStatement y) {
        return new GLSLFunctionCallStatement(
                "vec2",
                List.of(x, y)
        );
    }

    public static GLSLStatement vec2(Vector2f v) {
        return vec2(v.x, v.y);
    }

    public static GLSLStatement vec3(float x, float y, float z) {
        return new GLSLFunctionCallStatement(
                "vec3",
                List.of(
                        literal(x),
                        literal(y),
                        literal(z)
                )
        );
    }

    public static GLSLStatement vec3(GLSLStatement x, GLSLStatement y, GLSLStatement z) {
        return new GLSLFunctionCallStatement(
                "vec3",
                List.of(x, y, z)
        );
    }

    public static GLSLStatement vec3(Vector3f v) {
        return vec3(v.x, v.y, v.z);
    }

    public static GLSLStatement vec4(float x, float y, float z, float w) {
        return new GLSLFunctionCallStatement(
                "vec4",
                List.of(
                        literal(x),
                        literal(y),
                        literal(z),
                        literal(w)
                )
        );
    }

    public static GLSLStatement vec4(
            GLSLStatement x, GLSLStatement y, GLSLStatement z,
            GLSLStatement w
    ) {
        return new GLSLFunctionCallStatement(
                "vec4",
                List.of(x, y, z, w)
        );
    }

    public static GLSLStatement vec4(Vector4f v) {
        return vec4(v.x, v.y, v.y, v.w);
    }

    public static GLSLStatement vec4(Quaternionf q) {
        return vec4(q.x, q.y, q.z, q.w);
    }

    public static GLSLStatement mat4(
            GLSLStatement col1,
            GLSLStatement col2,
            GLSLStatement col3,
            GLSLStatement col4
    ) {
        return new GLSLFunctionCallStatement(
                "mat4",
                List.of(col1, col2, col3, col4)
        );
    }

    public static GLSLStatement mat4(
            Vector4f col1,
            Vector4f col2,
            Vector4f col3,
            Vector4f col4
    ) {
        return mat4(
                vec4(col1),
                vec4(col2),
                vec4(col3),
                vec4(col4)
        );
    }

    public static GLSLStatement mat4(Matrix4f m) {
        return
                new GLSLFunctionCallStatement(
                        "mat4",
                        List.of(
                                vec4(tmpMC0.set(m.m00(), m.m01(), m.m02(), m.m03())),
                                vec4(tmpMC1.set(m.m10(), m.m11(), m.m12(), m.m13())),
                                vec4(tmpMC2.set(m.m20(), m.m21(), m.m22(), m.m23())),
                                vec4(tmpMC3.set(m.m30(), m.m31(), m.m32(), m.m33()))
                        )
                );
    }

    public static GLSLVariableDeclarationStatement resultVar(
            GPUContext context,
            GLSLStatement value
    ) {
        return new GLSLVariableDeclarationStatement(
                "float",
                context.getResult(),
                value
        );
    }

    public static GLSLVariableDeclarationStatement var(
            String type, String name,
            GLSLStatement value
    ) {
        return new GLSLVariableDeclarationStatement(
                type,
                name,
                value
        );
    }

    public static GLSLMemberStatement ref(String... names) {
        return new GLSLMemberStatement(Arrays.asList(names));
    }

    public static GLSLFunctionCallStatement fn(String fnName, GLSLStatement... args) {
        return new GLSLFunctionCallStatement(fnName, Arrays.asList(args));
    }

    public static GLSLBlockStatement block(GLSLStatement... body) {
        return new GLSLBlockStatement(Arrays.asList(body));
    }

    public static GLSLParenthesesStatement paren(GLSLStatement statement) {
        return new GLSLParenthesesStatement(statement);
    }

    public static GLSLStatement combine(List<GLSLStatement> statements, String op) {
        if (statements.size() == 1) {
            return statements.get(0);
        }
        if (statements.size() == 2) {
            return new GLSLBinaryExpressionStatement(statements.get(0), statements.get(1), op);
        }
        List<GLSLStatement> rest = new ArrayList<>(statements);
        GLSLStatement first = rest.remove(0);
        return new GLSLBinaryExpressionStatement(
                first,
                combine(rest, op),
                op
        );
    }

}
