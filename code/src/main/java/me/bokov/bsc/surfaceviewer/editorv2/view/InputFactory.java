package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.scene.Parent;
import me.bokov.bsc.surfaceviewer.editorv2.view.input.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class InputFactory {

    private final Parent parent;

    public InputFactory(Parent parent) {
        this.parent = parent;
    }

    public FloatInput makeFloatInputFor(String label, float defaultValue) {

        var result = new FloatInput();
        result.getLabelProperty().setValue(label);
        result.getValueProperty().setValue(defaultValue);

        return result;

    }

    public IntInput makeIntInputFor(String label, int defaultValue) {

        var result = new IntInput();
        result.getLabelProperty().setValue(label);
        result.getValueProperty().setValue(defaultValue);

        return result;

    }

    public BoolInput makeBoolInputFor(String label, boolean defaultValue) {

        var result = new BoolInput();
        result.getLabelProperty().setValue(label);
        result.getValueProperty().setValue(defaultValue);

        return result;

    }

    public ChoiceInput makeChoiceInputFor(String label, Object defaultValue) {

        var result = new ChoiceInput();
        result.getLabelProperty().setValue(label);
        result.getValueProperty().setValue(defaultValue);

        return result;

    }

    public Vec2Input makeVec2InputFor(String label, Vector2f defaultValue) {

        var result = new Vec2Input();
        result.getLabelProperty().setValue(label);
        result.getValueProperty().setValue(defaultValue);

        return result;

    }

    public Vec3Input makeVec3InputFor(String label, Vector3f defaultValue) {

        var result = new Vec3Input();
        result.getLabelProperty().setValue(label);
        result.getValueProperty().setValue(defaultValue);

        return result;

    }

    public Vec4Input makeVec4InputFor(String label, Vector4f defaultValue) {

        var result = new Vec4Input();
        result.getLabelProperty().setValue(label);
        result.getValueProperty().setValue(defaultValue);

        return result;

    }

    public GLInput<?> makeInputComponentFor(String type, String label, Object defaultValue) {

        switch (type) {
            case "float":
                return makeFloatInputFor(label, ((Number) defaultValue).floatValue());
            case "int":
                return makeIntInputFor(label, ((Number) defaultValue).intValue());
            case "vec2":
                return makeVec2InputFor(label, (Vector2f) defaultValue);
            case "vec3":
                return makeVec3InputFor(label, (Vector3f) defaultValue);
            case "vec4":
                return makeVec4InputFor(label, (Vector4f) defaultValue);
            case "bool":
                return makeBoolInputFor(label, Boolean.TRUE.equals(defaultValue));
            case "choice":
                return makeChoiceInputFor(label, defaultValue);
            default :
                throw new UnsupportedOperationException("No way to create input for type " + type);
        }

    }

}
