package me.bokov.bsc.surfaceviewer.editorv2.service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.surfacelang.SurfaceLangExpression;
import me.bokov.bsc.surfaceviewer.surfacelang.SurfaceLangFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class SaveSceneTask extends Task<File> {

    @Getter
    private final ObjectProperty<World> worldProperty = new SimpleObjectProperty<>();

    @Getter
    private final StringProperty savePathProperty = new SimpleStringProperty();

    @Override
    protected File call() throws Exception {

        final File file = new File(savePathProperty.get());

        try (FileOutputStream fos = new FileOutputStream(file);
             PrintWriter pw = new PrintWriter(fos)) {

            final var formatter = new SurfaceLangFormatter(worldProperty.get());

            pw.write(formatter.format());

        }

        return file;
    }
}
