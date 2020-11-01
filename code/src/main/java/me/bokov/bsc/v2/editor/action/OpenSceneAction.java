package me.bokov.bsc.v2.editor.action;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.editor.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class OpenSceneAction extends AbstractAction {

    private final Editor editor;

    public OpenSceneAction(Editor editor) {
        super("Open scene", Icons.FA_FOLDER_SOLID_BLACK);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
