package me.bokov.bsc.surfaceviewer.editor.menu;

import me.bokov.bsc.surfaceviewer.Editor;
import me.bokov.bsc.surfaceviewer.editor.action.NewSceneAction;
import me.bokov.bsc.surfaceviewer.editor.action.OpenSceneAction;
import me.bokov.bsc.surfaceviewer.editor.action.SaveSceneAction;

import javax.swing.*;

public class FileMenu extends JMenu {

    private final Editor editor;

    public FileMenu(Editor editor) {
        super("File");
        this.editor = editor;
    }

    public FileMenu create() {

        add(new JMenuItem(new NewSceneAction(editor)));
        add(new JMenuItem(new SaveSceneAction(editor)));
        add(new JMenuItem(new OpenSceneAction(editor)));

        return this;
    }
}
