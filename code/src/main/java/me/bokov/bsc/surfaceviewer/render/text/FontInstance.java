package me.bokov.bsc.surfaceviewer.render.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.render.Texture;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class FontInstance {

    private final TextureFont font;
    private final int size;
    private final Map<Integer, Texture> pages;
    private final Map<Character, Glyph> glyphs = new HashMap<>();

    public FontInstance glyph(Glyph g) {
        glyphs.put((char) g.getCharCode(), g);
        return this;
    }

    public List<Glyph> convertText(String text) {
        List<Glyph> result = new ArrayList<>();
        for (char c : text.toCharArray()) {
            final Glyph g = glyphs.get(c);
            if (g != null) {
                result.add(g);
            }
        }
        return result;
    }

}
