package me.bokov.bsc.surfaceviewer.editorv2.view.input;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;

import java.net.URL;
import java.util.*;

public class BoolInput extends HBox implements Initializable {
    @Getter
    private StringProperty labelProperty = new SimpleStringProperty();
    @Getter
    private BooleanProperty valueProperty = new SimpleBooleanProperty(false);

    @FXML
    private Label inputLabel;

    @FXML
    private CheckBox checkbox;

    public BoolInput() {
        FXMLUtil.loadForComponent("/fxml/input/CheckboxWithLabel.fxml", this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        inputLabel.textProperty()
                .bind(labelProperty);

        checkbox.selectedProperty().bindBidirectional(valueProperty);

    }
}
