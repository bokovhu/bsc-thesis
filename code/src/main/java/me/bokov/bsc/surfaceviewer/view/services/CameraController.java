package me.bokov.bsc.surfaceviewer.view.services;

import me.bokov.bsc.surfaceviewer.SurfaceViewerPlatform;
import me.bokov.bsc.surfaceviewer.render.Camera;
import me.bokov.bsc.surfaceviewer.view.AppView;
import me.bokov.bsc.surfaceviewer.view.services.InputManager.MouseShortcut;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class CameraController {

    public static final float MOUSE_SENTITIVITY = 0.01f;
    private static final MouseShortcut SHORTCUT_ROTATE = InputManager
            .mNoMods(GLFW.GLFW_MOUSE_BUTTON_3);
    private static final MouseShortcut SHORTCUT_ZOOM = InputManager
            .mCtrlShiftPlus(GLFW.GLFW_MOUSE_BUTTON_3);
    private static final MouseShortcut SHORTCUT_PAN = InputManager
            .mShiftPlus(GLFW.GLFW_MOUSE_BUTTON_3);
    private final AppView view;
    private final Camera camera;
    private final SurfaceViewerPlatform platform;
    private State state = State.Identity;
    private Vector3f orbitPosition = new Vector3f();
    private Vector3f orbitTarget = new Vector3f();
    private float orbitRadius = 6.0f;
    private float orbitTimescale = 0.4f;
    private float orbitTimer = 0.0f;
    private Vector3f orbitUp = new Vector3f(0f, 1f, 0f);
    private Vector3f origin = new Vector3f(0f, 0f, 0f);
    private Vector3f rotationStartPoint = new Vector3f();
    private Vector3f rotationStartUp = new Vector3f();
    private float rotationYaw = 0.0f;
    private float rotationPitch = 0.0f;
    private Vector3f zoomStartPoint = new Vector3f();
    private float zoomDistance = 0.0f;
    private Vector3f panStartPoint = new Vector3f();
    private Vector2f panDistance = new Vector2f();
    private Vector3f panStartOrigin = new Vector3f();
    private float lastMouseX = 0.0f;
    private float lastMouseY = 0.0f;


    public CameraController(AppView view, Camera camera,
            SurfaceViewerPlatform platform
    ) {
        this.view = view;
        this.camera = camera;
        this.platform = platform;

        camera.lookAt(new Vector3f(6f, 6f, 6f), origin, new Vector3f(0f, 1f, 0f));
        camera.update();

        this.view.inputManager()
                .shortcut(
                        InputManager.kbShiftPlus(GLFW.GLFW_KEY_F),
                        () -> this.state = this.state == State.Orbiting
                                ? State.Identity
                                : State.Orbiting
                )
                .mouseUp(
                        SHORTCUT_ROTATE,
                        () -> this.state = State.Identity
                )
                .mouseUp(
                        SHORTCUT_ZOOM,
                        () -> this.state = State.Identity
                )
                .mouseUp(
                        SHORTCUT_PAN,
                        () -> this.state = State.Identity
                )
                .mouseDown(
                        SHORTCUT_ROTATE,
                        () -> {
                            this.state = State.Rotating;
                            this.rotationStartPoint.set(camera.eye());
                            this.rotationPitch = 0.0f;
                            this.rotationYaw = 0.0f;
                            this.rotationStartUp.set(camera.up());
                        }
                )
                .mouseDown(
                        SHORTCUT_ZOOM,
                        () -> {
                            this.state = State.Zooming;
                            this.zoomDistance = 0.0f;
                            this.zoomStartPoint.set(camera.eye());
                        }
                )
                .mouseDown(
                        SHORTCUT_PAN,
                        () -> {
                            this.state = State.Panning;
                            this.panDistance.set(0f, 0f);
                            this.panStartPoint.set(camera.eye());
                            this.panStartOrigin.set(origin);
                        }
                )
                .drag(
                        SHORTCUT_ROTATE,
                        d -> {
                            this.rotationPitch += -d.y * MOUSE_SENTITIVITY;
                            this.rotationYaw += -d.x * MOUSE_SENTITIVITY;
                        }
                )
                .drag(
                        SHORTCUT_PAN,
                        d -> {
                            this.panDistance.add(
                                    -d.x * MOUSE_SENTITIVITY, d.y * MOUSE_SENTITIVITY
                            );
                        }
                ).drag(
                SHORTCUT_ZOOM,
                d -> {
                    this.zoomDistance -= d.y * MOUSE_SENTITIVITY;
                }
        );
    }

    public void update(float delta) {

        if (state == State.Orbiting) {

            orbitTimer += delta;

            camera.lookAt(
                    orbitPosition.set(
                            (float) Math.cos(orbitTimer * orbitTimescale) * orbitRadius,
                            4.0f,
                            (float) Math.sin(orbitTimer * orbitTimescale) * orbitRadius
                    ),
                    orbitTarget,
                    orbitUp
            );

        } else if (state == State.Zooming) {

            Vector3f newEye = new Vector3f(zoomStartPoint).add(
                    new Vector3f(camera.forward()).mul(zoomDistance)
            );
            camera.lookAt(
                    newEye,
                    new Vector3f(newEye).add(camera.forward()),
                    camera.up()
            );

        } else if (state == State.Rotating) {

            Quaternionf rotation = new Quaternionf()
                    .rotateY(rotationYaw)
                    .rotateX(rotationPitch);
            Vector3f newEye = new Vector3f(rotationStartPoint)
                    .rotate(rotation);
            camera.lookAt(
                    newEye,
                    origin,
                    camera.up()
            );

        } else if (state == State.Panning) {

            Vector3f panMovement = new Vector3f()
                    .add(new Vector3f(camera.right()).mul(panDistance.x))
                    .add(new Vector3f(camera.right()).cross(camera.forward()).mul(panDistance.y));
            Vector3f newEye = new Vector3f(panStartPoint).add(panMovement);
            origin.set(new Vector3f(panStartOrigin).add(panMovement));

            camera.lookAt(
                    newEye,
                    new Vector3f(newEye).add(camera.forward()),
                    camera.up()
            );

        }

        camera.update();

    }

    public enum State {
        Identity,
        Orbiting,
        Rotating,
        Panning,
        Zooming
    }

}
