package me.bokov.bsc.v2.editor.property;

import me.bokov.bsc.v2.Property;
import net.miginfocom.swing.MigLayout;
import org.joml.Vector3f;

import javax.swing.*;
import java.text.DecimalFormat;

public class Vec3Input extends PropertyInput<Vector3f> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private JFormattedTextField xField, yField, zField;

    private static final String GROUP = "Vec3Input";

    public Vec3Input(Property<Vector3f> property) {
        super(property);
    }

    private Vector3f extractValue() {
        return new Vector3f(
                ((Number) xField.getValue()).floatValue(),
                ((Number) yField.getValue()).floatValue(),
                ((Number) zField.getValue()).floatValue()
        );
    }

    @Override
    protected JComponent makeInput() {

        final JPanel container = new JPanel(
                new MigLayout("", "[grow][grow][grow]", "[shrink]")
        );

        final Vector3f def = this.property.getDefaultValue() != null
                ? this.property.getDefaultValue()
                : new Vector3f(0f);

        xField = new JFormattedTextField(DECIMAL_FORMAT);
        yField = new JFormattedTextField(DECIMAL_FORMAT);
        zField = new JFormattedTextField(DECIMAL_FORMAT);

        xField.setValue(def.x);
        yField.setValue(def.y);
        zField.setValue(def.z);

        this.xField.addPropertyChangeListener(
                "value",
                e -> this.setValue(extractValue())
        );
        this.yField.addPropertyChangeListener(
                "value",
                e -> this.setValue(extractValue())
        );
        this.zField.addPropertyChangeListener(
                "value",
                e -> this.setValue(extractValue())
        );

        container.add(xField, "grow");
        container.add(yField, "grow");
        container.add(zField, "grow");

        return container;
    }

}
