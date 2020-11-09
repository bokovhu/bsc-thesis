package me.bokov.bsc.surfaceviewer.editor.surface;

import lombok.EqualsAndHashCode;
import me.bokov.bsc.surfaceviewer.Property;
import me.bokov.bsc.surfaceviewer.PropertyType;
import me.bokov.bsc.surfaceviewer.MeshSurface;
import me.bokov.bsc.surfaceviewer.editor.Icons;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.swing.*;
import java.io.Serializable;
import java.util.*;
import java.util.function.*;

import static me.bokov.bsc.surfaceviewer.sdf.Evaluetables.*;

@EqualsAndHashCode
public class ShapeSurface extends MeshSurface {

    private static final Property<Vector3f> P_TRANSF_POSITION = new Property<>(
            PropertyType.VEC3,
            "Transform",
            "Position",
            new Vector3f()
    );

    private static final Property<Vector3f> P_TRANSF_ROTATION_AXIS = new Property<>(
            PropertyType.VEC3,
            "Transform",
            "Rotation axis",
            new Vector3f(0f, 1f, 0f)
    );

    private static final Property<Float> P_TRANSF_ROTATION_ANGLE = new Property<>(
            PropertyType.FLOAT,
            "Transform",
            "Rotation angle",
            0.0f
    );

    private static final Property<Vector3f> P_TRANSF_SCALE = new Property<>(
            PropertyType.VEC3,
            "Transform",
            "Scale",
            new Vector3f(1f)
    );

    private static final Property<Vector3f> P_BOX_BOUNDS = new Property<>(
            PropertyType.VEC3,
            "Box",
            "Bounds",
            new Vector3f(1f)
    );

    private static final Property<Float> P_SPHERE_RADIUS = new Property<>(
            PropertyType.FLOAT,
            "Sphere",
            "Radius",
            1.0f
    );

    private static final Property<Float> P_CONE_ANGLE = new Property<>(
            PropertyType.FLOAT,
            "Cone",
            "Angle",
            30.0f
    );
    private static final Property<Float> P_CONE_HEIGHT = new Property<>(
            PropertyType.FLOAT,
            "Cone",
            "Height",
            1.0f
    );

    private final ShapeKind kind;
    private ShapeDescriptor shapeDescriptor = new ShapeDescriptor(new HashMap<>());

    public ShapeSurface(ShapeKind kind) {
        this.kind = kind;
    }

    private static Function<ShapeDescriptor, Evaluatable<Float, CPUContext, GPUContext>> shape(
            Function<ShapeDescriptor, Evaluatable<Float, CPUContext, GPUContext>> wrappedFactory
    ) {
        return desc -> rotate(
                new Quaternionf()
                        .fromAxisAngleDeg(
                                desc.get(P_TRANSF_ROTATION_AXIS),
                                desc.get(P_TRANSF_ROTATION_ANGLE)
                        ),
                scale(
                        desc.get(P_TRANSF_SCALE).x,
                        translate(
                                desc.get(P_TRANSF_POSITION),
                                wrappedFactory.apply(desc)
                        )
                )
        );
    }

    private static List<Property<?>> shapeProps(Property<?>... args) {
        List<Property<?>> result = new ArrayList<>();
        result.addAll(List.of(P_TRANSF_POSITION, P_TRANSF_ROTATION_AXIS, P_TRANSF_ROTATION_ANGLE, P_TRANSF_SCALE));
        for (var p : args) {
            result.add(p);
        }
        return result;
    }

    @Override
    public String getDisplayName() {
        return kind.name;
    }

    @Override
    public ImageIcon getImageIcon() {
        return kind.icon;
    }

    public List<Property<?>> getShapeProperties() {
        return kind.properties;
    }

    public ShapeDescriptor getShapeDescriptor() {
        return this.shapeDescriptor;
    }

    @Override
    public Evaluatable<Float, CPUContext, GPUContext> toEvaluatable() {
        return kind.factory.apply(this.shapeDescriptor);
    }

    public enum ShapeKind {
        BOX(
                "Box", Icons.FA_CUBES_SOLID_BLACK,
                shapeProps(P_BOX_BOUNDS),
                shape(desc -> box(desc.get(P_BOX_BOUNDS)))
        ),
        SPHERE(
                "Sphere",
                Icons.FA_CUBES_SOLID_BLACK,
                shapeProps(P_SPHERE_RADIUS),
                shape(desc -> sphere(desc.get(P_SPHERE_RADIUS)))
        ),
        CONE(
                "Cone",
                Icons.FA_CUBES_SOLID_BLACK,
                shapeProps(P_CONE_ANGLE, P_CONE_HEIGHT),
                shape(desc -> cone(desc.get(P_CONE_ANGLE), desc.get(P_CONE_HEIGHT)))
        );
        public final String name;
        public final ImageIcon icon;
        public final List<Property<?>> properties;
        public final Function<ShapeDescriptor, Evaluatable<Float, CPUContext, GPUContext>> factory;

        ShapeKind(
                String name,
                ImageIcon icon,
                List<Property<?>> properties,
                Function<ShapeDescriptor, Evaluatable<Float, CPUContext, GPUContext>> factory
        ) {
            this.name = name;
            this.icon = icon;
            this.properties = properties;
            this.factory = factory;
        }
    }

    @EqualsAndHashCode
    public static class ShapeDescriptor implements Serializable {

        private final Map<String, Object> properties;

        public ShapeDescriptor(Map<String, Object> properties) {
            this.properties = properties;
        }

        public <T extends Serializable> T get(String name) {
            return (T) properties.getOrDefault(name, null);
        }

        public <T extends Serializable> T get(String name, T defaultValue) {
            return (T) properties.getOrDefault(name, defaultValue);
        }

        public <T extends Serializable> T get(Property<T> prop) {
            return (T) properties.getOrDefault(prop.getName(), prop.getDefaultValue());
        }

        public void set(String name, Object value) {
            this.properties.put(name, value);
        }

    }

}
