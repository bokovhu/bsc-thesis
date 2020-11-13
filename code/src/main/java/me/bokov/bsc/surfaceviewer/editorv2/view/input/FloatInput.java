package me.bokov.bsc.surfaceviewer.editorv2.view.input;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.editorv2.view.laf.Borders;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;

import java.net.URL;
import java.util.*;

public class FloatInput extends GLInput<Number> implements Initializable {

    @Getter
    private FloatProperty valueProperty = new SimpleFloatProperty(0.0f);

    @FXML
    private TextField inputField;

    public FloatInput() {
        FXMLUtil.loadForComponent("/fxml/input/InputWithLabel.fxml", this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        inputField.setOnAction(
                event -> onTextEntered(inputField.getText())
        );

        inputField.textProperty()
                .setValue(valueProperty.get() + "");

        valueProperty.addListener(
                (observable, oldValue, newValue) -> inputField.setText(
                        String.format(Locale.ENGLISH, "%.4f", newValue)
                )
        );

    }

    @Override
    protected Property<Number> getInternalValueProperty() {
        return valueProperty;
    }

    @Override
    public void collectValue() {
        onTextEntered(inputField.getText());
    }

    private void onTextEntered(String newText) {

        try {

            float f = Float.parseFloat(newText);
            valueProperty.setValue(f);

        } catch (NumberFormatException ignored) {

        }

    }


}
