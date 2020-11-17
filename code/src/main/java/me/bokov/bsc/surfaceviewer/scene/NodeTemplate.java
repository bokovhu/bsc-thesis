package me.bokov.bsc.surfaceviewer.scene;

import lombok.Builder;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.threed.*;
import me.bokov.bsc.surfaceviewer.sdf.twod.Box2D;
import me.bokov.bsc.surfaceviewer.sdf.twod.Disk2D;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static me.bokov.bsc.surfaceviewer.sdf.Evaluables.*;

public enum NodeTemplate {

    EVERYWHERE(
            false,
            c -> Evaluable.of(new Everywhere())
    ),
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
    GATE(
            List.of(),
            true,
            c -> c.getPorts().containsKey("Boundary") && c.getPorts().containsKey("Generator") ? Evaluable.of(
                    new OpGate(
                            c.getPorts().get("Boundary").toEvaluable(),
                            c.getPorts().get("Generator").toEvaluable()
                    )
            ) : null,
            List.of(
                    Port.builder().name("Boundary").color("#e07740").build(),
                    Port.builder().name("Generator").color("#40e077").build()
            )
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
    ONION(
            List.of(
                    Property.builder().defaultValue(0.1f).type("float").name("radius").build()
            ),
            true,
            c -> c.getPorts().containsKey("Generator") ? Evaluable.of(
                    new OpOnion(
                            c.getFloatProperties().getOrDefault("radius", 0.1f),
                            c.getPorts().get("Generator").toEvaluable()
                    )
            ) : null,
            List.of(
                    Port.builder().name("Generator").color("#4090e0").build()
            )
    ),
    ROUND(
            List.of(
                    Property.builder().defaultValue(0.1f).type("float").name("radius").build()
            ),
            true,
            c -> c.getPorts().containsKey("Generator") ? Evaluable.of(
                    new OpRound(
                            c.getFloatProperties().getOrDefault("radius", 0.1f),
                            c.getPorts().get("Generator").toEvaluable()
                    )
            ) : null,
            List.of(
                    Port.builder().name("Generator").color("#4090e0").build()
            )
    ),
    EXTRUDE(
            List.of(
                    Property.builder().defaultValue(1.0f).type("float").name("depth").build()
            ),
            true,
            c -> c.getPorts().containsKey("Generator") ? Evaluable.of(
                    new OpExtrude(
                            c.getFloatProperties().getOrDefault("depth", 1.0f),
                            c.getPorts().get("Generator").toEvaluable()
                    )
            ) : null,
            List.of(
                    Port.builder().name("Generator").color("#4090e0").build()
            )
    ),
    SYMX(
            true,
            c -> c.getPorts().containsKey("Generator") ? Evaluable.of(
                    new OpSymX(c.getPorts().get("Generator").toEvaluable())
            ) : null,
            List.of(
                    Port.builder().name("Generator").color("#000000").build()
            )
    ),
    SYMY(
            true,
            c -> c.getPorts().containsKey("Generator") ? Evaluable.of(
                    new OpSymY(c.getPorts().get("Generator").toEvaluable())
            ) : null,
            List.of(
                    Port.builder().name("Generator").color("#000000").build()
            )
    ),
    SYMZ(
            true,
            c -> c.getPorts().containsKey("Generator") ? Evaluable.of(
                    new OpSymZ(c.getPorts().get("Generator").toEvaluable())
            ) : null,
            List.of(
                    Port.builder().name("Generator").color("#000000").build()
            )
    ),
    ARRAY(
            List.of(
                    Property.builder().type("int").name("countX").defaultValue(1).build(),
                    Property.builder().type("int").name("countY").defaultValue(1).build(),
                    Property.builder().type("int").name("countZ").defaultValue(1).build(),
                    Property.builder().type("vec3").name("offset").defaultValue(new Vector3f(1f)).build()
            ),
            true,
            c -> (c.getPorts().containsKey("Generator"))
                    ? Evaluable.of(new OpArray(
                    c.getIntProperties().getOrDefault("countX", 1),
                    c.getIntProperties().getOrDefault("countY", 1),
                    c.getIntProperties().getOrDefault("countZ", 1),
                    c.getVec3Properties().getOrDefault("offset", new Vector3f(1f)),
                    c.getPorts().get("Generator").toEvaluable()
            ))
                    : null,
            List.of(
                    Port.builder().name("Generator").color("#000000").build()
            )
    ),
    RADIAL_ARRAY(
            List.of(
                    Property.builder().type("int").name("count").defaultValue(4).build(),
                    Property.builder().type("float").name("fromAngleDeg").defaultValue(0.0f).build(),
                    Property.builder().type("float").name("toAngleDeg").defaultValue(360.0f).build(),
                    Property.builder().type("vec3").name("axis").defaultValue(new Vector3f(0f, 0f, 1f)).build()
            ),
            true,
            c -> (c.getPorts().containsKey("Generator"))
                    ? Evaluable.of(new OpRadialArray(
                    c.getIntProperties().getOrDefault("count", 4),
                    c.getFloatProperties().getOrDefault("fromAngleDeg", 0.0f),
                    c.getFloatProperties().getOrDefault("toAngleDeg", 360.0f),
                    c.getVec3Properties().getOrDefault("axis", new Vector3f(0f, 0f, 1f)),
                    c.getPorts().get("Generator").toEvaluable()
            ))
                    : null,
            List.of(
                    Port.builder().name("Generator").color("#000000").build()
            )
    ),
    INFINITE_REPEAT(
            List.of(
                    Property.builder().type("vec3").name("period").defaultValue(new Vector3f(1f)).build()
            ),
            true,
            c -> c.getPorts().containsKey("Generator") ? Evaluable.of(
                    new OpInifiniteRepetition(
                            c.getVec3Properties().getOrDefault("period", new Vector3f(1f)),
                            c.getPorts().get("Generator").toEvaluable()
                    )
            ) : null,
            List.of(
                    Port.builder().name("Generator").color("#000000").build()
            )
    ),
    PLANE(
            List.of(
                    Property.builder().defaultValue(new Vector3f(0f, 1f, 0f)).type("vec3").name("normal").build(),
                    Property.builder().defaultValue(1.0f).type("float").name("h").build()
            ),
            false,
            c -> Evaluable.of(
                    new Plane(
                            c.getVec3Properties().getOrDefault("normal", new Vector3f(0f, 1f, 0f)),
                            c.getFloatProperties().getOrDefault("h", 1.0f)
                    )
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
    ),
    CONE(
            List.of(
                    Property.builder().defaultValue((float) Math.toRadians(60.0)).type("float").name("angle").build(),
                    Property.builder().defaultValue(1.0f).type("float").name("height").build()
            ),
            false,
            c -> cone(
                    c.getFloatProperties().getOrDefault("angle", (float) Math.toRadians(60.0)),
                    c.getFloatProperties().getOrDefault("height", 1.0f)
            )
    ),
    CAPPED_CONE(
            List.of(
                    Property.builder().defaultValue(new Vector3f(0f, 0f, 0f)).type("vec3").name("a").build(),
                    Property.builder().defaultValue(new Vector3f(0f, 1f, 0f)).type("vec3").name("b").build(),
                    Property.builder().defaultValue(0.5f).type("float").name("ra").build(),
                    Property.builder().defaultValue(0.25f).type("float").name("rb").build()
            ),
            false,
            c -> Evaluable.of(
                    new CappedCone(
                            c.getVec3Properties().getOrDefault("a", new Vector3f(0f, 0f, 0f)),
                            c.getFloatProperties().getOrDefault("ra", 0.5f),
                            c.getVec3Properties().getOrDefault("b", new Vector3f(0f, 1f, 0f)),
                            c.getFloatProperties().getOrDefault("rb", 0.25f)
                    )
            )
    ),
    BOX2D(
            List.of(
                    Property.builder().defaultValue(new Vector2f(1f)).type("vec2").name("bounds").build()
            ),
            false,
            c -> Evaluable.of(new Box2D(c.getVec2Properties().getOrDefault("bounds", new Vector2f(1f))))
    ),
    DISK2D(
            List.of(
                    Property.builder().defaultValue(1f).type("float").name("radius").build()
            ),
            false,
            c -> Evaluable.of(new Disk2D(c.getFloatProperties().getOrDefault("radius", 1f)))
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
