package me.bokov.bsc.v2.editor.property;

import me.bokov.bsc.v2.Property;

import javax.swing.*;

public class StringInput extends PropertyInput<String> {

    private JTextField field;

    public StringInput(Property<String> property) {
        super(property);
    }

    @Override
    protected JComponent makeInput() {

        this.field = new JTextField(this.property.getDefaultValue());

        this.field.addActionListener(
                e -> this.setValue(this.field.getText())
        );

        return this.field;
    }

}
