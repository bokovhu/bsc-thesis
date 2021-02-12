package me.bokov.bsc.surfaceviewer.surfacelang;

import lombok.Getter;
import me.bokov.bsc.surfaceviewer.scene.*;
import me.bokov.bsc.surfaceviewer.scene.materializer.ConstantMaterial;
import me.bokov.bsc.surfaceviewer.scene.materializer.TriplanarMaterial;
import me.bokov.bsc.surfaceviewer.scene.resource.BaseResourceTexture;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.joml.*;

import java.lang.Math;
import java.util.function.*;

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

    private String stringVal(SurfaceLangParser.StringValueContext ctx) {

        final var text = ctx.STRING()
                .getText();
        return text.substring(1, text.length() - 1);

    }

    private void putPropBySpec(SurfaceLangParser.PropertySpecContext ctx, SceneNode node) {

        final var name = ctx.IDENTIFIER().getText();
        final var prop = node.getTemplate()
                .getProperties()
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

    private void putPortBySpec(SurfaceLangParser.PortSpecContext spec, SceneNode node) {

        final String name = spec.IDENTIFIER().getText();
        final SceneNode child = expressionToSceneNode(spec.expression());

        node.add(child);
        node.plug(name, child);

    }

    private void putDefaultPortBySpec(SurfaceLangParser.DefaultPortSpecContext spec, SceneNode node) {

        if (node.getTemplate().getPorts().size() != 1) {
            throw new IllegalArgumentException(node.getTemplate() + " has no default port!");
        }

        final var port = node.getTemplate().getPorts().get(0);
        final String name = port.getName();
        final SceneNode child = expressionToSceneNode(spec.expression());

        node.add(child);
        node.plug(name, child);

    }
    private SceneNode expressionToSceneNode(SurfaceLangParser.ExpressionContext ctx) {

        final var nodeType = ctx.expressionName().IDENTIFIER().getText().toUpperCase();
        NodeTemplate nodeTemplate = NodeTemplate.forName("IDENTITY");

        final var prefab = world.findPrefabByName(nodeType);
        if(prefab.isEmpty()) {
            nodeTemplate = NodeTemplate.forName(nodeType);
        }

        SceneNode node = new BaseSceneNode(
                lastId++,
                nodeTemplate
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

                if (!node.getTemplate().isSupportsChildren() || node.getTemplate().getPorts().isEmpty()) {
                    throw new IllegalArgumentException(node.getTemplate() + " does not support ports!");
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

                if (!node.getTemplate().isSupportsChildren() || !node.getTemplate().getPorts().isEmpty()) {
                    throw new IllegalArgumentException(node.getTemplate() + " does not support children list!");
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

    private <T> T findMaterialParam(
            SurfaceLangParser.MaterialDefContext def,
            String name,
            Predicate<SurfaceLangParser.MaterialParamContext> filter,
            Function<SurfaceLangParser.MaterialParamContext, T> mapper
    ) {
        return def.materialParamList()
                .materialParam()
                .stream()
                .filter(mp -> mp.materialParamName()
                        .IDENTIFIER()
                        .getText()
                        .equals(name) && filter.test(mp))
                .map(mapper)
                .findFirst()
                .orElseThrow();
    }

    private <T> T findMaterialParamOpt(
            SurfaceLangParser.MaterialDefContext def,
            String name,
            Predicate<SurfaceLangParser.MaterialParamContext> filter,
            Function<SurfaceLangParser.MaterialParamContext, T> mapper
    ) {
        return def.materialParamList()
                .materialParam()
                .stream()
                .filter(mp -> mp.materialParamName()
                        .IDENTIFIER()
                        .getText()
                        .equals(name) && filter.test(mp))
                .map(mapper)
                .findFirst()
                .orElse(null);
    }

    private Materializer parseMaterial(SurfaceLangParser.MaterialContext ctx) {

        final var type = ctx.materialType().IDENTIFIER().getText();

        final SurfaceLangParser.ExpressionContext boundaryContext = findMaterialParam(
                ctx.materialDef(), "boundary",
                mp -> mp.expression() != null,
                SurfaceLangParser.MaterialParamContext::expression
        );

        switch (type.toLowerCase()) {
            case "constant":

                final SurfaceLangParser.Vec3ValueContext constantDiffuseCtx = findMaterialParam(
                        ctx.materialDef(), "diffuse",
                        mp -> mp.vec3Value() != null,
                        SurfaceLangParser.MaterialParamContext::vec3Value
                );
                final SurfaceLangParser.NumberValueContext constantShininessCtx = findMaterialParam(
                        ctx.materialDef(), "shininess",
                        mp -> mp.numberValue() != null,
                        SurfaceLangParser.MaterialParamContext::numberValue
                );

                ConstantMaterial constantMaterial = new ConstantMaterial(
                        world.nextId(),
                        expressionToSceneNode(boundaryContext),
                        vec3Val(constantDiffuseCtx),
                        floatVal(constantShininessCtx)
                );

                return constantMaterial;

            case "triplanar":

                final SurfaceLangParser.Vec3ValueContext triplanarDiffuseCtx = findMaterialParamOpt(
                        ctx.materialDef(), "diffuse",
                        mp -> mp.vec3Value() != null,
                        SurfaceLangParser.MaterialParamContext::vec3Value
                );
                final SurfaceLangParser.StringValueContext triplanarDiffuseMapCtx = findMaterialParamOpt(
                        ctx.materialDef(), "diffuseMap",
                        mp -> mp.stringValue() != null,
                        SurfaceLangParser.MaterialParamContext::stringValue
                );
                final SurfaceLangParser.NumberValueContext triplanarShininessCtx = findMaterialParamOpt(
                        ctx.materialDef(), "shininess",
                        mp -> mp.numberValue() != null,
                        SurfaceLangParser.MaterialParamContext::numberValue
                );
                final SurfaceLangParser.StringValueContext triplanarShininessMapCtx = findMaterialParamOpt(
                        ctx.materialDef(), "shininessMap",
                        mp -> mp.stringValue() != null,
                        SurfaceLangParser.MaterialParamContext::stringValue
                );

                TriplanarMaterial triplanarMaterial = new TriplanarMaterial(
                        world.nextId(),
                        expressionToSceneNode(boundaryContext),
                        triplanarDiffuseMapCtx != null ? stringVal(triplanarDiffuseMapCtx) : null,
                        triplanarDiffuseCtx != null ? vec3Val(triplanarDiffuseCtx) : null,
                        triplanarShininessMapCtx != null ? stringVal(triplanarShininessMapCtx) : null,
                        triplanarShininessCtx != null ? floatVal(triplanarShininessCtx) : null
                );

                SurfaceLangParser.NumberValueContext triplanarScaleCtx = findMaterialParamOpt(
                        ctx.materialDef(), "textureScale",
                        mp -> mp.numberValue() != null,
                        SurfaceLangParser.MaterialParamContext::numberValue
                );

                if(triplanarScaleCtx != null) {
                    triplanarMaterial.scale(floatVal(triplanarScaleCtx));
                }

                return triplanarMaterial;

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

    private Prefab parsePrefab(SurfaceLangParser.PrefabContext ctx) {

        final var prefab = new BasePrefab(world.nextId());
        prefab.setName(ctx.prefabName().IDENTIFIER().getText());
        prefab.setNode(expressionToSceneNode(ctx.expression()));

        return prefab;

    }

    private ResourceTexture parseResourceTexture(SurfaceLangParser.ResourceTextureContext ctx) {

        final String name = ctx.resourceTextureName().IDENTIFIER().getText();
        final String location = stringVal(ctx.resourceTextureLocation().stringValue());

        return new BaseResourceTexture(
                world.nextId(),
                name, location
        );

    }

    @Override
    public void exitWorld(SurfaceLangParser.WorldContext ctx) {
        super.exitWorld(ctx);

        world = new BaseWorld();
        world.getLightSources().clear();
        ctx.resourceTexture().forEach(
                tex -> world.add(parseResourceTexture(tex))
        );
        ctx.prefab().forEach(
                pref -> world.add(parsePrefab(pref))
        );
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
                            new BaseSceneNode(world.nextId(), NodeTemplate.forName("EVERYWHERE")),
                            new Vector3f(1f),
                            80.0f
                    )
            );
        }

    }
}
