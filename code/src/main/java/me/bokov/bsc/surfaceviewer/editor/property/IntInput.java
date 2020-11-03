package me.bokov.bsc.surfaceviewer.editor.property;

import me.bokov.bsc.surfaceviewer.Property;

import java.text.DecimalFormat;
import java.text.Format;

public class IntInput extends FormattedTextFieldPropertyInput<Integer> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");

    public IntInput(Property<Integer> property) {
        super(property);
    }

    @Override
    protected Format makeFormat() {
        return DECIMAL_FORMAT;
    }

    @Override
    protected <R> Integer convertValue(R value) {
        return ((Number) value).intValue();
    }
}