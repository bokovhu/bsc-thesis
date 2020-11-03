package me.bokov.bsc.surfaceviewer.render.text;

import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram.CachedUniform;
import me.bokov.bsc.surfaceviewer.render.Texture;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.*;

// TODO
public class Text {

    private final Map<Integer, Drawable> pageDrawables = new HashMap<>();
    private final Vector4f color = new Vector4f(1f);
    private String text;
    private List<Glyph> glyphs = new ArrayList<>();

    private void makePageDrawables(FontInstance font) {
        for (var d : pageDrawables.values()) {
            d.tearDown();
        }
        pageDrawables.clear();

        Map<Integer, FloatBuffer> pageVertices = new HashMap<>();
        Map<Integer, Integer> pageVertexCounts = new HashMap<>();

        float x = 0.0f;
        float y = 0.0f;
        for (var g : glyphs) {
            final var fb = pageVertices.computeIfAbsent(
                    g.getPage(), k -> BufferUtils.createFloatBuffer(6 * glyphs.size() * (2 + 2 + 4))
            );
            final Texture t = font.getPages().get(g.getPage());

            final float x00 = x + g.getXoff();
            final float y00 = y + g.getYoff();
            final float x11 = x00 + g.getW();
            final float y11 = y00 + g.getH();

            final float u00 = (1.0f / t.w()) * (float) g.getU();
            final float v00 = (1.0f / t.h()) * (float) g.getV();
            final float u11 = (1.0f / t.w()) * (float) (g.getU() + g.getW());
            final float v11 = (1.0f / t.h()) * (float) (g.getV() + g.getH());

            fb.put(new float[]{
                    x00, y00, u00, v00, color.x, color.y, color.z, color.w,
                    x11, y00, u11, v00, color.x, color.y, color.z, color.w,
                    x00, y11, u00, v11, color.x, color.y, color.z, color.w,

                    x11, y00, u11, v00, color.x, color.y, color.z, color.w,
                    x11, y11, u11, v11, color.x, color.y, color.z, color.w,
                    x00, y11, u00, v11, color.x, color.y, color.z, color.w
            });

            pageVertexCounts.compute(
                    g.getPage(),
                    (k, v) -> (v == null ? 0 : v) + 6
            );

            x += g.getXadv();
        }

        for (var entry : pageVertices.entrySet()) {

            final Drawable drawable = Drawable.standard2D();
            drawable.init();

            drawable.upload(
                    entry.getValue().flip(), GL46.GL_TRIANGLES, pageVertexCounts.get(entry.getKey())
            );

            pageDrawables.put(entry.getKey(), drawable);

        }
    }

    public Text ofText(String text, FontInstance font) {
        this.text = text;
        this.glyphs = new ArrayList<>(font.convertText(this.text));
        makePageDrawables(font);
        return this;
    }

    public String text() {
        return this.text;
    }

    public Set<Integer> usedPages() {
        return pageDrawables.keySet();
    }

    public void draw(FontInstance font, CachedUniform samplerUniform) {

        for (var e : pageDrawables.entrySet()) {

            font.getPages().get(e.getKey())
                    .bind(0);
            samplerUniform.i1(0);

            e.getValue().draw();

        }

    }

}
