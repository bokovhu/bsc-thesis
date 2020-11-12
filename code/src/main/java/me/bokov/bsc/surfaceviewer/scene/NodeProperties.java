package me.bokov.bsc.surfaceviewer.scene;

import org.joml.*;

import java.io.Serializable;
import java.util.*;

public class NodeProperties implements Serializable {

    private Map<String, Float> floatProperties = new HashMap<>();
    private Map<String, Vector2f> vec2Properties = new HashMap<>();
    private Map<String, Vector3f> vec3Properties = new HashMap<>();
    private Map<String, Vector4f> vec4Properties = new HashMap<>();
    private Map<String, Matrix2f> mat2Properties = new HashMap<>();
    private Map<String, Matrix3f> mat3Properties = new HashMap<>();
    private Map<String, Matrix4f> mat4Properties = new HashMap<>();
    private Map<String, Integer> intProperties = new HashMap<>();
    private Map<String, Boolean> booleanProperties = new HashMap<>();

    private void removeProperty(String type, String name) {

        switch (type) {
            case "float":
                floatProperties.remove(name);
                break;
            case "vec2":
                vec2Properties.remove(name);
                break;
            case "vec3":
                vec3Properties.remove(name);
                break;
            case "vec4":
                vec4Properties.remove(name);
                break;
            case "mat2":
                mat2Properties.remove(name);
                break;
            case "mat3":
                mat3Properties.remove(name);
                break;
            case "mat4":
                mat4Properties.remove(name);
                break;
            case "int":
                intProperties.remove(name);
                break;
            case "bool":
                booleanProperties.remove(name);
                break;
            default:
                throw new UnsupportedOperationException("Unknown property type: " + type);
        }

    }

    private void putProperty(String type, String name, Object value) {

        switch (type) {
            case "float":
                floatProperties.put(name, ((Number) value).floatValue());
                break;
            case "vec2":
                vec2Properties.put(name, (Vector2f) value);
                break;
            case "vec3":
                vec3Properties.put(name, (Vector3f) value);
                break;
            case "vec4":
                vec4Properties.put(name, (Vector4f) value);
                break;
            case "mat2":
                mat2Properties.put(name, (Matrix2f) value);
                break;
            case "mat3":
                mat3Properties.put(name, (Matrix3f) value);
                break;
            case "mat4":
                mat4Properties.put(name, (Matrix4f) value);
                break;
            case "int":
                intProperties.put(name, ((Number) value).intValue());
                break;
            case "bool":
                booleanProperties.put(name, Boolean.TRUE.equals(value));
                break;
            default:
                throw new UnsupportedOperationException("Unknown property type: " + type);
        }

    }

    public void include(NodeTemplate.Property propertyTemplate, Object value) {

        if (value == null) {
            removeProperty(propertyTemplate.getType(), propertyTemplate.getName());
        } else {
            putProperty(propertyTemplate.getType(), propertyTemplate.getName(), value);
        }

    }

    public void exclude(NodeTemplate.Property propertyTemplate) {
        removeProperty(propertyTemplate.getType(), propertyTemplate.getName());
    }

    public void apply(SurfaceFactoryRequest.SurfaceFactoryRequestBuilder builder) {
        builder.floatProperties(new HashMap<>(floatProperties))
                .vec2Properties(new HashMap<>(vec2Properties))
                .vec3Properties(new HashMap<>(vec3Properties))
                .vec4Properties(new HashMap<>(vec4Properties))
                .mat2Properties(new HashMap<>(mat2Properties))
                .mat3Properties(new HashMap<>(mat3Properties))
                .mat4Properties(new HashMap<>(mat4Properties))
                .intProperties(new HashMap<>(intProperties))
                .booleanProperties(new HashMap<>(booleanProperties));
    }

    public float getFloat(String name) {
        return floatProperties.getOrDefault(name, 0.0f);
    }

    public float getFloat(String name, float defaultValue) {
        return floatProperties.getOrDefault(name, defaultValue);
    }

    public Vector2f getVec2(String name) {
        return vec2Properties.getOrDefault(name, new Vector2f(0.0f));
    }

    public Vector2f getVec2(String name, Vector2f defaultValue) {
        return vec2Properties.getOrDefault(name, defaultValue);
    }


    public Vector3f getVec3(String name) {
        return vec3Properties.getOrDefault(name, new Vector3f(0.0f));
    }

    public Vector3f getVec3(String name, Vector3f defaultValue) {
        return vec3Properties.getOrDefault(name, defaultValue);
    }


    public Vector4f getVec4(String name) {
        return vec4Properties.getOrDefault(name, new Vector4f(0.0f));
    }

    public Vector4f getVec4(String name, Vector4f defaultValue) {
        return vec4Properties.getOrDefault(name, defaultValue);
    }


    public int getInt(String name) {
        return intProperties.getOrDefault(name, 0);
    }

    public int getInt(String name, int defaultValue) {
        return intProperties.getOrDefault(name, defaultValue);
    }


    public boolean getBool(String name) {
        return booleanProperties.getOrDefault(name, false);
    }

    public boolean getBool(String name, boolean defaultValue) {
        return booleanProperties.getOrDefault(name, defaultValue);
    }


    public Matrix2f getMat2(String name) {
        return mat2Properties.getOrDefault(name, new Matrix2f());
    }

    public Matrix2f getMat2(String name, Matrix2f defaultValue) {
        return mat2Properties.getOrDefault(name, defaultValue);
    }


    public Matrix3f getMat3(String name) {
        return mat3Properties.getOrDefault(name, new Matrix3f());
    }

    public Matrix3f getMat3(String name, Matrix3f defaultValue) {
        return mat3Properties.getOrDefault(name, defaultValue);
    }


    public Matrix4f getMat4(String name) {
        return mat4Properties.getOrDefault(name, new Matrix4f());
    }

    public Matrix4f getMat4(String name, Matrix4f defaultValue) {
        return mat4Properties.getOrDefault(name, defaultValue);
    }

    public void copyFrom(NodeProperties other) {

        floatProperties.clear();
        vec2Properties.clear();
        vec3Properties.clear();
        vec4Properties.clear();
        mat2Properties.clear();
        mat3Properties.clear();
        mat4Properties.clear();
        intProperties.clear();
        booleanProperties.clear();


        floatProperties.putAll(other.floatProperties);
        vec2Properties.putAll(other.vec2Properties);
        vec3Properties.putAll(other.vec3Properties);
        vec4Properties.putAll(other.vec4Properties);
        mat2Properties.putAll(other.mat2Properties);
        mat3Properties.putAll(other.mat3Properties);
        mat4Properties.putAll(other.mat4Properties);
        intProperties.putAll(other.intProperties);
        booleanProperties.putAll(other.booleanProperties);

    }

}
