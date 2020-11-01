package me.bokov.bsc.v2.editor.property;

import me.bokov.bsc.v2.Property;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.*;
import java.util.function.*;

public abstract class PropertyInput<T> extends JPanel {

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
