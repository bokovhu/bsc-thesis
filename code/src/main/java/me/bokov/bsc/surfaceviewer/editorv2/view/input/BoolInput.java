package me.bokov.bsc.surfaceviewer.editorv2.view.input;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;

import java.net.URL;
import java.util.*;

public class BoolInput extends GLInput<Boolean> implements Initializable {

    @Getter
    private BooleanProperty valueProperty = new SimpleBooleanProperty(false);

    @FXML
    private CheckBox checkbox;

    public BoolInput() {
        FXMLUtil.loadForComponent("/fxml/input/CheckboxWithLabel.fxml", this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        checkbox.selectedProperty().bindBidirectional(valueProperty);

    }

    @Override
    protected Property<Boolean> getInternalValueProperty() {
        return valueProperty;
    }

    @Override
    public void collectValue() {
        valueProperty.setValue(checkbox.isSelected());
    }
}
