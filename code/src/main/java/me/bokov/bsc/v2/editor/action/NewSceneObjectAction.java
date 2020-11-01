package me.bokov.bsc.v2.editor.action;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.editor.Icons;
import me.bokov.bsc.v2.editor.menu.NewSceneObjectMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class NewSceneObjectAction extends AbstractAction {

    private final Editor editor;

    public NewSceneObjectAction(Editor editor) {
        super("Add scene object", Icons.FA_PLUS_SOLID_GREEN);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JPopupMenu popupMenu = new JPopupMenu("New scene object");
        popupMenu.add(new NewSceneObjectMenu(editor).create());

        final var component = (JComponent) e.getSource();

        popupMenu.show(component, component.getX(), component.getY());

    }

}
