package me.bokov.bsc.v2.editor.menu;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.editor.action.NewSceneAction;
import me.bokov.bsc.v2.editor.action.OpenSceneAction;

import javax.swing.*;

public class FileMenu extends JMenu {

    private final Editor editor;

    public FileMenu(Editor editor) {
        super("File");
        this.editor = editor;
    }

    public FileMenu create() {

        add(new JMenuItem(new NewSceneAction(editor)));
        add(new JMenuItem(new OpenSceneAction(editor)));

        return this;
    }
}
