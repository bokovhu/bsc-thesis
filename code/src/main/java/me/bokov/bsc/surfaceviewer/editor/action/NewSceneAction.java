package me.bokov.bsc.surfaceviewer.editor.action;

import me.bokov.bsc.surfaceviewer.Editor;
import me.bokov.bsc.surfaceviewer.editor.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class NewSceneAction extends AbstractAction {

    private final Editor editor;

    public NewSceneAction(Editor editor) {
        super("New scene", Icons.FA_FILE_SOLID_BLACK);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final String sceneName = JOptionPane.showInputDialog("How should we call your new scene?");

        editor.getScene().setMeshes(new ArrayList<>());
        editor.getScene().setName(sceneName);
        editor.applySceneChanges();

    }

}
