package me.bokov.bsc.v2.editor.action;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.editor.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;

// TODO: Implement
public class NewSceneAction extends AbstractAction {

    private final Editor editor;

    public NewSceneAction(Editor editor) {
        super("New scene", Icons.FA_FILE_SOLID_BLACK);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final String sceneName = JOptionPane.showInputDialog("How should we call your new scene?");

    }

}
