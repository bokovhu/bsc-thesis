package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.editorv2.service.LoadSceneTask;
import me.bokov.bsc.surfaceviewer.editorv2.service.SaveSceneTask;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;
import me.bokov.bsc.surfaceviewer.util.IOUtil;

import java.io.File;
import java.net.URL;
import java.util.*;

public class EditorMenu extends MenuBar implements Initializable {

    @Getter
    private ObjectProperty<World> worldProperty = new SimpleObjectProperty<>();

    @FXML
    private AddMeshMenu addMeshMenu;

    public EditorMenu() {

        FXMLUtil.loadForComponent("/fxml/EditorMenu.fxml", this);

    }

    @FXML
    public void onSaveScene(ActionEvent event) {

        Platform.runLater(
                () -> {

                    FileChooser fileChooser = new FileChooser();

                    fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                    fileChooser.setInitialFileName("Scene.surfacelang");
                    fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(
                            "SurfaceLang code",
                            "*.surfacelang"
                    ));
                    fileChooser.setTitle("Save scene");

                    final File outputFile = fileChooser.showSaveDialog(getScene().getWindow());

                    SaveSceneTask task = new SaveSceneTask();
                    task.getSavePathProperty().setValue(outputFile.getAbsolutePath());
                    task.getWorldProperty().setValue(IOUtil.serialize(worldProperty.get()));

                    task.run();

                }
        );

    }

    @FXML
    public void onSaveSceneAs(ActionEvent event) {
        onSaveScene(event);
    }

    @FXML
    public void onOpenScene(ActionEvent event) {

        Platform.runLater(
                () -> {

                    FileChooser fileChooser = new FileChooser();

                    fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                    fileChooser.setInitialFileName("Scene.surfacelang");
                    fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(
                            "SurfaceLang code",
                            "*.surfacelang"
                    ));
                    fileChooser.setTitle("Save scene");

                    final File outputFile = fileChooser.showOpenDialog(getScene().getWindow());

                    LoadSceneTask task = new LoadSceneTask();
                    task.getLoadPathProperty().setValue(outputFile.getAbsolutePath());
                    task.setOnSucceeded(
                            doneEvent -> worldProperty.setValue(task.getValue())
                    );
                    task.run();

                }
        );

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        addMeshMenu.getWorldProperty()
                .bindBidirectional(worldProperty);

    }
}
