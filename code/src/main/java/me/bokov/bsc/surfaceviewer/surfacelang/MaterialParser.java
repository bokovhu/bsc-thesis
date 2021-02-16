package me.bokov.bsc.surfaceviewer.surfacelang;

import me.bokov.bsc.surfaceviewer.scene.Materializer;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.scene.World;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public abstract class MaterialParser extends ComponentParser<SurfaceLangParser.MaterialContext, Materializer> {

    public MaterialParser(World world) {
        super(world);
    }

    public interface MaterialParserFactory extends ComponentParserFactory<
            SurfaceLangParser.MaterialContext,
            Materializer,
            MaterialParser
            > {}

    protected <T> T findMaterialParam(
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

    protected boolean hasMaterialParam(SurfaceLangParser.MaterialContext ctx, Predicate<SurfaceLangParser.MaterialParamContext> predicate) {
        return ctx.materialDef()
                .materialParamList()
                .materialParam()
                .stream()
                .anyMatch(predicate);
    }

    protected String paramName(SurfaceLangParser.MaterialParamContext ctx) {
        return ctx.materialParamName()
                .IDENTIFIER()
                .getText();
    }

    protected abstract List<String> validate(SurfaceLangParser.MaterialContext ctx);
    protected abstract Materializer parse(SceneNode boundary, SurfaceLangParser.MaterialContext ctx);

    private ParsedComponent<Materializer> parseMaterial(SurfaceLangParser.MaterialContext ctx) {

        final var type = ctx.materialType().IDENTIFIER().getText().toLowerCase();

        final SurfaceLangParser.ExpressionContext boundaryContext = findMaterialParam(
                ctx.materialDef(), "boundary",
                mp -> mp.expression() != null,
                SurfaceLangParser.MaterialParamContext::expression
        );

        if (boundaryContext == null) {
            return ParsedComponent.<Materializer>builder()
                    .error("No boundary expression for material!")
                    .build();
        }

        List<String> errors = validate(ctx);

        if(errors != null && errors.size() > 0) {
            return ParsedComponent.<Materializer>builder()
                    .errors(errors)
                    .build();
        }

        return ParsedComponent.<Materializer>builder()
                .component(parse(expressionToSceneNode(boundaryContext), ctx))
                .build();

    }

    @Override
    public List<ParsedComponent<Materializer>> parseComponents(
            List<SurfaceLangParser.MaterialContext> parseContexts
    ) {
        return parseContexts.stream().map(this::parseMaterial).collect(Collectors.toList());
    }

}
