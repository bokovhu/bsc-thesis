package me.bokov.bsc.v2.editor.property;

import me.bokov.bsc.v2.Property;
import net.miginfocom.swing.MigLayout;
import org.joml.Vector2f;

import javax.swing.*;
import java.text.DecimalFormat;

public class Vec2Input extends PropertyInput<Vector2f> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String GROUP = "Vec2Input";
    private JFormattedTextField xField, yField;

    public Vec2Input(Property<Vector2f> property) {
        super(property);
    }

    private Vector2f extractValue() {
        return new Vector2f(
                ((Number) xField.getValue()).floatValue(),
                ((Number) yField.getValue()).floatValue()
        );
    }

    @Override
    protected JComponent makeInput() {

        final JPanel container = new JPanel(
                new MigLayout("", "[grow][grow]", "[shrink]")
        );

        final Vector2f def = this.property.getDefaultValue() != null
                ? this.property.getDefaultValue()
                : new Vector2f(0f);

        xField = new JFormattedTextField(DECIMAL_FORMAT);
        yField = new JFormattedTextField(DECIMAL_FORMAT);

        xField.setValue(def.x);
        yField.setValue(def.y);

        this.xField.addPropertyChangeListener(
                "value",
                e -> this.setValue(extractValue())
        );
        this.yField.addPropertyChangeListener(
                "value",
                e -> this.setValue(extractValue())
        );

        container.add(xField, "grow");
        container.add(yField, "grow");

        return container;
    }
}
