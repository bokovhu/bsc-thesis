package me.bokov.bsc.surfaceviewer.editorv2.service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.scene.LightSource;
import me.bokov.bsc.surfaceviewer.scene.NodeTemplate;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.scene.World;

import java.util.stream.*;

@Getter
public class SceneTreeBuilderTask extends Task<TreeItem<Object>> {

    private ObjectProperty<World> worldProperty = new SimpleObjectProperty<>(null);

    private TreeItem<Object> parentSceneNodeToTreeItem(SceneNode node) {

        TreeItem<Object> item = new TreeItem<>(node);
        item.getChildren()
                .addAll(
                        node.children().stream().map(this::sceneNodeToTreeItem)
                                .collect(Collectors.toList())
                );

        item.setExpanded(true);

        return item;
    }

    private TreeItem<Object> portedTreeItem(SceneNode node) {

        TreeItem<Object> item = new TreeItem<>(node);
        for (NodeTemplate.Port port : node.getTemplate().ports) {
            if (node.pluggedPorts().containsKey(port.getName())) {
                item.getChildren().add(
                        sceneNodeToTreeItem(node.pluggedPorts().get(port.getName()))
                );
            } else {
                item.getChildren().add(
                        new TreeItem<>(port)
                );
            }
        }

        item.setExpanded(true);

        return item;

    }

    private TreeItem<Object> sceneNodeToTreeItem(SceneNode node) {

        if (node.getTemplate().supportsChildren) {
            if (node.getTemplate().ports.isEmpty()) {
                return parentSceneNodeToTreeItem(node);
            } else {
                return portedTreeItem(node);
            }
        } else {

            TreeItem<Object> item = new TreeItem<>(node);
            return item;

        }

    }

    private TreeItem<Object> lightSourceToTreeItem(LightSource lightSource) {

        TreeItem<Object> item = new TreeItem<>(lightSource);
        return item;

    }

    @Override
    protected TreeItem<Object> call() throws Exception {

        final World world = worldProperty.get();

        if (world == null) {
            throw new IllegalArgumentException("World is null.");
        }

        TreeItem<Object> root = new TreeItem<>(world);

        for (var ls : world.getLightSources()) {
            root.getChildren().add(lightSourceToTreeItem(ls));
        }

        for (var node : world.roots()) {

            root.getChildren().add(sceneNodeToTreeItem(node));

        }

        root.setExpanded(true);

        return root;
    }

}
