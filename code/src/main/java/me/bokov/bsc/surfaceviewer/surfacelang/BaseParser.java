package me.bokov.bsc.surfaceviewer.surfacelang;

import me.bokov.bsc.surfaceviewer.scene.BaseSceneNode;
import me.bokov.bsc.surfaceviewer.scene.NodeTemplate;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.scene.World;
import org.antlr.v4.runtime.tree.ParseTree;
import org.joml.*;

import java.lang.Math;
import java.util.*;

import static java.util.stream.Collectors.*;

public abstract class BaseParser {

    private final World world;

    protected BaseParser(World world) {
        this.world = world;
    }

    protected World getWorld() {
        return world;
    }

    protected float floatVal(SurfaceLangParser.NumberValueContext ctx) {
        return Float.parseFloat(ctx.getText());
    }

    protected Vector2f vec2Val(SurfaceLangParser.Vec2ValueContext ctx) {

        Vector2f r = new Vector2f(
                Float.parseFloat(ctx.x.getText()),
                Float.parseFloat(ctx.y.getText())
        );

        if (ctx.KW_NORM() != null && !ctx.KW_NORM().getText().isBlank()) {
            r = r.normalize();
        }

        return r;

    }

    protected Vector3f vec3Val(SurfaceLangParser.Vec3ValueContext ctx) {

        Vector3f r = new Vector3f(
                Float.parseFloat(ctx.x.getText()),
                Float.parseFloat(ctx.y.getText()),
                Float.parseFloat(ctx.z.getText())
        );

        if (ctx.KW_NORM() != null && !ctx.KW_NORM().getText().isBlank()) {
            r = r.normalize();
        }

        return r;

    }

    protected Vector4f vec4Val(SurfaceLangParser.Vec4ValueContext ctx) {

        Vector4f r = new Vector4f(
                Float.parseFloat(ctx.x.getText()),
                Float.parseFloat(ctx.y.getText()),
                Float.parseFloat(ctx.z.getText()),
                Float.parseFloat(ctx.w.getText())
        );

        if (ctx.KW_NORM() != null && !ctx.KW_NORM().getText().isBlank()) {
            r = r.normalize();
        }

        return r;

    }

    protected String stringVal(SurfaceLangParser.StringValueContext ctx) {

        final var text = ctx.STRING()
                .getText();
        return text.substring(1, text.length() - 1);

    }

    protected void putPropBySpec(SurfaceLangParser.PropertySpecContext ctx, SceneNode node) {

        final var name = ctx.IDENTIFIER().getText();
        final var prop = NodeTemplate.forName(node.getTemplateName())
                .getProperties()
                .stream().filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);

        if (prop == null) {
            System.err.println("WARN: Invalid property for " + node.getTemplateName() + ": " + name + "!");
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
                        vec2Val(ctx.propertyValue().vec2Value())
                );
                break;
            case "vec3":
                if (ctx.propertyValue()
                        .vec3Value() == null) {
                    throw new IllegalArgumentException(name + " requires a vec3 argument!");
                }
                node.properties().include(
                        prop,
                        vec3Val(ctx.propertyValue().vec3Value())
                );
                break;
            case "vec4":
                if (ctx.propertyValue()
                        .vec4Value() == null) {
                    throw new IllegalArgumentException(name + " requires a vec4 argument!");
                }
                node.properties().include(
                        prop,
                        vec4Val(ctx.propertyValue().vec4Value())
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
                                vec2Val(ctx.propertyValue().mat2Value().col0),
                                vec2Val(ctx.propertyValue().mat2Value().col1)
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
                                vec3Val(ctx.propertyValue().mat3Value().col0),
                                vec3Val(ctx.propertyValue().mat3Value().col1),
                                vec3Val(ctx.propertyValue().mat3Value().col2)
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
                                vec4Val(ctx.propertyValue().mat4Value().col0),
                                vec4Val(ctx.propertyValue().mat4Value().col1),
                                vec4Val(ctx.propertyValue().mat4Value().col2),
                                vec4Val(ctx.propertyValue().mat4Value().col3)
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
            case "string":
                if (ctx.propertyValue().stringValue() == null) {
                    throw new IllegalArgumentException(name + " requires a string argument!");
                }
                node.properties().include(
                        prop,
                        stringVal(ctx.propertyValue().stringValue())
                );
        }

    }

    protected void putPortBySpec(SurfaceLangParser.PortSpecContext spec, SceneNode node) {

        final String name = spec.IDENTIFIER().getText();
        final SceneNode child = expressionToSceneNode(spec.expression());

        node.add(child);
        node.plug(name, child);

    }

    protected void putDefaultPortBySpec(SurfaceLangParser.DefaultPortSpecContext spec, SceneNode node) {

        final var template = NodeTemplate.forName(node.getTemplateName());
        if (template.getPorts().size() != 1) {
            throw new IllegalArgumentException(node.getTemplateName() + " has no default port!");
        }

        final var port = template.getPorts().get(0);
        final String name = port.getName();
        final SceneNode child = expressionToSceneNode(spec.expression());

        node.add(child);
        node.plug(name, child);

    }

    protected List<String> validateSceneNode(SurfaceLangParser.ExpressionContext ctx) {
        return Collections.emptyList();
    }

    protected ParsedComponent<SceneNode> parseSceneNode(SurfaceLangParser.ExpressionContext ctx) {

        final var validationErrors = validateSceneNode(ctx);

        if (validationErrors != null && !validationErrors.isEmpty()) {
            return ParsedComponent.<SceneNode>builder()
                    .errors(validationErrors)
                    .build();
        }

        final var nodeType = ctx.expressionName().IDENTIFIER().getText().toUpperCase();
        NodeTemplate nodeTemplate = NodeTemplate.forName("IDENTITY");

        final var prefab = world.findPrefabByName(nodeType);
        if (prefab.isEmpty()) {
            nodeTemplate = NodeTemplate.forName(nodeType);
        }

        SceneNode node = new BaseSceneNode(
                world.nextId(),
                nodeTemplate.getName()
        );

        if (ctx.expressionAlias() != null && ctx.expressionAlias().IDENTIFIER() != null && ctx.expressionAlias()
                .IDENTIFIER()
                .size() > 0) {
            node.getDisplay().setName(
                    ctx.expressionAlias().IDENTIFIER().stream().map(ParseTree::getText).collect(joining(" "))
            );
        }

        if (prefab.isPresent()) {
            node.setPrefab(prefab.get());
        } else {

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

                if (!nodeTemplate.isSupportsChildren() || nodeTemplate.getPorts().isEmpty()) {
                    throw new IllegalArgumentException(node.getTemplateName() + " does not support ports!");
                }

                SurfaceLangParser.PortMapContext pMapCtx = ctx.expressionPorts().portMap();
                SurfaceLangParser.DefaultPortSpecContext dPortSpecCtx = pMapCtx.defaultPortSpec();

                if (pMapCtx.portSpec() != null) {
                    pMapCtx.portSpec().forEach(
                            spec -> putPortBySpec(spec, node)
                    );
                }

                if (dPortSpecCtx != null) {
                    putDefaultPortBySpec(dPortSpecCtx, node);
                }

            }

            if (ctx.expressionChildren() != null && ctx.expressionChildren().childList() != null) {

                if (!nodeTemplate.isSupportsChildren() || !nodeTemplate.getPorts().isEmpty()) {
                    throw new IllegalArgumentException(node.getTemplateName() + " does not support children list!");
                }

                SurfaceLangParser.ChildListContext cListCtx = ctx.expressionChildren().childList();

                if (cListCtx.expression() != null) {
                    cListCtx.expression().forEach(
                            e -> node.add(expressionToSceneNode(e))
                    );
                }

            }

        }

        if (ctx.expressionTransform() != null) {

            final SurfaceLangParser.ExpressionTransformContext tCtx = ctx.expressionTransform();

            if (tCtx.positionTransform() != null && tCtx.positionTransform().position != null) {

                node.localTransform().applyPosition(
                        vec3Val(tCtx.positionTransform().position)
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
                        vec3Val(tCtx.rotationTransform().vec3Value()),
                        tCtx.rotationTransform().KW_DEGREES() != null
                                ? Float.parseFloat(tCtx.rotationTransform().numberValue().getText())
                                : (float) Math.toDegrees(Float.parseFloat(tCtx.rotationTransform()
                                .numberValue()
                                .getText()))
                );

            }

        }

        return ParsedComponent.<SceneNode>builder()
                .component(node)
                .build();

    }

    protected SceneNode expressionToSceneNode(SurfaceLangParser.ExpressionContext ctx) {
        return parseSceneNode(ctx).getComponent();
    }

}
