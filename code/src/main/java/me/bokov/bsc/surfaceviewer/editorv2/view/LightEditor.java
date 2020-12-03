package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.editorv2.view.input.ChoiceInput;
import me.bokov.bsc.surfaceviewer.editorv2.view.input.Vec3Input;
import me.bokov.bsc.surfaceviewer.scene.AmbientLight;
import me.bokov.bsc.surfaceviewer.scene.DirectionalLight;
import me.bokov.bsc.surfaceviewer.scene.LightSource;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;
import me.bokov.bsc.surfaceviewer.util.IOUtil;

import java.net.URL;
import java.util.*;

public class LightEditor extends VBox implements Initializable {

    @Getter
    private ObjectProperty<LightSource> lightSourceProperty = new SimpleObjectProperty<>();

    @Getter
    private ObjectProperty<World> worldProperty = new SimpleObjectProperty<>();

    @FXML
    private ChoiceInput lightTypeChoiceInput;

    @FXML
    private Vec3Input energyInput;

    @FXML
    private Vec3Input directionInput;

    public LightEditor() {
        FXMLUtil.loadForComponent("/fxml/LightEditor.fxml", this);
    }

    private void onLightSourceChanged() {

        lightTypeChoiceInput.collectValue();
        energyInput.collectValue();
        directionInput.collectValue();

        final var world = IOUtil.serialize(worldProperty.get());
        if (world != null) {
            final var lsIfExists = world.getLightSources()
                    .stream()
                    .filter(l -> l.getId() == lightSourceProperty.get().getId())
                    .findFirst();

            if (lsIfExists.isPresent()) {

                final var ls = lsIfExists.get();

                if (ls instanceof DirectionalLight) {
                    final var dls = (DirectionalLight) ls;
                    dls.setEnergy(energyInput.getValueProperty().get());
                    dls.dir(directionInput.getValueProperty().get());
                } else if (ls instanceof AmbientLight) {
                    final var als = (AmbientLight) ls;
                    als.setEnergy(energyInput.getValueProperty().get());
                }

                worldProperty.set(world);

            }

        }

    }

    private void onLightSourceChanged(LightSource newValue) {
        if (newValue instanceof DirectionalLight) {
            lightTypeChoiceInput.getValueProperty()
                    .setValue("Directional");
            final var ls = (DirectionalLight) newValue;
            energyInput.getValueProperty()
                    .setValue(ls.getEnergy());
            directionInput.getValueProperty()
                    .setValue(ls.getDirection());
            directionInput.visibleProperty()
                    .set(true);
        } else if (newValue instanceof AmbientLight) {
            lightTypeChoiceInput.getValueProperty()
                    .setValue("Ambient");
            final var ls = (AmbientLight) newValue;
            energyInput.getValueProperty()
                    .setValue(ls.getEnergy());
            directionInput.visibleProperty()
                    .set(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lightTypeChoiceInput.getItems()
                .add("Directional");
        lightTypeChoiceInput.getItems()
                .add("Ambient");
        lightTypeChoiceInput.getLabelProperty()
                .set("Light type");


        energyInput.getLabelProperty().set("Energy (RGB)");
        directionInput.getLabelProperty().set("Direction");

        lightSourceProperty.addListener(
                (observable, oldValue, newValue) -> {
                    onLightSourceChanged(newValue);
                }
        );

        lightTypeChoiceInput.setOnValueChangedListener(
                (observable, oldValue, newValue) -> onLightSourceChanged()
        );
        energyInput.setOnValueChangedListener(
                (observable, oldValue, newValue) -> onLightSourceChanged()
        );
        directionInput.setOnValueChangedListener(
                (observable, oldValue, newValue) -> onLightSourceChanged()
        );

    }
}
