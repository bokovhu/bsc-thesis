package me.bokov.bsc.surfaceviewer.editorv2.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import lombok.Data;
import me.bokov.bsc.surfaceviewer.scene.World;

@Data
public class SceneBrowserModel {

    private ObjectProperty<TreeItem<Object>> treeRootProperty = new SimpleObjectProperty<>(new TreeItem<>());

}
