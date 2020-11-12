package me.bokov.bsc.surfaceviewer.editorv2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.extern.java.Log;
import me.bokov.bsc.surfaceviewer.App;
import me.bokov.bsc.surfaceviewer.editorv2.event.OpenSceneNodeEditorEvent;
import me.bokov.bsc.surfaceviewer.editorv2.service.UpdateViewTask;
import me.bokov.bsc.surfaceviewer.editorv2.view.EditorMenu;
import me.bokov.bsc.surfaceviewer.editorv2.view.RendererSettings;
import me.bokov.bsc.surfaceviewer.editorv2.view.SceneBrowser;
import me.bokov.bsc.surfaceviewer.editorv2.view.SceneNodeEditor;
import me.bokov.bsc.surfaceviewer.scene.BaseWorld;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;

import java.net.URL;
import java.util.*;

@Log
public class EditorWindow extends AnchorPane implements Initializable {

    private ObjectProperty<World> worldProperty = new SimpleObjectProperty<>(new BaseWorld());

    @Getter
    private ObjectProperty<App> appProperty = new SimpleObjectProperty<>(null);

    @FXML
    private SceneBrowser sceneBrowser;

    @FXML
    private EditorMenu editorMenu;

    @FXML
    private TabPane editorTabs;

    @FXML
    private RendererSettings rendererSettings;

    public EditorWindow() {

        FXMLUtil.loadForComponent("/fxml/EditorRoot.fxml", this);

    }

    private void onWorldChanged(World newWorld) {

        System.out.println("onWorldChanged");

        final var task = new UpdateViewTask();
        task.getAppProperty().bind(appProperty);
        task.getNewWorldProperty().setValue(newWorld);
        task.run();

    }

    private void onOpenSceneEditorRequested(int nodeId) {

        Optional<Tab> openedTab = editorTabs.getTabs()
                .stream()
                .filter(
                        tab -> ("scene-node-" + nodeId).equals(tab.idProperty().get())
                )
                .findFirst();

        if (openedTab.isPresent()) {
            editorTabs.getSelectionModel()
                    .select(openedTab.get());
        } else {

            final var editor = new SceneNodeEditor();
            final var node = worldProperty.get()
                    .findById(nodeId).get();
            editor.getSceneNodeProperty()
                    .setValue(node);
            editor.getWorldProperty()
                    .bindBidirectional(worldProperty);

            Tab newEditorTab = new Tab(
                    node.getDisplay().getName(),
                    editor
            );
            newEditorTab.setClosable(true);
            newEditorTab.idProperty()
                    .setValue("scene-node-" + node.getId());
            editorTabs.getTabs().add(newEditorTab);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sceneBrowser.addEventHandler(
                OpenSceneNodeEditorEvent.OPEN_SCENE_NODE_EDITOR,
                event -> onOpenSceneEditorRequested(event.getSceneNodeId())
        );

        sceneBrowser.getWorldProperty()
                .setValue(worldProperty.get());
        sceneBrowser.getWorldProperty()
                .bindBidirectional(worldProperty);

        editorMenu.getWorldProperty()
                .bindBidirectional(worldProperty);

        worldProperty.addListener(
                (observable, oldValue, newValue) -> onWorldChanged(newValue)
        );

        rendererSettings.getAppProperty()
                .bind(appProperty);

    }
}
