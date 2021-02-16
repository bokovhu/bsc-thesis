package me.bokov.bsc.surfaceviewer.surfacelang;

import me.bokov.bsc.surfaceviewer.scene.Materializer;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.scene.materializer.ConstantMaterial;
import org.joml.Vector3f;

import java.util.*;

public class ConstantMaterialParser extends MaterialParser {

    public ConstantMaterialParser(World world) {
        super(world);
    }

    @Override
    protected List<String> validate(SurfaceLangParser.MaterialContext ctx) {
        return Collections.emptyList();
    }

    @Override
    protected Materializer parse(SceneNode boundary, SurfaceLangParser.MaterialContext ctx) {

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

        var diffuseColor = constantDiffuseCtx == null ? new Vector3f(1f) : vec3Val(constantDiffuseCtx);
        var shininess = constantShininessCtx == null ? 32.0f : floatVal(constantShininessCtx);

        return new ConstantMaterial(getWorld().nextId(), boundary, diffuseColor, shininess);
    }
}
