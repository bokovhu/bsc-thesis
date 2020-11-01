package me.bokov.bsc.v2.editor.property;

import me.bokov.bsc.v2.Property;

import java.text.DecimalFormat;
import java.text.Format;

public class FloatInput extends FormattedTextFieldPropertyInput<Float> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    public FloatInput(Property<Float> property) {
        super(property);
    }

    @Override
    protected Format makeFormat() {
        return DECIMAL_FORMAT;
    }

    @Override
    protected <R> Float convertValue(R value) {
        return ((Number) value).floatValue();
    }
}
