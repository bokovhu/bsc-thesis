package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.render.Texture;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

// TODO: Test and fix if wrong
public class PrecomputedSurface implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final String textureName;
    private final Vector3f p000;
    private final Vector3f p111;

    private final Vector3f size;

    private final Vector3f tmpPT = new Vector3f();

    public PrecomputedSurface(String textureName, Vector3f p000, Vector3f p111) {
        this.textureName = textureName;
        this.p000 = p000;
        this.p111 = p111;
        this.size = new Vector3f(
                Math.abs(p111.x - p000.x),
                Math.abs(p111.y - p000.y),
                Math.abs(p111.z - p000.z)
        );
    }

    public String getTextureName() {
        return textureName;
    }

    public Vector3f getP000() {
        return p000;
    }

    public Vector3f getP111() {
        return p111;
    }

    private boolean inside(Vector3f p) {
        return p.x >= p000.x && p.y >= p000.y && p.z >= p000.z
                && p.x <= p111.x && p.y <= p111.y && p.z <= p111.z;
    }

    @Override
    public Float evaluate(CPUContext context) {

        final var t = context.getTexture(this.textureName);

        if (t.type() != Texture.TextureType.Tex3D) {
            throw new IllegalArgumentException("Texture should be a 3D texture.");
        }

        final var p = context.getPoint();

        tmpPT.set(p).sub(p000)
                .div(this.size)
                .min(new Vector3f(1f, 1f, 1f))
                .max(new Vector3f(0f, 0f, 0f));

        final float u = tmpPT.x * t.width();
        final float v = tmpPT.y * t.height();
        final float w = tmpPT.z * t.depth();

        final Vector4f color = t.atTrilinear(u, v, w);

        return color.x;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        return List.of(
                var(
                        "vec3",
                        context.getContextId() + "_PT",
                        max(min(
                                opDiv(paren(opMinus(ref(context.getPointVariable()), vec3(p000))), vec3(size)),
                                vec3(1f, 1f, 1f)
                        ), vec3(0.0f, 0.0f, 0.0f))
                ),
                var(
                        "vec4",
                        context.getContextId() + "_Color",
                        fn(
                                "texture",
                                ref(context.getTextureUniformName(textureName)),
                                ref(context.getContextId() + "_PT")
                        )
                ),
                resultVar(context, ref(context.getContextId() + "_Color", "x"))
        );
    }
}
