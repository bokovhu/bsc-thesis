package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;

import java.net.URL;
import java.util.*;

public class RendererStatus extends HBox implements Initializable {

    @Getter
    private StringProperty statusInfoProperty = new SimpleStringProperty("No news.");

    @FXML
    private TextArea logArea;

    public RendererStatus() {
        FXMLUtil.loadForComponent("/fxml/RendererStatus.fxml", this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        logArea.textProperty().bind(statusInfoProperty);

    }
}
