package me.bokov.bsc.surfaceviewer.editor.property;

import me.bokov.bsc.surfaceviewer.Property;
import net.miginfocom.swing.MigLayout;
import org.joml.Vector4f;

import javax.swing.*;
import java.text.DecimalFormat;

public class Vec4Input extends PropertyInput<Vector4f> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String GROUP = "Vec4Input";
    private JFormattedTextField xField, yField, zField, wField;

    public Vec4Input(Property<Vector4f> property) {
        super(property);
    }

    private Vector4f extractValue() {
        return new Vector4f(
                ((Number) xField.getValue()).floatValue(),
                ((Number) yField.getValue()).floatValue(),
                ((Number) zField.getValue()).floatValue(),
                ((Number) wField.getValue()).floatValue()
        );
    }

    @Override
    protected JComponent makeInput() {

        final JPanel container = new JPanel(
                new MigLayout("", "[grow][grow][grow][grow]", "[shrink]")
        );

        final Vector4f def = this.property.getDefaultValue() != null
                ? this.property.getDefaultValue()
                : new Vector4f(0f);

        xField = new JFormattedTextField(DECIMAL_FORMAT);
        yField = new JFormattedTextField(DECIMAL_FORMAT);
        zField = new JFormattedTextField(DECIMAL_FORMAT);
        wField = new JFormattedTextField(DECIMAL_FORMAT);

        xField.setValue(def.x);
        yField.setValue(def.y);
        zField.setValue(def.z);
        wField.setValue(def.w);

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
        this.wField.addPropertyChangeListener(
                "value",
                e -> this.setValue(extractValue())
        );

        container.add(xField, "grow");
        container.add(yField, "grow");
        container.add(zField, "grow");
        container.add(wField, "grow");

        return container;
    }
}
