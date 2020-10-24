package me.bokov.bsc.surfaceviewer;

import java.util.Map;
import java.util.function.BiFunction;
import me.bokov.bsc.surfaceviewer.render.Lighting;
import me.bokov.bsc.surfaceviewer.sdf.SDFAxisAlignedBox;
import me.bokov.bsc.surfaceviewer.sdf.SDFCappedCylinder;
import me.bokov.bsc.surfaceviewer.sdf.SDFCone;
import me.bokov.bsc.surfaceviewer.sdf.SDFInifiniteRepetition;
import me.bokov.bsc.surfaceviewer.sdf.SDFOpSubtract;
import me.bokov.bsc.surfaceviewer.sdf.SDFOpUnion;
import me.bokov.bsc.surfaceviewer.sdf.SDFRotate;
import me.bokov.bsc.surfaceviewer.sdf.SDFSphere;
import me.bokov.bsc.surfaceviewer.sdf.SDFTorus;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

// Intentionally package-private
final class SceneManager {

    private static final Lighting DEFAULT_LIGHTING = new Lighting(
            new Vector3f(-1.8f, 2.0f, 1.4f).normalize(),
            new Vector3f(0.75f, 1.1f, 1.4f),
            new Vector3f(0.14f, 0.22f, 0.27f)
    );

    private static final Map<String, BiFunction<AppConfig, SurfaceViewerPlatform, AppScene>> SCENE_FACTORIES = Map
            .of(
                    "cube-minus-sphere", (c, p) -> new AppScene(
                            new SDFOpSubtract(
                                    new SDFSphere(0.9f),
                                    new SDFAxisAlignedBox(
                                            new Vector3f(0.8f, 0.8f, 0.8f)
                                    )
                            ),
                            DEFAULT_LIGHTING
                    ),
                    "rotated-cube", (c, p) -> new AppScene(
                            new SDFRotate(
                                    new Quaternionf()
                                            .fromAxisAngleDeg(
                                                    new Vector3f(1f, 1f, 1f).normalize(),
                                                    45f
                                            ),
                                    new SDFAxisAlignedBox(
                                            new Vector3f(1f, 1f, 1f)
                                    )
                            ),
                            DEFAULT_LIGHTING
                    ),
                    "torus", (c, p) -> new AppScene(
                            new SDFTorus(new Vector2f(1.5f, 0.3f)),
                            DEFAULT_LIGHTING
                    ),
                    "complicated", (c, p) -> new AppScene(
                            new SDFInifiniteRepetition(
                                    new Vector3f(16f, 1.5f, 16f),
                                    new SDFOpSubtract(
                                            new SDFOpUnion(
                                                    new SDFAxisAlignedBox(
                                                            new Vector3f(4f, 0.8f, 0.25f)),
                                                    new SDFOpUnion(
                                                            new SDFAxisAlignedBox(
                                                                    new Vector3f(0.25f, 0.8f, 4f)),
                                                            new SDFOpUnion(
                                                                    new SDFRotate(
                                                                            new Quaternionf()
                                                                                    .fromAxisAngleDeg(
                                                                                            new Vector3f(
                                                                                                    1f,
                                                                                                    0f,
                                                                                                    1f
                                                                                            ).normalize(),
                                                                                            90f
                                                                                    ),
                                                                            new SDFCappedCylinder(
                                                                                    8f, 0.2f)
                                                                    ),
                                                                    new SDFRotate(
                                                                            new Quaternionf()
                                                                                    .fromAxisAngleDeg(
                                                                                            new Vector3f(
                                                                                                    -1f,
                                                                                                    0f,
                                                                                                    1f
                                                                                            ).normalize(),
                                                                                            90f
                                                                                    ),
                                                                            new SDFCappedCylinder(
                                                                                    8f, 0.2f)
                                                                    )
                                                            )
                                                    )
                                            ),
                                            new SDFTorus(new Vector2f(1.5f, 0.4f))
                                    )
                            ),
                            DEFAULT_LIGHTING
                    ),
                    "cone", (c, p) -> new AppScene(
                            new SDFCone((float) Math.toRadians(20.0), 2.0f),
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
