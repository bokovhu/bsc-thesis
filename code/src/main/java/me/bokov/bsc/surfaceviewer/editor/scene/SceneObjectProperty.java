package me.bokov.bsc.surfaceviewer.editor.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SceneObjectProperty<T> {

    private final PropertyType type;
    private final String name;
    private T value;
    private Supplier<T> valueSupplier = null;
    private List<Consumer<T>> changeListeners = new ArrayList<>();

    public SceneObjectProperty(
            PropertyType type,
            String name
    ) {
        this.type = type;
        this.name = name;
        this.value = null;
        this.valueSupplier = null;
    }

    public SceneObjectProperty(
            PropertyType type,
            String name,
            T defaultValue
    ) {
        this.type = type;
        this.name = name;
        this.value = defaultValue;
    }

    public SceneObjectProperty(
            PropertyType type,
            String name,
            T defaultValue,
            Supplier<T> valueSuppier
    ) {
        this.type = type;
        this.name = name;
        this.value = defaultValue;
        this.valueSupplier = valueSuppier;
    }

    public SceneObjectProperty<T> onChange(Consumer<T>... newValueConsumers) {
        for (Consumer<T> c : newValueConsumers) {
            changeListeners.add(c);
        }
        return this;
    }

    public SceneObjectProperty<T> set(T newValue) {
        this.value = newValue;
        this.changeListeners.forEach(c -> c.accept(newValue));
        return this;
    }

    public T get() {
        if (valueSupplier != null) {
            return valueSupplier.get();
        }
        return this.value;
    }

    public PropertyType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public enum PropertyType {
        Int, Float,
        Vec2, Vec3, Vec4
    }

}
