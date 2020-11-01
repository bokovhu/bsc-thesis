package me.bokov.bsc.v2.editor.property;

import me.bokov.bsc.v2.Property;

import javax.swing.*;
import java.text.DecimalFormat;
import java.text.Format;

public abstract class FormattedTextFieldPropertyInput <T> extends PropertyInput<T> {

    protected JFormattedTextField field;

    public FormattedTextFieldPropertyInput(Property<T> property) {
        super(property);
    }

    @Override
    protected JComponent makeInput() {

        this.field = new JFormattedTextField(makeFormat());
        if (this.property.getDefaultValue() != null) {
            this.field.setValue(this.property.getDefaultValue());
        }
        this.field.setFocusLostBehavior(JFormattedTextField.COMMIT);

        this.field.addPropertyChangeListener(
                "value",
                e -> this.setValue(convertValue(this.field.getValue()))
        );

        return this.field;
    }

    protected abstract Format makeFormat();
    protected abstract <R> T convertValue(R value);

}
