package me.bokov.bsc.surfaceviewer.editor;

import me.bokov.bsc.surfaceviewer.Editor;
import me.bokov.bsc.surfaceviewer.Installable;
import me.bokov.bsc.surfaceviewer.editor.menu.FileMenu;
import me.bokov.bsc.surfaceviewer.editor.menu.NewMeshMenu;

import javax.swing.*;

public class EditorMenubar extends JMenuBar implements Installable<Editor> {

    private Editor editor;

    public EditorMenubar() {
    }

    @Override
    public void install(Editor parent) {

        this.editor = parent;
        add(new FileMenu(editor).create());

        add(new NewMeshMenu(editor).create());

        this.editor.getEditorFrame()
                .setJMenuBar(this);

    }

    @Override
    public void uninstall() {

        this.editor = null;
        this.removeAll();

    }
}
