package me.bokov.bsc.surfaceviewer.view;

import me.bokov.bsc.surfaceviewer.View;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class CameraManager {

    public static final float MOUSE_SENTITIVITY = 0.01f;
    private static final InputManager.MouseShortcut SHORTCUT_ROTATE = InputManager
            .mNoMods(GLFW.GLFW_MOUSE_BUTTON_3);
    private static final InputManager.MouseShortcut SHORTCUT_ZOOM = InputManager
            .mCtrlShiftPlus(GLFW.GLFW_MOUSE_BUTTON_3);
    private static final InputManager.MouseShortcut SHORTCUT_PAN = InputManager
            .mShiftPlus(GLFW.GLFW_MOUSE_BUTTON_3);
    private final Vector3f origin = new Vector3f(0f, 0f, 0f);
    private final Vector3f rotationStartPoint = new Vector3f();
    private final Vector3f rotationStartUp = new Vector3f();
    private final Vector3f zoomStartPoint = new Vector3f();
    private final Vector3f panStartPoint = new Vector3f();
    private final Vector2f panDistance = new Vector2f();
    private final Vector3f panStartOrigin = new Vector3f();
    private final float lastMouseX = 0.0f;
    private final float lastMouseY = 0.0f;
    private final Vector3f tmpNewEye = new Vector3f();
    private final Vector3f tmpDir = new Vector3f();
    private final Vector3f tmpNewTarget = new Vector3f();
    private final Vector3f tmpPanMovement = new Vector3f();
    private final Vector3f tmpNewOrigin = new Vector3f();
    private final Quaternionf tmpRotation = new Quaternionf();
    private View view = null;
    private State state = State.Identity;
    private float rotationYaw = 0.0f;
    private float rotationPitch = 0.0f;
    private float zoomDistance = 0.0f;

    public void install(View parent) {

        this.view = parent;
        this.view.getInputManager()
                .mouseUp(
                        SHORTCUT_ROTATE,
                        () -> this.state = CameraManager.State.Identity
                )
                .mouseUp(
                        SHORTCUT_ZOOM,
                        () -> this.state = CameraManager.State.Identity
                )
                .mouseUp(
                        SHORTCUT_PAN,
                        () -> this.state = CameraManager.State.Identity
                )
                .mouseDown(
                        SHORTCUT_ROTATE,
                        () -> {
                            this.state = CameraManager.State.Rotating;
                            this.rotationStartPoint.set(view.getCamera().eye());
                            this.rotationPitch = 0.0f;
                            this.rotationYaw = 0.0f;
                            this.rotationStartUp.set(view.getCamera().up());
                        }
                )
                .mouseDown(
                        SHORTCUT_ZOOM,
                        () -> {
                            this.state = CameraManager.State.Zooming;
                            this.zoomDistance = 0.0f;
                            this.zoomStartPoint.set(view.getCamera().eye());
                        }
                )
                .mouseDown(
                        SHORTCUT_PAN,
                        () -> {
                            this.state = CameraManager.State.Panning;
                            this.panDistance.set(0f, 0f);
                            this.panStartPoint.set(view.getCamera().eye());
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

    public void update() {

        if (this.view == null || this.view.getCamera() == null) { return; }

        final var camera = this.view.getCamera();

        if (state == State.Zooming) {

            tmpNewEye.set(zoomStartPoint).add(
                    tmpDir.set(camera.forward()).mul(zoomDistance)
            );
            camera.lookAt(
                    tmpNewEye,
                    tmpNewTarget.set(tmpNewEye).add(camera.forward()),
                    camera.up()
            );

        }
        if (state == State.Rotating) {

            tmpRotation.identity()
                    .rotateY(rotationYaw)
                    .rotateX(rotationPitch);
            tmpNewEye.set(rotationStartPoint)
                    .rotate(tmpRotation);
            camera.lookAt(
                    tmpNewEye,
                    origin,
                    camera.up()
            );

        } else if (state == State.Panning) {

            tmpPanMovement.set(0f)
                    .add(tmpDir.set(camera.right()).mul(panDistance.x))
                    .add(tmpDir.set(camera.right()).cross(camera.forward()).mul(panDistance.y));
            tmpNewEye.set(panStartPoint).add(tmpPanMovement);
            origin.set(tmpNewOrigin.set(panStartOrigin).add(tmpPanMovement));

            camera.lookAt(
                    tmpNewEye,
                    tmpNewTarget.set(tmpNewEye).add(camera.forward()),
                    camera.up()
            );

        }

        camera.update();

    }

    public void uninstall() {

        this.view = null;

    }

    public enum State {
        Identity,
        Rotating,
        Panning,
        Zooming
    }
}
