package me.bokov.bsc.surfaceviewer.editor;

import me.bokov.bsc.surfaceviewer.Editor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class SceneActionBar extends JPanel {

    private final Editor editor;

    public SceneActionBar(Editor editor) {
        this.editor = editor;
    }

    public SceneActionBar create() {

        final var layout = new MigLayout("", "[grow]", "[shrink]");

        return this;

    }

}
