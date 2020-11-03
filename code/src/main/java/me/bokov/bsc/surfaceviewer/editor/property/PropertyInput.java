package me.bokov.bsc.surfaceviewer.editor.property;

import me.bokov.bsc.surfaceviewer.Property;
import net.miginfocom.swing.MigLayout;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.swing.*;
import java.io.Serializable;

// TODO: Value need not be saved in the input component instance, instead an initialValue
//      may be supplied to override the prop's default value, extractValue() and changeValue()
//      functions are required
public abstract class PropertyInput<T extends Serializable> extends JPanel {

    protected final Property<T> property;
    protected T value;

    public PropertyInput(Property<T> property) {
        this.property = property;

        final var layout = new MigLayout("", "[shrink][grow]", "[shrink]");
        setLayout(layout);

        final var label = new JLabel(property.getName());
        add(label, "shrink");
        add(makeInput(), "grow");

        if (this.property.getDefaultValue() != null) {
            this.setValue(this.property.getDefaultValue());
        }

    }

    public static PropertyInput<?> inputFor(Property<? extends Serializable> prop) {
        switch (prop.getType()) {
            case INT:
                return new IntInput((Property<Integer>) prop);
            case FLOAT:
                return new FloatInput((Property<Float>) prop);
            case BOOLEAN:
                return new BooleanInput((Property<Boolean>) prop);
            case STRING:
                return new StringInput((Property<String>) prop);
            case VEC2:
                return new Vec2Input((Property<Vector2f>) prop);
            case VEC3:
                return new Vec3Input((Property<Vector3f>) prop);
            case VEC4:
                return new Vec4Input((Property<Vector4f>) prop);
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getPropertyName() {
        return property.getName();
    }

    protected abstract JComponent makeInput();

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}