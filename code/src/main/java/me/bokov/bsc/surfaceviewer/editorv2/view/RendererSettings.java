package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.App;
import me.bokov.bsc.surfaceviewer.editorv2.service.UpdateViewConfigurationTask;
import me.bokov.bsc.surfaceviewer.editorv2.service.UpdateViewTask;
import me.bokov.bsc.surfaceviewer.editorv2.view.input.ChoiceInput;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;
import me.bokov.bsc.surfaceviewer.view.ViewConfiguration;
import me.bokov.bsc.surfaceviewer.view.renderer.RendererType;

import java.net.URL;
import java.util.*;

public class RendererSettings extends VBox implements Initializable {

    @Getter
    private StringProperty rendererTypeProperty = new SimpleStringProperty(RendererType.UniformGridMarchingCubes.name());

    @Getter
    private ObjectProperty<App> appProperty = new SimpleObjectProperty<>(null);

    @FXML
    private ChoiceInput rendererChoiceInput;

    public RendererSettings() {
        FXMLUtil.loadForComponent("/fxml/RendererSettings.fxml", this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for(RendererType rendererType : RendererType.values()) {
            rendererChoiceInput.getItems()
                    .add(rendererType.name());
        }

        rendererChoiceInput.getLabelProperty()
                .setValue("Renderer type");

        rendererChoiceInput.getValueProperty()
                .addListener(
                        (observable, oldValue, newValue) -> onRendererTypeChanged(Objects.toString(newValue))
                );

    }

    private void onRendererTypeChanged(String newRendererName) {

        RendererType type = RendererType.valueOf(newRendererName);

        ViewConfiguration newConfig = ViewConfiguration.builder()
                .rendererType(type).build();

        UpdateViewConfigurationTask task = new UpdateViewConfigurationTask();
        task.getConfigurationProperty().setValue(newConfig);
        task.getAppProperty().bind(appProperty);

        task.run();

    }

}
