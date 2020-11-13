package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.*;
import javafx.util.StringConverter;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.editorv2.event.EditSceneNodeNameEvent;
import me.bokov.bsc.surfaceviewer.editorv2.event.OpenSceneNodeEditorEvent;
import me.bokov.bsc.surfaceviewer.scene.NodeTemplate;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.IOUtil;

import java.util.*;

public class SceneTreeCell extends TextFieldTreeCell<Object> {

    @Getter
    private ObjectProperty<SceneNode> sceneNodeProperty = new SimpleObjectProperty<>();

    @Getter
    private ObjectProperty<World> worldProperty = new SimpleObjectProperty<>();

    public SceneTreeCell() {
        getStyleClass()
                .add("scene-tree-cell");
        setConverter(
                new StringConverter<>() {
                    @Override
                    public String toString(Object object) {
                        return Objects.toString(object);
                    }

                    @Override
                    public Object fromString(String string) {
                        final var world = IOUtil.serialize(worldProperty.get());
                        final var node = world.findById(sceneNodeProperty.get().getId()).get();
                        node.getDisplay()
                                .setName(string);

                        worldProperty.setValue(world);

                        return node;
                    }
                }
        );
    }

    private boolean isSceneNodeCell(Object value) {
        return value instanceof SceneNode;
    }

    private boolean isWorldCell(Object value) {
        return value instanceof World;
    }

    private boolean isPortCell(Object value) {
        return value instanceof NodeTemplate.Port;
    }

    private void onSceneNodeItemDragged(MouseEvent event) {
        Dragboard db = startDragAndDrop(TransferMode.MOVE);
        ClipboardContent cc = new ClipboardContent();
        cc.put(DataFormat.PLAIN_TEXT, sceneNodeProperty.get().getId());
        db.setContent(cc);
        db.setDragView(snapshot(null, null));
        event.consume();
    }

    private void onSceneNodeDragOver(DragEvent event) {

        var node = sceneNodeProperty.get();

        if (!node.getTemplate().supportsChildren) {
            styleProperty().setValue("");
        } else {
            styleProperty().setValue("background: #efefef");
            event.acceptTransferModes(TransferMode.MOVE);
        }
    }

    private void onSceneNodeDropped(DragEvent event) {
        var node = sceneNodeProperty.get();

        if (node.getTemplate().supportsChildren && node.getTemplate().ports.isEmpty()) {
            event.setDropCompleted(true);

            final int childId = ((Number) event.getDragboard()
                    .getContent(DataFormat.PLAIN_TEXT)).intValue();

            if (node.getId() != childId) {

                final var world = IOUtil.serialize(worldProperty.get());
                final var n = world.findById(node.getId()).get();
                final var newChild = world.findById(childId).get();

                world.remove(childId);
                n.add(newChild);

                worldProperty.setValue(world);

            }
        }
    }

    private void onPortDropped(DragEvent event, NodeTemplate.Port port) {
        final int childId = ((Number) event.getDragboard()
                .getContent(DataFormat.PLAIN_TEXT)).intValue();
        var node = sceneNodeProperty.get();

        if (sceneNodeProperty.get().getId() != childId) {

            final var world = IOUtil.serialize(worldProperty.get());
            final var n = world.findById(node.getId()).get();
            final var newChild = world.findById(childId).get();

            if (n.pluggedPorts().containsKey(port.getName())) {
                world.remove(n.pluggedPorts().get(port.getName()).getId());
            }

            world.remove(childId);
            n.add(newChild);
            n.plug(port.getName(), newChild);

            worldProperty.setValue(world);

            event.setDropCompleted(true);

        }
    }

    private void updateSceneNodeItem(SceneNode node) {

        setEditable(true);

        sceneNodeProperty.setValue(node);

        setText(node.getId() + ": " + node.getTemplate().name() + " - " + node.getDisplay().getName());

        final var contextMenu = new SceneNodeContextMenu();
        contextMenu.getSceneNodeProperty()
                .bindBidirectional(sceneNodeProperty);
        contextMenu.getWorldProperty()
                .bindBidirectional(worldProperty);
        contextMenu.addEventHandler(OpenSceneNodeEditorEvent.OPEN_SCENE_NODE_EDITOR, this::fireEvent);
        contextMenu.addEventFilter(
                EditSceneNodeNameEvent.EDIT_SCENE_NODE_NAME,
                event -> startEdit()
        );
        setContextMenu(contextMenu);


        setOnDragDetected(this::onSceneNodeItemDragged);
        setOnDragOver(this::onSceneNodeDragOver);
        setOnDragDropped(this::onSceneNodeDropped);

    }

    private void updatePortItem(NodeTemplate.Port port) {

        setEditable(false);

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

                setOnDragOver(this::onSceneNodeDragOver);
                setOnDragDropped(event -> this.onPortDropped(event, port));

            }

        }

    }

    private void updateWorldItem(World world) {

        setEditable(false);

        final var addMeshMesh = new AddMeshMenu();
        addMeshMesh.getWorldProperty().bindBidirectional(worldProperty);
        final var contextMenu = new ContextMenu(addMeshMesh);
        setContextMenu(contextMenu);

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
