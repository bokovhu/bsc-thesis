package me.bokov.bsc.surfaceviewer.editorv2.service;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.surfacelang.SurfaceLangExpression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class LoadSceneTask extends Task<World> {

    @Getter
    private final StringProperty loadPathProperty = new SimpleStringProperty();

    @Override
    protected World call() throws Exception {

        final File file = new File(loadPathProperty.get());

        StringBuilder sb = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {

            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

        }

        final var expr = new SurfaceLangExpression();
        expr.parse(sb.toString());

        return expr.getWorld();
    }
}
