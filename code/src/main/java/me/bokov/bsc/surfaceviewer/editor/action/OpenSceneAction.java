package me.bokov.bsc.surfaceviewer.editor.action;

import me.bokov.bsc.surfaceviewer.Editor;
import me.bokov.bsc.surfaceviewer.World;
import me.bokov.bsc.surfaceviewer.editor.Icons;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

public class OpenSceneAction extends AbstractAction {

    private final Editor editor;

    public OpenSceneAction(Editor editor) {
        super("Open scene", Icons.FA_FOLDER_SOLID_BLACK);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Scene files", "scene"));

        final int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {

            final File sceneFile = fileChooser.getSelectedFile();

            try (FileInputStream fis = new FileInputStream(sceneFile);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                final World serializedWorld = (World) ois.readObject();

                editor.getScene().setName(serializedWorld.getName());
                editor.getScene().setMeshes(
                        new ArrayList<>(serializedWorld.getMeshes())
                );

                editor.applySceneChanges();

            } catch (Exception exc) {
                JOptionPane.showMessageDialog(
                        null,
                        exc.getMessage(),
                        "An error occured while saving your scene", JOptionPane.ERROR_MESSAGE
                );
            }

        }

    }
}
