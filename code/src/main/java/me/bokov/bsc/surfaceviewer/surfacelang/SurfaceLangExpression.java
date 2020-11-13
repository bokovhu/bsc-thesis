package me.bokov.bsc.surfaceviewer.surfacelang;

import lombok.Getter;
import me.bokov.bsc.surfaceviewer.scene.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.joml.*;

import java.lang.Math;
import java.util.*;

import static java.util.stream.Collectors.*;

public class SurfaceLangExpression extends SurfaceLangBaseListener {

    @Getter
    private String code = null;

    @Getter
    private World world = new BaseWorld();

    private int lastId = world.nextId();

    private void putPropBySpec(SurfaceLangParser.PropertySpecContext ctx, SceneNode node) {

        final var name = ctx.IDENTIFIER().getText();
        final var prop = node.getTemplate()
                .properties
                .stream().filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);

        if (prop == null) {
            System.err.println("WARN: Invalid property for " + node.getTemplate() + ": " + name + "!");
            return;
        }

        switch (prop.getType()) {
            case "float":
                if (ctx.propertyValue()
                        .numberValue() == null) {
                    throw new IllegalArgumentException(name + " requires a number argument!");
                }
                node.properties().include(prop, Float.parseFloat(ctx.propertyValue().numberValue().getText()));
                break;
            case "int":
                if (ctx.propertyValue()
                        .numberValue() == null) {
                    throw new IllegalArgumentException(name + " requires a number argument!");
                }
                node.properties().include(prop, Integer.parseInt(ctx.propertyValue().numberValue().getText()));
                break;
            case "vec2":
                if (ctx.propertyValue()
                        .vec2Value() == null) {
                    throw new IllegalArgumentException(name + " requires a vec2 argument!");
                }
                node.properties().include(
                        prop,
                        new Vector2f(
                                Float.parseFloat(ctx.propertyValue().vec2Value().x.getText()),
                                Float.parseFloat(ctx.propertyValue().vec2Value().y.getText())
                        )
                );
                break;
            case "vec3":
                if (ctx.propertyValue()
                        .vec3Value() == null) {
                    throw new IllegalArgumentException(name + " requires a vec3 argument!");
                }
                node.properties().include(
                        prop,
                        new Vector3f(
                                Float.parseFloat(ctx.propertyValue().vec3Value().x.getText()),
                                Float.parseFloat(ctx.propertyValue().vec3Value().y.getText()),
                                Float.parseFloat(ctx.propertyValue().vec3Value().z.getText())
                        )
                );
                break;
            case "vec4":
                if (ctx.propertyValue()
                        .vec4Value() == null) {
                    throw new IllegalArgumentException(name + " requires a vec4 argument!");
                }
                node.properties().include(
                        prop,
                        new Vector4f(
                                Float.parseFloat(ctx.propertyValue().vec4Value().x.getText()),
                                Float.parseFloat(ctx.propertyValue().vec4Value().y.getText()),
                                Float.parseFloat(ctx.propertyValue().vec4Value().z.getText()),
                                Float.parseFloat(ctx.propertyValue().vec4Value().w.getText())
                        )
                );
                break;
            case "mat2":
                if (ctx.propertyValue()
                        .mat2Value() == null) {
                    throw new IllegalArgumentException(name + " requires a mat2 argument!");
                }
                node.properties().include(
                        prop,
                        new Matrix2f(
                                new Vector2f(
                                        Float.parseFloat(ctx.propertyValue().mat2Value().col0.x.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat2Value().col0.y.getText())
                                ),
                                new Vector2f(
                                        Float.parseFloat(ctx.propertyValue().mat2Value().col1.x.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat2Value().col1.y.getText())
                                )
                        )
                );
                break;
            case "mat3":
                if (ctx.propertyValue()
                        .mat3Value() == null) {
                    throw new IllegalArgumentException(name + " requires a mat3 argument!");
                }
                node.properties().include(
                        prop,
                        new Matrix3f(
                                new Vector3f(
                                        Float.parseFloat(ctx.propertyValue().mat3Value().col0.x.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat3Value().col0.y.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat3Value().col0.z.getText())
                                ),
                                new Vector3f(
                                        Float.parseFloat(ctx.propertyValue().mat3Value().col1.x.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat3Value().col1.y.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat3Value().col1.z.getText())
                                ),
                                new Vector3f(
                                        Float.parseFloat(ctx.propertyValue().mat3Value().col2.x.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat3Value().col2.y.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat3Value().col2.z.getText())
                                )
                        )
                );
                break;
            case "mat4":
                if (ctx.propertyValue()
                        .mat4Value() == null) {
                    throw new IllegalArgumentException(name + " requires a mat4 argument!");
                }
                node.properties().include(
                        prop,
                        new Matrix4f(
                                new Vector4f(
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col0.x.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col0.y.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col0.z.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col0.w.getText())
                                ),
                                new Vector4f(
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col1.x.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col1.y.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col1.z.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col1.w.getText())
                                ),
                                new Vector4f(
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col2.x.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col2.y.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col2.z.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col2.w.getText())
                                ),
                                new Vector4f(
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col3.x.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col3.y.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col3.z.getText()),
                                        Float.parseFloat(ctx.propertyValue().mat4Value().col3.w.getText())
                                )
                        )
                );
                break;
            case "bool":
                if (ctx.propertyValue().boolValue() == null) {
                    throw new IllegalArgumentException(name + " requires a bool argument!");
                }
                node.properties().include(
                        prop,
                        ctx.propertyValue().boolValue().KW_TRUE() != null
                );
                break;
        }

    }

    private void putPortBySpec(SurfaceLangParser.PortSpecContext spec, SceneNode node) {

        final String name = spec.IDENTIFIER().getText();
        final SceneNode child = expressionToSceneNode(spec.expression());

        node.add(child);
        node.plug(name, child);

    }

    private SceneNode expressionToSceneNode(SurfaceLangParser.ExpressionContext ctx) {

        SceneNode node = new BaseSceneNode(
                lastId++,
                NodeTemplate.valueOf(ctx.expressionName().IDENTIFIER().getText().toUpperCase())
        );

        if (ctx.expressionAlias() != null && ctx.expressionAlias().IDENTIFIER() != null && ctx.expressionAlias()
                .IDENTIFIER()
                .size() > 0) {
            node.getDisplay().setName(
                    ctx.expressionAlias().IDENTIFIER().stream().map(ParseTree::getText).collect(joining(" "))
            );
        }

        if (ctx.expressionProperties() != null && ctx.expressionProperties().propertyMap() != null) {

            SurfaceLangParser.PropertyMapContext pMapCtx = ctx.expressionProperties().propertyMap();

            if (pMapCtx.propertySpec() != null) {
                pMapCtx.propertySpec()
                        .forEach(
                                spec -> putPropBySpec(spec, node)
                        );
            }

        }

        if (ctx.expressionPorts() != null && ctx.expressionPorts().portMap() != null) {

            if (!node.getTemplate().supportsChildren || node.getTemplate().ports.isEmpty()) {
                throw new IllegalArgumentException(node.getTemplate() + " does not support ports!");
            }

            SurfaceLangParser.PortMapContext pMapCtx = ctx.expressionPorts().portMap();

            if (pMapCtx.portSpec() != null) {
                pMapCtx.portSpec().forEach(
                        spec -> putPortBySpec(spec, node)
                );
            }

        }

        if (ctx.expressionChildren() != null && ctx.expressionChildren().childList() != null) {

            if (!node.getTemplate().supportsChildren || !node.getTemplate().ports.isEmpty()) {
                throw new IllegalArgumentException(node.getTemplate() + " does not support children list!");
            }

            SurfaceLangParser.ChildListContext cListCtx = ctx.expressionChildren().childList();

            if (cListCtx.expression() != null) {
                cListCtx.expression().forEach(
                        e -> node.add(expressionToSceneNode(e))
                );
            }

        }

        if (ctx.expressionTransform() != null) {

            final SurfaceLangParser.ExpressionTransformContext tCtx = ctx.expressionTransform();

            if (tCtx.positionTransform() != null && tCtx.positionTransform().position != null) {

                node.localTransform().applyPosition(
                        new Vector3f(
                                Float.parseFloat(tCtx.positionTransform().position.x.getText()),
                                Float.parseFloat(tCtx.positionTransform().position.y.getText()),
                                Float.parseFloat(tCtx.positionTransform().position.z.getText())
                        )
                );

            }

            if (tCtx.scaleTransform() != null && tCtx.scaleTransform().scale != null) {

                node.localTransform().applyScale(
                        Float.parseFloat(tCtx.scaleTransform().scale.getText())
                );

            }

            if (tCtx.rotationTransform() != null && tCtx.rotationTransform()
                    .vec3Value() != null && tCtx.rotationTransform().numberValue() != null) {

                node.localTransform().applyRotation(
                        new Vector3f(
                                Float.parseFloat(tCtx.rotationTransform().vec3Value().x.getText()),
                                Float.parseFloat(tCtx.rotationTransform().vec3Value().y.getText()),
                                Float.parseFloat(tCtx.rotationTransform().vec3Value().z.getText())
                        ),
                        tCtx.rotationTransform().KW_DEGREES() != null
                                ? Float.parseFloat(tCtx.rotationTransform().numberValue().getText())
                                : (float) Math.toDegrees(Float.parseFloat(tCtx.rotationTransform()
                                .numberValue()
                                .getText()))
                );

            }

        }

        return node;

    }

    private String formatPropertyValue(NodeTemplate.Property p, NodeProperties props) {
        switch (p.getType()) {
            case "float":
                return String.format(
                        Locale.ENGLISH,
                        "%.4f",
                        props.getFloat(p.getName(), ((Number) p.getDefaultValue()).floatValue())
                );
            case "int":
                return String.format(
                        Locale.ENGLISH,
                        "%d",
                        props.getInt(p.getName(), ((Number) p.getDefaultValue()).intValue())
                );
            case "bool":
                return props.getBool(p.getName(), Boolean.TRUE.equals(p.getDefaultValue())) ? "true" : "false";
            case "vec2":
                Vector2f v2 = props.getVec2(p.getName(), (Vector2f) p.getDefaultValue());
                return String.format(
                        Locale.ENGLISH,
                        "(%.4f, %.4f)",
                        v2.x, v2.y
                );
            case "vec3":
                Vector3f v3 = props.getVec3(p.getName(), (Vector3f) p.getDefaultValue());
                return String.format(
                        Locale.ENGLISH,
                        "(%.4f, %.4f, %.4f)",
                        v3.x, v3.y, v3.z
                );
            case "vec4":
                Vector4f v4 = props.getVec4(p.getName(), (Vector4f) p.getDefaultValue());
                return String.format(
                        Locale.ENGLISH,
                        "(%.4f, %.4f, %.4f, %.4f)",
                        v4.x, v4.y, v4.z, v4.w
                );
            case "mat2":
                Matrix2f m2 = props.getMat2(p.getName(), (Matrix2f) p.getDefaultValue());
                return String.format(
                        Locale.ENGLISH,
                        "((%.4f, %.4f), (%.4f, %.4f))",
                        m2.m00, m2.m01,
                        m2.m10, m2.m11
                );

            case "mat3":
                Matrix3f m3 = props.getMat3(p.getName(), (Matrix3f) p.getDefaultValue());
                return String.format(
                        Locale.ENGLISH,
                        "((%.4f, %.4f, %.4f), (%.4f, %.4f, %.4f), (%.4f, %.4f, %.4f))",
                        m3.m00, m3.m01, m3.m02,
                        m3.m10, m3.m11, m3.m12,
                        m3.m20, m3.m21, m3.m22
                );
            case "mat4":
                Matrix4f m4 = props.getMat4(p.getName(), (Matrix4f) p.getDefaultValue());
                return String.format(
                        Locale.ENGLISH,
                        "((%.4f, %.4f, %.4f, %.4f), (%.4f, %.4f, %.4f, %.4f), (%.4f, %.4f, %.4f, %.4f), (%.4f, %.4f, %.4f, %.4f))",
                        m4.m00(),
                        m4.m01(),
                        m4.m02(),
                        m4.m03(),
                        m4.m10(),
                        m4.m11(),
                        m4.m12(),
                        m4.m13(),
                        m4.m20(),
                        m4.m21(),
                        m4.m22(),
                        m4.m23(),
                        m4.m30(),
                        m4.m31(),
                        m4.m32(),
                        m4.m33()
                );
            default:
                return null;
        }
    }

    private String formatNodeTransform(SceneNode node, int indentation) {
        return String.format(
                Locale.ENGLISH,
                "AT POSITION (%.4f, %.4f, %.4f)\n" + "    ".repeat(indentation)
                        + "SCALE %.4f\n" + "    ".repeat(indentation)
                        + "ROTATE AROUND (%.4f, %.4f, %.4f) BY %.4f DEGREES",
                node.localTransform().position().x,
                node.localTransform().position().y,
                node.localTransform().position().z,

                node.localTransform().scale(),

                node.localTransform().rotationAxis().x,
                node.localTransform().rotationAxis().y,
                node.localTransform().rotationAxis().z,

                node.localTransform().rotationAngle()
        ).trim();
    }

    private String formatNodeProperties(NodeProperties props, int indentation) {

        if (props.getIncludedProperties().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        List<NodeTemplate.Property> propertyList = new ArrayList<>(props.getIncludedProperties());
        propertyList.sort(Comparator.comparing(NodeTemplate.Property::getName));

        sb.append("(");
        sb.append(
                propertyList.stream().map(
                        p -> p.getName() + ": " + formatPropertyValue(p, props)
                ).collect(joining(", "))
        );
        sb.append(")");

        return sb.toString().trim();

    }

    private String formatNodePorts(Map<String, SceneNode> portMap, int indentation) {

        List<String> portKeyList = new ArrayList<>(portMap.keySet());
        portKeyList.sort(Comparator.naturalOrder());

        return portKeyList.stream()
                .map(
                        portKey -> "    ".repeat(indentation) + portKey + ": " + formatNode(portMap.get(portKey), indentation)
                ).collect(joining(",\n")).stripTrailing();

    }

    private String formatNodeChildren(List<SceneNode> children, int indentation) {

        return children.stream()
                .map(
                        node -> "    ".repeat(indentation) + formatNode(node, indentation)
                ).collect(joining(",\n"));

    }

    private String formatNode(SceneNode node, int indentation) {

        StringBuilder sb = new StringBuilder();

        sb.append(node.getTemplate())
                .append(" ");

        if (!node.getDisplay().getName().isBlank()) {
            sb.append("\"")
                    .append(node.getDisplay().getName())
                    .append("\" ");
        }

        sb.append("\n").append("    ".repeat(indentation))
                .append(formatNodeTransform(node, indentation));
        sb.append(formatNodeProperties(node.properties(), indentation));

        if (!node.pluggedPorts().keySet().isEmpty()) {

            sb.append("{\n")
                    .append(formatNodePorts(node.pluggedPorts(), indentation + 1))
                    .append("\n")
                    .append("    ".repeat(indentation))
                    .append("}");

        }

        if (!node.children().isEmpty() && node.pluggedPorts().isEmpty()) {

            sb.append("[\n")
                    .append(formatNodeChildren(node.children(), indentation + 1))
                    .append("\n")
                    .append("    ".repeat(indentation))
                    .append("]");

        }

        return sb.toString().trim();

    }

    public void parse(String src) {

        this.code = src;

        try {

            SurfaceLangLexer lexer = new SurfaceLangLexer(
                    CharStreams.fromString(code)
            );

            SurfaceLangParser parser = new SurfaceLangParser(
                    new CommonTokenStream(lexer)
            );

            parser.addParseListener(this);

            parser.world();

        } catch (Exception e) {
            throw new IllegalArgumentException("Exception while parsing expression: \n" + src + "\n\n", e);
        }

    }

    public void format(World world) {

        this.world = world;

        this.code = this.world.roots()
                .stream().map(node -> formatNode(node, 0))
                .collect(joining("\n"));

    }


    @Override
    public void exitWorld(SurfaceLangParser.WorldContext ctx) {
        super.exitWorld(ctx);

        world = new BaseWorld();
        ctx.expression().forEach(
                expr -> world.add(expressionToSceneNode(expr))
        );

    }
}
