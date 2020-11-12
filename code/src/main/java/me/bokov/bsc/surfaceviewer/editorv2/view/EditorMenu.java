package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        addMeshMenu.getWorldProperty()
                .bindBidirectional(worldProperty);

    }
}
