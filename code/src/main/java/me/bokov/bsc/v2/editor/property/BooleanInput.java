package me.bokov.bsc.v2.editor.property;

import me.bokov.bsc.v2.Property;

import javax.swing.*;

import static java.lang.Boolean.*;

public class BooleanInput extends PropertyInput<Boolean> {

    private JCheckBox checkBox;

    public BooleanInput(Property<Boolean> property) {
        super(property);
    }

    @Override
    protected JComponent makeInput() {

        checkBox = new JCheckBox("Enabled", TRUE.equals(this.property.getDefaultValue()));

        return checkBox;
    }
}
