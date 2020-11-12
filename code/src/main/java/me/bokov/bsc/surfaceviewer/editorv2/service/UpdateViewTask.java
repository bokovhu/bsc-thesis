package me.bokov.bsc.surfaceviewer.editorv2.service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.App;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.view.ViewConfiguration;

public class UpdateViewTask extends Task<Boolean> {

    @Getter
    private final ObjectProperty<World> newWorldProperty = new SimpleObjectProperty<>(null);

    @Getter
    private final ObjectProperty<App> appProperty = new SimpleObjectProperty<>(null);

    @Override
    protected Boolean call() throws Exception {

        final var app = appProperty.get();
        final var world = newWorldProperty.get();

        if (app == null || world == null) {
            throw new IllegalArgumentException("app and world cannot be null!");
        }

        app.getViewClient()
                .changeConfig(
                        ViewConfiguration.builder()
                                .world(world)
                                .build()
                );

        return true;

    }
}
