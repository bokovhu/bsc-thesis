package me.bokov.bsc.surfaceviewer.editorv2.view.input;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;

import java.net.URL;
import java.util.*;

public abstract class GLInput<TValue> extends HBox implements Initializable {

    @Getter
    protected final StringProperty labelProperty = new SimpleStringProperty();

    @FXML
    protected Label inputLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inputLabel.textProperty().bind(labelProperty);
    }

    protected abstract Property<TValue> getInternalValueProperty();

    public void setOnValueChangedListener(ChangeListener<TValue> listener) {

        getInternalValueProperty()
                .addListener(listener);

    }

    public abstract void collectValue();

}
