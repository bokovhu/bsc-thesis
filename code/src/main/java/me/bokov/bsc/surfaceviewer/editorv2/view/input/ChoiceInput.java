package me.bokov.bsc.surfaceviewer.editorv2.view.input;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;

import java.net.URL;
import java.util.*;
import java.util.function.*;

public class ChoiceInput extends HBox implements Initializable {

    @Getter
    private ObjectProperty<Object> valueProperty = new SimpleObjectProperty<>();

    @Getter
    private ObjectProperty<Function<Object, String>> choiceLabelerProperty = new SimpleObjectProperty<>(Objects::toString);

    @Getter
    private ObservableList<Object> items = FXCollections.observableArrayList();

    @Getter
    private StringProperty labelProperty = new SimpleStringProperty();

    @FXML
    private Label inputLabel;

    @FXML
    private VBox choicesVBox;

    public ChoiceInput() {
        FXMLUtil.loadForComponent("/fxml/input/ChoiceInput.fxml", this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        inputLabel.textProperty().bind(labelProperty);

        items.addListener(
                (ListChangeListener<? super Object>) (c -> onItemsChanged())
        );
        valueProperty.addListener(
                (observable, oldValue, newValue) -> onValueChanged(newValue)
        );

    }

    private void onValueChanged(Object newValue) {
        onItemsChanged();
    }

    private void onItemsChanged() {

        choicesVBox.getChildren().clear();

        for (Object item : items) {

            RadioButton choiceRadio = new RadioButton(
                    choiceLabelerProperty.get().apply(item)
            );
            choiceRadio.setSelected(
                    Objects.equals(valueProperty.get(), item)
            );
            choicesVBox.getChildren().add(choiceRadio);

            choiceRadio.setOnAction(
                    event -> {
                        if (choiceRadio.selectedProperty().get()) {
                            valueProperty.setValue(item);
                        }

                        onItemsChanged();
                    }
            );

        }

    }

}
