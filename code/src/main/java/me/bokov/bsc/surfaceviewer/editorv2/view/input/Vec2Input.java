package me.bokov.bsc.surfaceviewer.editorv2.view.input;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;
import org.joml.Vector2f;

import java.net.URL;
import java.util.*;

public class Vec2Input extends GLInput<Vector2f> implements Initializable {

    @Getter
    private ObjectProperty<Vector2f> valueProperty = new SimpleObjectProperty<>(new Vector2f());

    @FXML
    private TextField xInputField;

    @FXML
    private TextField yInputField;

    public Vec2Input() {
        FXMLUtil.loadForComponent("/fxml/input/Vec2Input.fxml", this);
    }

    private void displayValue() {

        xInputField.textProperty()
                .setValue(
                        String.format(Locale.ENGLISH, "%.4f", valueProperty.get().x)
                );
        yInputField.textProperty()
                .setValue(
                        String.format(Locale.ENGLISH, "%.4f", valueProperty.get().y)
                );

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        xInputField.setOnAction(
                event -> onInputsChanged()
        );
        yInputField.setOnAction(
                event -> onInputsChanged()
        );

        displayValue();

        valueProperty.addListener(
                (observable, oldValue, newValue) -> displayValue()
        );

    }

    @Override
    protected Property<Vector2f> getInternalValueProperty() {
        return valueProperty;
    }

    @Override
    public void collectValue() {
        onInputsChanged();
    }

    private void onInputsChanged() {

        try {

            float x = Float.parseFloat(xInputField.getText());
            float y = Float.parseFloat(yInputField.getText());
            valueProperty.setValue(new Vector2f(x, y));

        } catch (NumberFormatException ignored) {

        }

    }
}
