package me.bokov.bsc.surfaceviewer.editor.menu;

import me.bokov.bsc.surfaceviewer.Editor;
import me.bokov.bsc.surfaceviewer.editor.Icons;

import javax.swing.*;

public class NewLightMenu extends JMenu {

    private final Editor editor;

    public NewLightMenu(Editor editor) {
        super("New light");
        this.editor = editor;
    }

    public NewLightMenu create() {

        add(new JMenuItem("Point light", Icons.FA_LIGHTBULB_SOLID_BLACK));
        add(new JMenuItem("Directional light", Icons.FA_LIGHTBULB_SOLID_BLACK));

        return this;
    }
}
