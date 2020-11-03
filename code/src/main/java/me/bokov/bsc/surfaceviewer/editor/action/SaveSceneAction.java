package me.bokov.bsc.surfaceviewer.editor.action;

import me.bokov.bsc.surfaceviewer.Editor;
import me.bokov.bsc.surfaceviewer.editor.Icons;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class SaveSceneAction extends AbstractAction {

    private final Editor editor;

    public SaveSceneAction(Editor editor) {
        super("Save scene", Icons.FA_FILE_SOLID_BLACK);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setSelectedFile(new File(editor.getScene().getName() + ".scene"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Scene files", "scene"));

        final int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {

            final File sceneFile = fileChooser.getSelectedFile();

            try (FileOutputStream fos = new FileOutputStream(sceneFile);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {

                oos.writeObject(editor.getScene());

                JOptionPane.showMessageDialog(null, "Saved " + editor.getScene().getName());

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
