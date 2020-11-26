package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.App;
import me.bokov.bsc.surfaceviewer.editorv2.service.UpdateViewConfigurationTask;
import me.bokov.bsc.surfaceviewer.editorv2.view.input.GLInput;
import me.bokov.bsc.surfaceviewer.util.IOUtil;
import me.bokov.bsc.surfaceviewer.view.RendererConfig;
import me.bokov.bsc.surfaceviewer.view.ViewConfiguration;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class RendererProperties extends VBox {

    private final InputFactory inputFactory = new InputFactory(this);
    @Getter
    private ObjectProperty<App> appProperty = new SimpleObjectProperty<>();

    private ObjectProperty<Consumer<RendererConfig>> changeHandler = new SimpleObjectProperty<>(this::onFieldValuesChanged);

    private List<FieldHandler> fieldHandlers = new ArrayList<>();

    public RendererProperties() {

        setAlignment(Pos.CENTER_LEFT);

    }


    private void onFieldValuesChanged(RendererConfig sendConfig) {

        UpdateViewConfigurationTask task = new UpdateViewConfigurationTask();

        task.getAppProperty().bind(appProperty);
        task.getConfigurationProperty().setValue(
                ViewConfiguration.builder()
                        .rendererConfig(IOUtil.serialize(sendConfig))
                        .build()
        );

        task.run();

    }

    private List<Field> collectFields(RendererConfig config) {

        return Arrays.stream(config.getClass()
                .getDeclaredFields())
                .filter(
                        f -> Number.class.isAssignableFrom(f.getType())
                                || Vector2f.class.isAssignableFrom(f.getType())
                                || Vector3f.class.isAssignableFrom(f.getType())
                                || Vector4f.class.isAssignableFrom(f.getType())
                                || Boolean.class.isAssignableFrom(f.getType())
                )
                .collect(Collectors.toList());

    }

    public void populate(RendererConfig config) {

        getChildren().clear();
        fieldHandlers.clear();

        List<Field> configFields = collectFields(config);
        for (Field f : configFields) {

            FieldHandler fh = new FieldHandler(f, config);
            fieldHandlers.add(fh);

        }

    }

    public void save(RendererConfig config) {

        for (FieldHandler fh : fieldHandlers) {
            fh.inputField.collectValue();
        }

        changeHandler.get().accept(config);

    }

    private class FieldHandler {

        private final Field field;
        private final RendererConfig config;
        private GLInput<?> inputField;

        private FieldHandler(Field field, RendererConfig config) {
            this.field = field;
            this.config = config;
            this.field.setAccessible(true);
            try {
                this.inputField = inputFactory.makeInputComponentFor(
                        fieldType(field),
                        field.getName(),
                        field.get(config)
                );
                this.inputField.setOnValueChangedListener(
                        (observable, oldValue, newValue) -> {
                            setFieldValue(newValue);
                            save(config);
                        }
                );
                getChildren().add(this.inputField);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        private String fieldType(Field f) {
            if (float.class.isAssignableFrom(f.getType()) || Float.class.isAssignableFrom(f.getType())) {
                return "float";
            } else if (int.class.isAssignableFrom(f.getType()) || Integer.class.isAssignableFrom(f.getType())) {
                return "int";
            } else if (boolean.class.isAssignableFrom(f.getType()) || Boolean.class.isAssignableFrom(f.getType())) {
                return "bool";
            } else if (Vector2f.class.isAssignableFrom(f.getType())) {
                return "vec2";
            } else if (Vector3f.class.isAssignableFrom(f.getType())) {
                return "vec3";
            } else if (Vector4f.class.isAssignableFrom(f.getType())) {
                return "vec4";
            }
            return "choice";
        }

        private void setFieldValue(Object newValue) {
            try {
                field.set(config, newValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

}
