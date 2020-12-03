package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.editorv2.event.OpenEditorTabEvent;
import me.bokov.bsc.surfaceviewer.editorv2.service.SceneTreeBuilderTask;
import me.bokov.bsc.surfaceviewer.scene.World;

public class SceneBrowser extends TreeView<Object> {

    @Getter
    private ObjectProperty<World> worldProperty = new SimpleObjectProperty<>();

    private ObjectProperty<TreeItem<Object>> treeRootProperty = new SimpleObjectProperty<>(new TreeItem<>());

    public SceneBrowser() {
        super();

        worldProperty.addListener(
                (observable, oldValue, newValue) -> onWorldChanged(newValue)
        );

        treeRootProperty
                .addListener(
                        (observable, oldValue, newValue) -> setRoot(newValue)
                );

        setCellFactory(
                treeView -> makeTreeCell()
        );
        setEditable(true);

    }

    private TreeCell<Object> makeTreeCell() {

        SceneTreeCell cell = new SceneTreeCell();

        cell.addEventHandler(OpenEditorTabEvent.OPEN_SCENE_NODE_EDITOR, this::fireEvent);

        cell.getWorldProperty()
                .bindBidirectional(worldProperty);

        return cell;

    }

    private void onWorldChanged(World newWorld) {

        final var builderTask = new SceneTreeBuilderTask();
        builderTask.getWorldProperty()
                .set(newWorld);

        builderTask.setOnSucceeded(
                ev -> treeRootProperty
                        .set((TreeItem<Object>) ev.getSource().getValue())
        );
        builderTask.run();

    }

}
