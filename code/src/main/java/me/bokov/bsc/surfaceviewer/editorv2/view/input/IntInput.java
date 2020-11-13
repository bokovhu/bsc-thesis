package me.bokov.bsc.surfaceviewer.editorv2.view.input;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;

import java.net.URL;
import java.util.*;

public class IntInput extends GLInput<Number> implements Initializable {

    @Getter
    private IntegerProperty valueProperty = new SimpleIntegerProperty(0);

    @FXML
    private TextField inputField;

    public IntInput() {
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
                        String.format(Locale.ENGLISH, "%d", newValue)
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

            int i = Integer.parseInt(newText);
            valueProperty.setValue(i);

        } catch (NumberFormatException ignored) {

        }

    }

}
