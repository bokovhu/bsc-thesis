package me.bokov.bsc.surfaceviewer.editor.tab;

import me.bokov.bsc.surfaceviewer.editor.EditorTabset;

import javax.swing.*;

public class CloseableTabComponent extends JPanel {

    private final EditorTabset editorTabset;

    public CloseableTabComponent(EditorTabset editorTabset) {
        this.editorTabset = editorTabset;
    }

}
