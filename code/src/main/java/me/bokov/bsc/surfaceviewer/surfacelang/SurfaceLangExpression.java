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
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

public class SurfaceLangExpression extends SurfaceLangBaseListener {

    @Getter
    private String code = null;

    @Getter
    private World world = new BaseWorld();

    private static final Map<String, MaterialParser.MaterialParserFactory> MATERIAL_PARSER_FACTORIES = new HashMap<>(
            Map.of(
                    "constant", w -> new ConstantMaterialParser(w),
                    "triplanar", w -> new TriplanarMaterialParser(w)
            )
    );
    private static final Map<String, LightSourceParser.LightSourceParserFactory> LIGHT_SOURCE_PARSER_FACTORIES = new HashMap<>(
            Map.of(
                    "ambient", w -> new AmbientLightParser(w),
                    "directional", w -> new DirectionalLightParser(w)
            )
    );

    public static synchronized void registerMaterialParserFactory(String type, MaterialParser.MaterialParserFactory factory) {
        MATERIAL_PARSER_FACTORIES.put(type, factory);
    }

    private static MaterialParser.MaterialParserFactory materialParserFactoryForType(String type) {
        return MATERIAL_PARSER_FACTORIES.get(type);
    }

    public static synchronized void registerLightSourceParserFactory(String type, LightSourceParser.LightSourceParserFactory factory) {
        LIGHT_SOURCE_PARSER_FACTORIES.put(type, factory);
    }

    private static LightSourceParser.LightSourceParserFactory lightSourceParserFactoryForType(String type) {
        return LIGHT_SOURCE_PARSER_FACTORIES.get(type);
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

        final var nodeParser = new SceneNodeParser(world);

        prefab.setNode(nodeParser.expressionToSceneNode(ctx.expression()));

        return prefab;

    }

    private ResourceTexture parseResourceTexture(SurfaceLangParser.ResourceTextureContext ctx) {

        final String name = ctx.resourceTextureName().IDENTIFIER().getText();
        String location = ctx.resourceTextureLocation().stringValue()
                .getText();
        location = location.substring(1, location.length() - 1);

        return new BaseResourceTexture(
                world.nextId(),
                name, location
        );

    }

    private <T> void checkThenForEach(List<ParsedComponent<T>> list, Consumer<T> runnable) {

        final List<String> allErrors = new ArrayList<>();
        for(var pc : list) {
            final var pcErr = pc.getErrors();
            if (pcErr != null && !pcErr.isEmpty()) {
                allErrors.addAll(pcErr);
            }
        }

        if(!allErrors.isEmpty()) {
            throw new IllegalStateException(
                    "Failed to parse expression due to the following errors:\n\n"
                    + String.join("\n", allErrors)
            );
        }

        list.stream().map(pc -> pc.getComponent())
                .forEach(runnable);

    }

    private void parseSceneNodes(SurfaceLangParser.WorldContext ctx) {

        final var nodeParser = new SceneNodeParser(world);
        checkThenForEach(
                nodeParser.parseComponents(ctx.expression()),
                node -> world.add(node)
        );

    }

    private void parseLights(SurfaceLangParser.WorldContext ctx) {

        Map<String, List<SurfaceLangParser.LightContext>> lightContextsByType = new HashMap<>(
                ctx.light().stream()
                        .collect(groupingBy(l -> l.lightType().IDENTIFIER().getText()))
        );

        for(String type : lightContextsByType.keySet()) {

            final var factory = lightSourceParserFactoryForType(type);
            final var parseContexts = lightContextsByType.get(type);

            if(factory == null) {
                throw new IllegalStateException("Unknown light type: " + type);
            }

            final var parser = factory.createParserForWorld(world);
            final var parsed = parser.parseComponents(parseContexts);

            checkThenForEach(
                    parsed,
                    l -> world.add(l)
            );

        }

    }

    private void parseMaterials(SurfaceLangParser.WorldContext ctx) {

        for(SurfaceLangParser.MaterialContext matCtx : ctx.material()) {

            final var type = matCtx.materialType().IDENTIFIER().getText();
            final var factory = materialParserFactoryForType(type);

            if(factory == null) {
                throw new IllegalStateException("Unknown material type: " + type);
            }

            final var parser = factory.createParserForWorld(world);
            final var parsed = parser.parseComponents(List.of(matCtx));

            checkThenForEach(
                    parsed,
                    m -> world.add(m)
            );

        }

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

        parseSceneNodes(ctx);
        parseLights(ctx);
        parseMaterials(ctx);

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
                            new BaseSceneNode(world.nextId(), "EVERYWHERE"),
                            new Vector3f(1f),
                            80.0f
                    )
            );
        }

    }
}
