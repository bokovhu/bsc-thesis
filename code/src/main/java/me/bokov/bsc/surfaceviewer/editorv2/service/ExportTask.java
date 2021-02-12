package me.bokov.bsc.surfaceviewer.editorv2.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.App;
import me.bokov.bsc.surfaceviewer.editorv2.view.ExportConfigDialog;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.Drawables;
import me.bokov.bsc.surfaceviewer.scene.World;
import org.joml.Vector3f;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class ExportTask extends Task<File> {

    @Getter
    private final ObjectProperty<App> appProperty = new SimpleObjectProperty<>();
    @Getter
    private final ObjectProperty<World> worldProperty = new SimpleObjectProperty<>();

    @Getter
    private final BooleanProperty useMarchingCubesProperty = new SimpleBooleanProperty(true);

    @Override
    protected File call() throws Exception {

        Dialog<ExportConfig> configDialog = new Dialog<>();
        ExportConfigDialog exportDialog = new ExportConfigDialog();

        configDialog.getDialogPane()
                .setContent(exportDialog);

        configDialog.getDialogPane()
                .getButtonTypes()
                .add(ButtonType.CANCEL);
        configDialog.getDialogPane()
                .getButtonTypes()
                .add(ButtonType.FINISH);

        configDialog.setResultConverter(
                param -> {
                    if (param == ButtonType.FINISH) {
                        return exportDialog.toExportConfig();
                    }
                    return null;
                }
        );

        final Optional<ExportConfig> config = configDialog.showAndWait();

        if (config.isEmpty()) {
            failed();
            return null;
        }

        ExportType exportType = config.get().getOutputPath().endsWith("obj")
                ? ExportType.OBJ
                : config.get().getOutputPath().endsWith("gltf")
                ? ExportType.GLTF
                : null;

        if (exportType == null) {
            failed();
            return null;
        }

        List<Drawables.Face> faceList = new ArrayList<>();

        if (useMarchingCubesProperty.get()) {

            final var marchingCubesTask = new ExportMarchingCubesTask();
            marchingCubesTask.getGridWidthProperty().setValue(config.get().getGridWidth());
            marchingCubesTask.getGridHeightProperty().setValue(config.get().getGridHeight());
            marchingCubesTask.getGridDepthProperty().setValue(config.get().getGridDepth());
            marchingCubesTask.getGridOffsetProperty().setValue(config.get().getGridOffset());
            marchingCubesTask.getGridScaleProperty().setValue(config.get().getGridScale());
            marchingCubesTask.getWorldProperty().setValue(worldProperty.get());

            marchingCubesTask.run();

            faceList.addAll(marchingCubesTask.get());

        } else {

            final var dualContouringTask = new ExportDualContouringTask();
            dualContouringTask.getGridWidthProperty().setValue(config.get().getGridWidth());
            dualContouringTask.getGridHeightProperty().setValue(config.get().getGridHeight());
            dualContouringTask.getGridDepthProperty().setValue(config.get().getGridDepth());
            dualContouringTask.getGridOffsetProperty().setValue(config.get().getGridOffset());
            dualContouringTask.getGridScaleProperty().setValue(config.get().getGridScale());
            dualContouringTask.getWorldProperty().setValue(worldProperty.get());

            dualContouringTask.run();

            faceList.addAll(dualContouringTask.get());

        }

        switch (exportType) {
            case OBJ:

                final var objTask = new ExportOBJTask();
                objTask.getOutputPathProperty().setValue(config.get().getOutputPath());
                objTask.getFlipNormalsProperty().setValue(true);
                objTask.getTriangleListProperty().setValue(faceList);

                objTask.run();

                return objTask.get();

            case GLTF:

                final var gltfTask = new ExportGLTFTask();
                gltfTask.getOutputPathProperty().setValue(config.get().getOutputPath());
                gltfTask.getFlipNormalsProperty().setValue(true);
                gltfTask.getTriangleListProperty().setValue(faceList);

                gltfTask.run();

                return gltfTask.get();

        }

        return null;
    }

    enum ExportType {
        OBJ,
        GLTF
    }

    @Data
    @Accessors(chain = true)
    public static class ExportConfig implements Serializable {

        private int gridWidth = 64;
        private int gridHeight = 64;
        private int gridDepth = 64;
        private Vector3f gridOffset = new Vector3f(-4f, -4f, -4f);
        private float gridScale = 8f;
        private String outputPath;

    }

}
