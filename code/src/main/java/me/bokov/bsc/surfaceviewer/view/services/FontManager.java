package me.bokov.bsc.surfaceviewer.view.services;

import me.bokov.bsc.surfaceviewer.render.text.TextureFont;
import me.bokov.bsc.surfaceviewer.view.AppView;

import java.util.*;

@Deprecated
public class FontManager {

    private final AppView view;

    private final Map<String, TextureFont> fontsByName = new HashMap<>();

    public FontManager(AppView view) {
        this.view = view;
    }

    public TextureFont load(String name, String... fntFiles) {

        if (fontsByName.containsKey(name)) {
            return fontsByName.get(name);
        }

        TextureFont f = new TextureFont()
                .load(fntFiles);

        fontsByName.put(name, f);

        return f;

    }

}
