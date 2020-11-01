package me.bokov.bsc.v2.editor;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.Installable;
import me.bokov.bsc.v2.editor.menu.FileMenu;

import javax.swing.*;

public class EditorMenubar extends JMenuBar implements Installable<Editor> {

    private Editor editor;

    public EditorMenubar() {
    }

    @Override
    public void install(Editor parent) {

        this.editor = parent;
        add(new FileMenu(editor).create());

        this.editor.getEditorFrame()
                .setJMenuBar(this);

    }

    @Override
    public void uninstall() {

        this.editor = null;
        this.removeAll();

    }
}
