package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.editorv2.event.EditSceneNodeNameEvent;
import me.bokov.bsc.surfaceviewer.editorv2.event.OpenEditorTabEvent;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;
import me.bokov.bsc.surfaceviewer.util.IOUtil;

import java.net.URL;
import java.util.*;

public class SceneNodeContextMenu extends ContextMenu implements Initializable {

    @Getter
    private ObjectProperty<SceneNode> sceneNodeProperty = new SimpleObjectProperty<>();

    @Getter
    private ObjectProperty<World> worldProperty = new SimpleObjectProperty<>();

    @FXML
    private AddMeshMenu addMeshMenu;

    public SceneNodeContextMenu() {

        FXMLUtil.loadForComponent("/fxml/SceneNodeContextMenu.fxml", this);

    }

    @FXML
    public void onDeleteSceneNode(ActionEvent event) {

        final var world = IOUtil.serialize(worldProperty.get());
        world.remove(sceneNodeProperty.get().getId());

        worldProperty.setValue(world);

    }

    @FXML
    public void onEditSceneNode(ActionEvent event) {

        fireEvent(
                new OpenEditorTabEvent(
                        sceneNodeProperty.get().getId()
                )
        );

    }

    @FXML
    public void onRenameSceneNode(ActionEvent event) {
        fireEvent(
                new EditSceneNodeNameEvent(sceneNodeProperty.get().getId())
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        addMeshMenu.getWorldProperty()
                .bindBidirectional(worldProperty);

        addMeshMenu.getFromNodeProperty()
                .bindBidirectional(sceneNodeProperty);

        sceneNodeProperty.addListener(
                (observable, oldValue, newValue) -> onSceneNodeChanged(newValue)
        );

    }

    private void onSceneNodeChanged(SceneNode node) {

        if (!node.getTemplate().supportsChildren) {
            addMeshMenu.setVisible(false);
        }

    }

}
