package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.scene.BaseSceneNode;
import me.bokov.bsc.surfaceviewer.scene.NodeTemplate;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;
import me.bokov.bsc.surfaceviewer.util.IOUtil;

import java.net.URL;
import java.util.*;

public class AddMeshMenu extends Menu implements Initializable {

    @Getter
    private ObjectProperty<SceneNode> fromNodeProperty = new SimpleObjectProperty<>(null);

    @Getter
    private StringProperty portProperty = new SimpleStringProperty(null);

    @Getter
    private ObjectProperty<World> worldProperty = new SimpleObjectProperty<>(null);

    @FXML
    private Menu operatorsMenu;

    @FXML
    private Menu shapesMenu;

    public AddMeshMenu() {

        FXMLUtil.loadForComponent("/fxml/AddMeshMenu.fxml", this);

    }

    private void addToWorld(SceneNode node) {
        final var world = IOUtil.serialize(worldProperty.get());
        world.add(node);
        worldProperty.setValue(world);
    }

    private void addToNode(SceneNode parent, SceneNode node) {
        final var world = IOUtil.serialize(worldProperty.get());
        SceneNode parentById = world.findById(parent.getId())
                .get();
        parentById.add(node);

        if (portProperty.get() != null) {
            parentById.plug(portProperty.get(), node);
        }

        worldProperty.setValue(world);
    }

    private void addNode(NodeTemplate template) {
        final var world = IOUtil.serialize(worldProperty.get());
        final var newNode = new BaseSceneNode(world.nextId(), template.getName());
        final var parentNode = fromNodeProperty.getValue();
        if (parentNode != null) {
            addToNode(parentNode, newNode);
        } else {
            addToWorld(newNode);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for (NodeTemplate nodeTemplate : NodeTemplate.getAll()) {

            MenuItem nodeTemplateItem = new MenuItem(nodeTemplate.getName());

            nodeTemplateItem.setOnAction(
                    event -> addNode(nodeTemplate)
            );

            if (nodeTemplate.isSupportsChildren()) {
                operatorsMenu.getItems()
                        .add(nodeTemplateItem);
            } else {
                shapesMenu.getItems()
                        .add(nodeTemplateItem);
            }

        }

    }
}
