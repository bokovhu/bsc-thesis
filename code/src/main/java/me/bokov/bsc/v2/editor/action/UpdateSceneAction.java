package me.bokov.bsc.v2.editor.action;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.editor.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class UpdateSceneAction extends AbstractAction {

    private final Runnable beforeUpdate;
    private final Editor editor;

    public UpdateSceneAction(Runnable beforeUpdate, Editor editor) {
        super("Apply scene changes", Icons.FA_PLUS_SOLID_GREEN);
        this.beforeUpdate = beforeUpdate;
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        beforeUpdate.run();

        SwingUtilities.invokeLater(
                this.editor::applySceneChanges
        );

    }
}
