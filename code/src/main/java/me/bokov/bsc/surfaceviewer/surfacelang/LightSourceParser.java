package me.bokov.bsc.surfaceviewer.surfacelang;

import me.bokov.bsc.surfaceviewer.scene.LightSource;
import me.bokov.bsc.surfaceviewer.scene.World;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.*;

public abstract class LightSourceParser extends ComponentParser<SurfaceLangParser.LightContext, LightSource> {

    public LightSourceParser(World world) {
        super(world);
    }

    protected abstract List<String> validateLightSource(SurfaceLangParser.LightContext ctx);
    protected abstract LightSource parseLightSource(Vector3f energy, SurfaceLangParser.LightContext ctx);

    public interface LightSourceParserFactory extends ComponentParserFactory<
            SurfaceLangParser.LightContext,
            LightSource,
            LightSourceParser
            > {}

    protected boolean hasLightParam(SurfaceLangParser.LightContext ctx, Predicate<SurfaceLangParser.LightParamContext> predicate) {
        return ctx.lightDef()
                .lightParamList()
                .lightParam()
                .stream().anyMatch(predicate);
    }
    protected <T> Optional<T> findLightParam(SurfaceLangParser.LightContext ctx, Predicate<SurfaceLangParser.LightParamContext> predicate, Function<SurfaceLangParser.LightParamContext, T> transform) {
        return ctx.lightDef()
                .lightParamList()
                .lightParam()
                .stream()
                .filter(predicate).findFirst()
                .map(transform);
    }

    protected String paramName(SurfaceLangParser.LightParamContext ctx) {
        return ctx.lightParamName()
                .IDENTIFIER()
                .getText();
    }

    @Override
    public List<ParsedComponent<LightSource>> parseComponents(List<SurfaceLangParser.LightContext> parseContexts) {

        List<ParsedComponent<LightSource>> result = new ArrayList<>();

        for(SurfaceLangParser.LightContext ctx : parseContexts) {

            final var validationErrors = new ArrayList<>(validateLightSource(ctx));

            if(!hasLightParam(ctx, lp -> "energy".equals(paramName(lp)) && lp.vec3Value() != null)) {
                validationErrors.add("energy not defined!");
            }

            if(validationErrors != null && !validationErrors.isEmpty()) {
                result.add(
                        ParsedComponent.<LightSource>builder()
                                .errors(validationErrors)
                                .build()
                );
            } else {

                final var energy = findLightParam(ctx, lp -> "energy".equals(paramName(lp)) && lp.vec3Value() != null, lp -> vec3Val(lp.vec3Value()));

                result.add(
                        ParsedComponent.<LightSource>builder()
                                .component(parseLightSource(energy.orElseThrow(), ctx))
                                .build()
                );
            }

        }

        return result;

    }
}
