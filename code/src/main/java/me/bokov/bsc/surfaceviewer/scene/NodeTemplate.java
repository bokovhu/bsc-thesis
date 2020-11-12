package me.bokov.bsc.surfaceviewer.scene;

import lombok.Builder;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.threed.OpSmoothIntersect;
import me.bokov.bsc.surfaceviewer.sdf.threed.OpSmoothSubtract;
import me.bokov.bsc.surfaceviewer.sdf.threed.OpSmoothUnion;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static me.bokov.bsc.surfaceviewer.sdf.Evaluables.*;

public enum NodeTemplate {

    UNION(
            true,
            c -> union(
                    c.getChildren().stream().map(SceneNode::toEvaluable)
                            .collect(Collectors.toList())
            )
    ),
    SUBTRACT(
            true,
            c -> c.getPorts().containsKey("A") && c.getPorts().containsKey("B")
                    ? subtract(c.getPorts().get("A").toEvaluable(), c.getPorts().get("B").toEvaluable())
                    : null,
            List.of(
                    Port.builder().color("#40e077").name("A").build(),
                    Port.builder().color("#e07740").name("B").build()
            )
    ),
    INTERSECT(
            true,
            c -> intersect(c.getChildren().stream().map(SceneNode::toEvaluable).collect(Collectors.toList()))
    ),
    SMOOTH_UNION(
            List.of(
                    Property.builder().defaultValue(0.04f).type("float").name("k").build()
            ),
            true,
            c -> c.getPorts().containsKey("A") && c.getPorts().containsKey("B") ? Evaluable.of(
                    new OpSmoothUnion(
                            c.getPorts().get("A").toEvaluable(),
                            c.getPorts().get("B").toEvaluable(),
                            c.getFloatProperties().getOrDefault("k", 0.04f)
                    )
            ) : null,
            List.of(
                    Port.builder().name("A").color("#e07740").build(),
                    Port.builder().name("B").color("#40e077").build()
            )
    ),
    SMOOTH_SUBTRACT(
            List.of(
                    Property.builder().defaultValue(0.04f).type("float").name("k").build()
            ),
            true,
            c -> c.getPorts().containsKey("A") && c.getPorts().containsKey("B") ? Evaluable.of(
                    new OpSmoothSubtract(
                            c.getPorts().get("A").toEvaluable(),
                            c.getPorts().get("B").toEvaluable(),
                            c.getFloatProperties().getOrDefault("k", 0.04f)
                    )
            ) : null,
            List.of(
                    Port.builder().name("A").color("#e07740").build(),
                    Port.builder().name("B").color("#40e077").build()
            )
    ),
    SMOOTH_INTERSECT(
            List.of(
                    Property.builder().defaultValue(0.04f).type("float").name("k").build()
            ),
            true,
            c -> c.getPorts().containsKey("A") && c.getPorts().containsKey("B") ? Evaluable.of(
                    new OpSmoothIntersect(
                            c.getPorts().get("A").toEvaluable(),
                            c.getPorts().get("B").toEvaluable(),
                            c.getFloatProperties().getOrDefault("k", 0.04f)
                    )
            ) : null,
            List.of(
                    Port.builder().name("A").color("#e07740").build(),
                    Port.builder().name("B").color("#40e077").build()
            )
    ),
    BOX(
            List.of(
                    Property.builder().defaultValue(new Vector3f(1f)).type("vec3").name("bounds").build()
            ),
            false,
            c -> box(c.getVec3Properties().getOrDefault("bounds", new Vector3f(1f)))
    ),
    SPHERE(
            List.of(
                    Property.builder().defaultValue(1.0f).type("float").name("radius").build()
            ),
            false,
            c -> sphere(c.getFloatProperties().getOrDefault("radius", 1.0f))
    ),
    TORUS(
            List.of(
                    Property.builder().defaultValue(new Vector2f(1f, 0.2f)).type("vec2").name("radius").build()
            ),
            false,
            c -> torus(c.getVec2Properties().getOrDefault("radius", new Vector2f(1f, 0.2f)))
    ),
    CYLINDER(
            List.of(
                    Property.builder().defaultValue(1.0f).type("float").name("height").build(),
                    Property.builder().defaultValue(0.4f).type("float").name("radius").build()
            ),
            false,
            c -> cylinder(
                    c.getFloatProperties().getOrDefault("height", 1.0f),
                    c.getFloatProperties().getOrDefault("radius", 0.4f)
            )
    );
    public final boolean supportsChildren;
    public final Function<SurfaceFactoryRequest, Evaluable<Float, CPUContext, GPUContext>> factory;
    public final List<Port> ports;
    public final List<Property> properties;

    NodeTemplate(
            boolean supportsChildren,
            Function<SurfaceFactoryRequest, Evaluable<Float, CPUContext, GPUContext>> factory
    ) {
        this.supportsChildren = supportsChildren;
        this.factory = factory;
        this.ports = Collections.emptyList();
        this.properties = Collections.emptyList();
    }

    NodeTemplate(
            boolean supportsChildren,
            Function<SurfaceFactoryRequest, Evaluable<Float, CPUContext, GPUContext>> factory,
            List<Port> ports
    ) {
        this.supportsChildren = supportsChildren;
        this.factory = factory;
        this.ports = ports;
        this.properties = Collections.emptyList();
    }

    NodeTemplate(
            List<Property> properties,
            boolean supportsChildren,
            Function<SurfaceFactoryRequest, Evaluable<Float, CPUContext, GPUContext>> factory
    ) {
        this.supportsChildren = supportsChildren;
        this.factory = factory;
        this.properties = properties;
        this.ports = Collections.emptyList();
    }

    NodeTemplate(
            List<Property> properties,
            boolean supportsChildren,
            Function<SurfaceFactoryRequest, Evaluable<Float, CPUContext, GPUContext>> factory,
            List<Port> ports
    ) {
        this.supportsChildren = supportsChildren;
        this.factory = factory;
        this.properties = properties;
        this.ports = ports;
    }

    @Getter
    @Builder
    public static class Port implements Serializable {
        private String color;
        private String name;
    }

    @Getter
    @Builder
    public static class Property implements Serializable {
        private String type;
        private String name;
        private Object defaultValue;
    }

}
