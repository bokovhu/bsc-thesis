package me.bokov.bsc.surfaceviewer.editorv2.view.input;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;
import org.joml.Vector4f;

import java.net.URL;
import java.util.*;

public class Vec4Input extends HBox implements Initializable {

    @Getter
    private StringProperty labelProperty = new SimpleStringProperty();
    @Getter
    private ObjectProperty<Vector4f> valueProperty = new SimpleObjectProperty<>(new Vector4f());

    @FXML
    private Label inputLabel;

    @FXML
    private TextField xInputField;

    @FXML
    private TextField yInputField;

    @FXML
    private TextField zInputField;

    @FXML
    private TextField wInputField;

    public Vec4Input() {
        FXMLUtil.loadForComponent("/fxml/input/Vec4Input.fxml", this);
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
        zInputField.textProperty()
                .setValue(
                        String.format(Locale.ENGLISH, "%.4f", valueProperty.get().z)
                );
        wInputField.textProperty()
                .setValue(
                        String.format(Locale.ENGLISH, "%.4f", valueProperty.get().w)
                );

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        inputLabel.textProperty()
                .bind(labelProperty);

        xInputField.setOnAction(
                event -> onInputsChanged()
        );
        yInputField.setOnAction(
                event -> onInputsChanged()
        );
        zInputField.setOnAction(
                event -> onInputsChanged()
        );
        wInputField.setOnAction(
                event -> onInputsChanged()
        );

        displayValue();

        valueProperty.addListener(
                (observable, oldValue, newValue) -> displayValue()
        );

    }

    private void onInputsChanged() {

        try {

            float x = Float.parseFloat(xInputField.getText());
            float y = Float.parseFloat(yInputField.getText());
            float z = Float.parseFloat(zInputField.getText());
            float w = Float.parseFloat(wInputField.getText());
            valueProperty.setValue(new Vector4f(x, y, z, w));

        } catch (NumberFormatException ignored) {

        }

    }
}
