package me.bokov.bsc.surfaceviewer.editorv2.service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.App;
import me.bokov.bsc.surfaceviewer.view.ViewConfiguration;

public class UpdateViewConfigurationTask extends Task<Boolean> {

    @Getter
    private final ObjectProperty<App> appProperty = new SimpleObjectProperty<>(null);

    @Getter
    private final ObjectProperty<ViewConfiguration> configurationProperty = new SimpleObjectProperty<>(null);

    @Override
    protected Boolean call() throws Exception {

        final var app = appProperty.get();
        final var config = configurationProperty.get();

        if (app == null || config == null) {
            throw new IllegalArgumentException("app and config cannot be null!");
        }

        app.getViewClient()
                .changeConfig(config);

        return true;
    }
}
