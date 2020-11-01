package me.bokov.bsc.v2.editor.menu;

import me.bokov.bsc.v2.Editor;

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
