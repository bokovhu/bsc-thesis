package me.bokov.bsc.v2.editor.surface;

import lombok.Getter;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.v2.SceneMeshSurface;
import me.bokov.bsc.v2.editor.Icons;
import org.joml.Vector3f;

import javax.swing.*;
import java.io.Serializable;
import java.util.*;
import java.util.function.*;

import static me.bokov.bsc.surfaceviewer.sdf.Evaluetables.*;

public class ShapeSurface extends SceneMeshSurface {

    private static final ShapeProperty<Vector3f> P_BOX_BOUNDS = new ShapeProperty<>(
            ShapeProperty.ShapePropertyType.VEC3,
            "Bounds",
            new Vector3f(1f)
    );

    private static final ShapeProperty<Float> P_SPHERE_RADIUS = new ShapeProperty<>(
            ShapeProperty.ShapePropertyType.FLOAT,
            "Radius",
            1.0f
    );

    private ShapeDescriptor shapeDescriptor = new ShapeDescriptor(new HashMap<>());

    public ShapeSurface(ShapeKind kind) {
        this.kind = kind;
    }

    public enum ShapeKind {
        BOX(
                "Box", Icons.FA_CUBES_SOLID_BLACK,
                List.of(P_BOX_BOUNDS),
                desc -> box(desc.get(P_BOX_BOUNDS))
        ),
        SPHERE(
                "Sphere",
                Icons.FA_CUBES_SOLID_BLACK,
                List.of(P_SPHERE_RADIUS),
                desc -> sphere(desc.get(P_SPHERE_RADIUS))
        );
        public final String name;
        public final ImageIcon icon;
        public final List<ShapeProperty<?>> properties;
        public final Function<ShapeDescriptor, Evaluatable<Float, CPUContext, GPUContext>> factory;

        ShapeKind(
                String name,
                ImageIcon icon,
                List<ShapeProperty<?>> properties,
                Function<ShapeDescriptor, Evaluatable<Float, CPUContext, GPUContext>> factory
        ) {
            this.name = name;
            this.icon = icon;
            this.properties = properties;
            this.factory = factory;
        }
    }

    private final ShapeKind kind;

    @Override
    public String getDisplayName() {
        return kind.name;
    }

    @Override
    public ImageIcon getImageIcon() {
        return kind.icon;
    }

    public List<ShapeProperty<?>> getShapeProperties() {
        return kind.properties;
    }

    public ShapeDescriptor getShapeDescriptor() {
        return this.shapeDescriptor;
    }

    @Override
    public Evaluatable<Float, CPUContext, GPUContext> toEvaluatable() {
        return kind.factory.apply(this.shapeDescriptor);
    }

    @Getter
    public static class ShapeProperty<T> implements Serializable {

        public ShapeProperty(ShapePropertyType type, String name, T defaultValue) {
            this.type = type;
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public enum ShapePropertyType {
            FLOAT, INTEGER, VEC2, VEC3, VEC4
        }

        private final ShapePropertyType type;
        private final String name;
        private final T defaultValue;

    }

    public static class ShapeDescriptor implements Serializable {

        private final Map<String, Object> properties;

        public ShapeDescriptor(Map<String, Object> properties) {
            this.properties = properties;
        }

        public <T> T get(String name) {
            return (T) properties.getOrDefault(name, null);
        }

        public <T> T get(String name, T defaultValue) {
            return (T) properties.getOrDefault(name, defaultValue);
        }

        public <T> T get(ShapeProperty<T> prop) {
            return (T) properties.getOrDefault(prop.getName(), prop.getDefaultValue());
        }

        public void set(String name, Object value) {
            this.properties.put(name, value);
        }

    }

}
