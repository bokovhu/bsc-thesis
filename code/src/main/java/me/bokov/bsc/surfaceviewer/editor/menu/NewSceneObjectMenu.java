package me.bokov.bsc.surfaceviewer.editor.menu;

import me.bokov.bsc.surfaceviewer.Editor;

import javax.swing.*;

public class NewSceneObjectMenu extends JMenu {

    private final Editor editor;

    public NewSceneObjectMenu(Editor editor) {
        super("New scene object");
        this.editor = editor;
    }

    public NewSceneObjectMenu create() {

        add(new NewLightMenu(editor).create());
        add(new NewMeshMenu(editor).create());

        return this;
    }

}
