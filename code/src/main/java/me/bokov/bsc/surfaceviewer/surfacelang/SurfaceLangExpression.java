package me.bokov.bsc.surfaceviewer.surfacelang;

import lombok.Getter;
import me.bokov.bsc.surfaceviewer.scene.*;
import me.bokov.bsc.surfaceviewer.scene.materializer.ConstantMaterial;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.threed.Everywhere;
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

    private float floatVal(SurfaceLangParser.NumberValueContext ctx) {
        return Float.parseFloat(ctx.getText());
    }

    private Vector2f vec2Val(SurfaceLangParser.Vec2ValueContext ctx) {

        Vector2f r = new Vector2f(
                Float.parseFloat(ctx.x.getText()),
                Float.parseFloat(ctx.y.getText())
        );

        if (ctx.KW_NORM() != null && !ctx.KW_NORM().getText().isBlank()) {
            r = r.normalize();
        }

        return r;

    }

    private Vector3f vec3Val(SurfaceLangParser.Vec3ValueContext ctx) {

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

    private Vector4f vec4Val(SurfaceLangParser.Vec4ValueContext ctx) {

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

        final var nodeType = ctx.expressionName().IDENTIFIER().getText().toUpperCase();

        SceneNode node = new BaseSceneNode(
                lastId++,
                NodeTemplate.valueOf(nodeType)
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

    private LightSource lightToLightSource(SurfaceLangParser.LightContext ctx) {

        final var type = ctx.lightType().IDENTIFIER().getText();

        final SurfaceLangParser.Vec3ValueContext energyCtx = ctx.lightDef()
                .lightParamList()
                .lightParam()
                .stream()
                .filter(lp -> lp.lightParamName().IDENTIFIER().getText().equals("energy") && lp.vec3Value() != null)
                .map(SurfaceLangParser.LightParamContext::vec3Value)
                .findFirst()
                .orElseThrow();

        switch (type.toLowerCase()) {
            case "ambient":

                AmbientLight ambientLight = new AmbientLight(world.nextId());

                ambientLight.setEnergy(vec3Val(energyCtx));
                return ambientLight;

            case "directional":

                DirectionalLight directionalLight = new DirectionalLight(world.nextId());

                directionalLight.setEnergy(vec3Val(energyCtx));

                final SurfaceLangParser.Vec3ValueContext directionCtx = ctx.lightDef()
                        .lightParamList()
                        .lightParam()
                        .stream()
                        .filter(lp -> lp.lightParamName()
                                .IDENTIFIER()
                                .getText()
                                .equals("direction") && lp.vec3Value() != null)
                        .map(SurfaceLangParser.LightParamContext::vec3Value)
                        .findFirst()
                        .orElseThrow();

                directionalLight.dir(vec3Val(directionCtx));

                return directionalLight;
            default:
                throw new UnsupportedOperationException("Light type not supported: " + type);
        }

    }

    private Materializer parseMaterial(SurfaceLangParser.MaterialContext ctx) {

        final var type = ctx.materialType().IDENTIFIER().getText();

        final SurfaceLangParser.ExpressionContext boundaryContext = ctx.materialDef()
                .materialParamList()
                .materialParam()
                .stream()
                .filter(mp -> mp.expression() != null && mp.materialParamName()
                        .IDENTIFIER()
                        .getText()
                        .equals("boundary"))
                .map(SurfaceLangParser.MaterialParamContext::expression)
                .findFirst()
                .orElseThrow();

        switch (type.toLowerCase()) {
            case "constant":

                final SurfaceLangParser.Vec3ValueContext diffuseCtx = ctx.materialDef()
                        .materialParamList()
                        .materialParam()
                        .stream()
                        .filter(mp -> mp.materialParamName()
                                .IDENTIFIER()
                                .getText()
                                .equals("diffuse") && mp.vec3Value() != null)
                        .map(SurfaceLangParser.MaterialParamContext::vec3Value)
                        .findFirst()
                        .orElseThrow();
                final SurfaceLangParser.NumberValueContext shininessCtx = ctx.materialDef()
                        .materialParamList()
                        .materialParam()
                        .stream()
                        .filter(mp -> mp.materialParamName()
                                .IDENTIFIER()
                                .getText()
                                .equals("shininess") && mp.numberValue() != null)
                        .map(SurfaceLangParser.MaterialParamContext::numberValue)
                        .findFirst()
                        .orElseThrow();

                ConstantMaterial constantMaterial = new ConstantMaterial(
                        world.nextId(),
                        expressionToSceneNode(boundaryContext),
                        vec3Val(diffuseCtx),
                        floatVal(shininessCtx)
                );

                return constantMaterial;

            default:
                throw new UnsupportedOperationException("Materializer type not supported: " + type);
        }

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

    @Override
    public void exitWorld(SurfaceLangParser.WorldContext ctx) {
        super.exitWorld(ctx);

        world = new BaseWorld();
        world.getLightSources().clear();
        ctx.expression().forEach(
                expr -> world.add(expressionToSceneNode(expr))
        );
        ctx.light().forEach(
                l -> world.add(lightToLightSource(l))
        );
        ctx.material().forEach(
                m -> world.add(parseMaterial(m))
        );

        if (world.getLightSources().isEmpty()) {
            world.add(
                    new AmbientLight(world.nextId()).setEnergy(0.2f, 0.2f, 0.2f),
                    new DirectionalLight(world.nextId()).dir(1.5f, 2.5f, -1.5f).setEnergy(1f, 1f, 1f)
            );
        }

        if (world.getMaterializers().isEmpty()) {
            world.add(
                    new ConstantMaterial(
                            world.nextId(),
                            new BaseSceneNode(world.nextId(), NodeTemplate.EVERYWHERE),
                            new Vector3f(1f),
                            80.0f
                    )
            );
        }

    }
}
