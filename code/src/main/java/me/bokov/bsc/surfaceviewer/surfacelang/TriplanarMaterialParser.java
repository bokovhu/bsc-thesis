package me.bokov.bsc.surfaceviewer.surfacelang;

import me.bokov.bsc.surfaceviewer.scene.Materializer;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.scene.materializer.TriplanarMaterial;

import java.util.*;

public class TriplanarMaterialParser extends MaterialParser {
    public TriplanarMaterialParser(World world) {
        super(world);
    }

    @Override
    protected List<String> validate(SurfaceLangParser.MaterialContext ctx) {

        List<String> errors = new ArrayList<>();

        if (!hasMaterialParam(ctx, mp -> "diffuse".equals(paramName(mp)) && mp.vec3Value() != null)
                && !hasMaterialParam(ctx, mp -> "diffuseMap".equals(paramName(mp)) && mp.stringValue() != null)) {
            errors.add("Neither diffuse or diffuseMap are set!");
        }

        if (!hasMaterialParam(ctx, mp -> "shininess".equals(paramName(mp)) && mp.numberValue() != null)
                && !hasMaterialParam(ctx, mp -> "shininessMap".equals(paramName(mp)) && mp.stringValue() != null)) {
            errors.add("Neither shininess or shininessMap are set!");
        }

        return errors;
    }

    @Override
    protected Materializer parse(
            SceneNode boundary,
            SurfaceLangParser.MaterialContext ctx
    ) {

        final SurfaceLangParser.Vec3ValueContext triplanarDiffuseCtx = findMaterialParam(
                ctx.materialDef(), "diffuse",
                mp -> mp.vec3Value() != null,
                SurfaceLangParser.MaterialParamContext::vec3Value
        );
        final SurfaceLangParser.StringValueContext triplanarDiffuseMapCtx = findMaterialParam(
                ctx.materialDef(), "diffuseMap",
                mp -> mp.stringValue() != null,
                SurfaceLangParser.MaterialParamContext::stringValue
        );
        final SurfaceLangParser.NumberValueContext triplanarShininessCtx = findMaterialParam(
                ctx.materialDef(), "shininess",
                mp -> mp.numberValue() != null,
                SurfaceLangParser.MaterialParamContext::numberValue
        );
        final SurfaceLangParser.StringValueContext triplanarShininessMapCtx = findMaterialParam(
                ctx.materialDef(), "shininessMap",
                mp -> mp.stringValue() != null,
                SurfaceLangParser.MaterialParamContext::stringValue
        );

        final var triplanarBuilder = TriplanarMaterial.builder()
                .id(getWorld().nextId());

        if (triplanarDiffuseMapCtx != null) {
            triplanarBuilder.diffuseMapName(stringVal(triplanarDiffuseMapCtx));
        } else if (triplanarDiffuseCtx != null) {
            triplanarBuilder.diffuse(vec3Val(triplanarDiffuseCtx));
        } else {
            throw new IllegalArgumentException("Triplanar materials require a diffuseMap or diffuse attribute!");
        }

        if (triplanarShininessMapCtx != null) {
            triplanarBuilder.shininessMapName(stringVal(triplanarShininessMapCtx));
        } else if (triplanarShininessCtx != null) {
            triplanarBuilder.defaultShininess(floatVal(triplanarShininessCtx));
        } else {
            throw new IllegalArgumentException("Triplanar materials require a shininessMap or shininess attribute!");
        }

        SurfaceLangParser.NumberValueContext triplanarScaleCtx = findMaterialParam(
                ctx.materialDef(), "textureScale",
                mp -> mp.numberValue() != null,
                SurfaceLangParser.MaterialParamContext::numberValue
        );

        if (triplanarScaleCtx != null) {
            triplanarBuilder.scale(floatVal(triplanarScaleCtx));
        }

        return triplanarBuilder
                .boundary(boundary)
                .build();

    }
}
