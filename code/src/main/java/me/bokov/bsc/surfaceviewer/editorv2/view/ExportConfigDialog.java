package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.editorv2.service.ExportTask;
import me.bokov.bsc.surfaceviewer.editorv2.view.input.FloatInput;
import me.bokov.bsc.surfaceviewer.editorv2.view.input.IntInput;
import me.bokov.bsc.surfaceviewer.editorv2.view.input.Vec3Input;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;
import org.joml.Vector3f;

import java.io.File;
import java.net.URL;
import java.util.*;

public class ExportConfigDialog extends VBox implements Initializable {

    @FXML
    private IntInput gridWidthInput;

    @FXML
    private IntInput gridHeightInput;

    @FXML
    private IntInput gridDepthInput;

    @FXML
    private Vec3Input gridOffsetInput;

    @FXML
    private FloatInput gridScaleInput;

    @FXML
    private TextField outputFilePathField;

    public ExportConfigDialog() {

        FXMLUtil.loadForComponent("/fxml/ExportDialogContent.fxml", this);

    }

    @FXML
    public void onChooseOutputFile(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setInitialFileName("Scene.obj");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Wavefront OBJ", "*.obj"));
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("glTF", "*.gltf"));
        fileChooser.setTitle("Export scene");

        final File outputFile = fileChooser.showSaveDialog(getScene().getWindow());

        outputFilePathField.setText(outputFile.getAbsolutePath());

    }

    public ExportTask.ExportConfig toExportConfig() {

        gridWidthInput.collectValue();
        gridHeightInput.collectValue();
        gridDepthInput.collectValue();
        gridOffsetInput.collectValue();
        gridScaleInput.collectValue();


        return new ExportTask.ExportConfig()
                .setGridWidth(gridWidthInput.getValueProperty().get())
                .setGridHeight(gridHeightInput.getValueProperty().get())
                .setGridDepth(gridDepthInput.getValueProperty().get())
                .setGridOffset(gridOffsetInput.getValueProperty().get())
                .setGridScale(gridScaleInput.getValueProperty().get())
                .setOutputPath(outputFilePathField.getText());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gridWidthInput.getValueProperty().setValue(64);
        gridWidthInput.getLabelProperty().setValue("Grid width");

        gridHeightInput.getValueProperty().setValue(64);
        gridHeightInput.getLabelProperty().setValue("Grid height");

        gridDepthInput.getValueProperty().setValue(64);
        gridDepthInput.getLabelProperty().setValue("Grid depth");

        gridOffsetInput.getValueProperty().setValue(new Vector3f(-4f));
        gridOffsetInput.getLabelProperty().setValue("Offset");

        gridScaleInput.getValueProperty().setValue(8.0f);
        gridScaleInput.getLabelProperty().setValue("Scale");

    }
}
