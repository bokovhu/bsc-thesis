package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeCell;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.editorv2.event.OpenSceneNodeEditorEvent;
import me.bokov.bsc.surfaceviewer.scene.NodeTemplate;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.scene.World;

import java.util.*;

public class SceneTreeCell extends TextFieldTreeCell<Object> {

    @Getter
    private ObjectProperty<SceneNode> sceneNodeProperty = new SimpleObjectProperty<>();

    @Getter
    private ObjectProperty<World> worldProperty = new SimpleObjectProperty<>();

    private boolean isSceneNodeCell(Object value) {
        return value instanceof SceneNode;
    }

    private boolean isWorldCell(Object value) {
        return value instanceof World;
    }

    private boolean isPortCell(Object value) {
        return value instanceof NodeTemplate.Port;
    }

    private void updateSceneNodeItem(SceneNode node) {

        sceneNodeProperty.setValue(node);

        setText(node.getId() + ": " + node.getTemplate().name() + " - " + node.getDisplay().getName());

        final var contextMenu = new SceneNodeContextMenu();
        contextMenu.getSceneNodeProperty()
                .bindBidirectional(sceneNodeProperty);
        contextMenu.getWorldProperty()
                .bindBidirectional(worldProperty);
        contextMenu.addEventHandler(OpenSceneNodeEditorEvent.OPEN_SCENE_NODE_EDITOR, this::fireEvent);
        setContextMenu(contextMenu);

    }

    private void updatePortItem(NodeTemplate.Port port) {

        TreeItem<Object> parentItem = getTreeItem()
                .getParent();

        if (isSceneNodeCell(parentItem.getValue())) {

            final var parentNode = (SceneNode) parentItem.getValue();
            List<SceneNode> parentChildren = parentNode.children();

            if (parentNode.pluggedPorts().containsKey(port.getName())) {

                updateSceneNodeItem(
                        parentNode.pluggedPorts().get(port.getName())
                );

            } else {

                sceneNodeProperty.setValue(parentNode);

                Label portNameLabel = new Label(port.getName());
                portNameLabel.styleProperty().setValue("-fx-text-fill: " + port.getColor());
                setGraphic(portNameLabel);
                setText(port.getName());

                final var addMeshToPortMenu = new AddMeshMenu();
                addMeshToPortMenu.getFromNodeProperty()
                        .bindBidirectional(sceneNodeProperty);
                addMeshToPortMenu.getWorldProperty()
                        .bindBidirectional(worldProperty);
                addMeshToPortMenu.getPortProperty()
                        .setValue(port.getName());

                final var contextMenu = new ContextMenu(addMeshToPortMenu);
                setContextMenu(contextMenu);

            }

        }

    }

    private void updateWorldItem(World world) {

    }

    @Override
    public void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        if (isSceneNodeCell(item)) {
            updateSceneNodeItem((SceneNode) item);
        } else if (isWorldCell(item)) {
            updateWorldItem((World) item);
        } else if (isPortCell(item)) {
            updatePortItem((NodeTemplate.Port) item);
        }
    }
}
