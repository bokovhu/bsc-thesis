package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.render.Lighting;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.threed.OpSmoothSubtract;
import me.bokov.bsc.surfaceviewer.sdf.threed.SimpleNoise;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.*;

import static me.bokov.bsc.surfaceviewer.sdf.Evaluetables.*;

// Intentionally package-private
final class SceneManager {

    private static final Lighting DEFAULT_LIGHTING = new Lighting(
            new Vector3f(-1.8f, 2.0f, 1.4f).normalize(),
            new Vector3f(2.4f, 2.0f, 1.1f),
            new Vector3f(0.14f, 0.22f, 0.27f)
    );

    private static final Map<String, BiFunction<AppConfig, SurfaceViewerPlatform, AppScene>> SCENE_FACTORIES = Map
            .of(
                    "cube-minus-sphere", (c, p) -> new AppScene(
                            subtract(sphere(0.9f), box(new Vector3f(0.8f))),
                            DEFAULT_LIGHTING
                    ),
                    "rotated-cube", (c, p) -> new AppScene(
                            rotate(
                                    new Quaternionf()
                                            .fromAxisAngleDeg(
                                                    new Vector3f(1f, 1f, 1f).normalize(),
                                                    45f
                                            ),
                                    box(new Vector3f(1f))
                            ),
                            DEFAULT_LIGHTING
                    ),
                    "torus", (c, p) -> new AppScene(
                            torus(new Vector2f(1.5f, 0.3f)),
                            DEFAULT_LIGHTING
                    ),
                    "complicated", (c, p) -> new AppScene(
                            infiniteRepeat(
                                    new Vector3f(16f, 1.5f, 16f),
                                    subtract(
                                            union(
                                                    box(new Vector3f(4f, 0.8f, 0.25f)),
                                                    box(new Vector3f(0.25f, 0.8f, 4f)),
                                                    rotate(
                                                            new Quaternionf()
                                                                    .fromAxisAngleDeg(
                                                                            new Vector3f(
                                                                                    1f,
                                                                                    0f,
                                                                                    1f
                                                                            ).normalize(),
                                                                            90f
                                                                    ),
                                                            cylinder(8f, 0.2f)
                                                    ),
                                                    rotate(
                                                            new Quaternionf()
                                                                    .fromAxisAngleDeg(
                                                                            new Vector3f(
                                                                                    -1f,
                                                                                    0f,
                                                                                    1f
                                                                            ).normalize(),
                                                                            90f
                                                                    ),
                                                            cylinder(8f, 0.2f)
                                                    )
                                            ),
                                            torus(new Vector2f(1.5f, 0.4f))
                                    )
                            ),
                            DEFAULT_LIGHTING
                    ),
                    "cone", (c, p) -> new AppScene(
                            cone((float) Math.toRadians(20.0), 2.0f),
                            DEFAULT_LIGHTING
                    ),
                    "noise", (c, p) -> new AppScene(
                            gate(
                                    sphere(25.0f),
                                    Evaluatable.of(new SimpleNoise(-0.1f, 1.0f, -0.2f, 1f))
                            ),
                            DEFAULT_LIGHTING
                    ),
                    "noise-op", (c, p) -> new AppScene(
                            union(
                                    translate(
                                            new Vector3f(4.0f, 0.0f, 0.0f),
                                            Evaluatable.of(
                                                    new OpSmoothSubtract(
                                                            scale(
                                                                    0.3f,
                                                                    Evaluatable.of(new SimpleNoise(
                                                                            -0.4f, 1.0f, -0.2f, 1f))
                                                            ),
                                                            sphere(4.0f),
                                                            0.45f
                                                    )
                                            )
                                    ),
                                    translate(
                                            new Vector3f(-4.0f, 0.0f, 0.0f),
                                            Evaluatable.of(
                                                    new OpSmoothSubtract(
                                                            scale(
                                                                    0.3f,
                                                                    Evaluatable.of(new SimpleNoise(
                                                                            -0.4f, 1.0f, -0.2f, 1f))
                                                            ),
                                                            box(new Vector3f(2.0f, 1.0f, 4.0f)),
                                                            0.45f
                                                    )
                                            )
                                    )
                            ),
                            DEFAULT_LIGHTING
                    ),
                    "capped-cylinder", (c, p) -> new AppScene(
                            cylinder(8.0f, 1.0f),
                            DEFAULT_LIGHTING
                    )
            );

    private final SurfaceViewerPlatform platform;

    SceneManager(SurfaceViewerPlatform platform) {
        this.platform = platform;
    }

    public AppScene makeScene(String name) {
        return SCENE_FACTORIES.get(name).apply(platform.config(), platform);
    }

}
