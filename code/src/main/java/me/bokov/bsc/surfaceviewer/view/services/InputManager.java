package me.bokov.bsc.surfaceviewer.view.services;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.*;

@Deprecated
public class InputManager {

    private final Map<KeyboardShortcut, List<Runnable>> keyboardShortcuts = new HashMap<>();
    private final Map<MouseShortcut, List<Runnable>> downShortcuts = new HashMap<>();
    private final Map<MouseShortcut, List<Runnable>> upShortcuts = new HashMap<>();
    private final Map<MouseShortcut, List<Consumer<Vector2f>>> dragShortcuts = new HashMap<>();
    private final Map<Integer, Boolean> keyStates = new HashMap<>();
    private final Map<Integer, Boolean> buttonStates = new HashMap<>();
    private final Vector2f mouseCoords = new Vector2f();
    private final Vector2f lastMouseCoords = new Vector2f();
    private final Vector2f mouseMovement = new Vector2f();
    private final Vector2f downMouseCoords = new Vector2f();
    private MouseShortcut lastDownMouseState = new MouseShortcut();

    public static KeyboardShortcut kbShiftPlus(int key) {
        return new KeyboardShortcut()
                .setMods(GLFW.GLFW_MOD_SHIFT)
                .setKeyCode(key);
    }

    public static KeyboardShortcut kbNoMods(int key) {
        return new KeyboardShortcut()
                .setMods(0)
                .setKeyCode(key);
    }

    public static KeyboardShortcut kbCtrlPlus(int key) {
        return new KeyboardShortcut()
                .setMods(GLFW.GLFW_MOD_CONTROL)
                .setKeyCode(key);
    }

    public static KeyboardShortcut kbAltPlus(int key) {
        return new KeyboardShortcut()
                .setMods(GLFW.GLFW_MOD_ALT)
                .setKeyCode(key);
    }

    public static KeyboardShortcut kbSuperPlus(int key) {
        return new KeyboardShortcut()
                .setMods(GLFW.GLFW_MOD_SUPER)
                .setKeyCode(key);
    }

    public static KeyboardShortcut kbCtrlShiftPlus(int key) {
        return new KeyboardShortcut()
                .setMods(GLFW.GLFW_MOD_SHIFT | GLFW.GLFW_MOD_CONTROL)
                .setKeyCode(key);
    }

    public static MouseShortcut mShiftPlus(int button) {
        return new MouseShortcut()
                .setMods(GLFW.GLFW_MOD_SHIFT)
                .setButton(button);
    }

    public static MouseShortcut mCtrlPlus(int button) {
        return new MouseShortcut()
                .setMods(GLFW.GLFW_MOD_CONTROL)
                .setButton(button);
    }

    public static MouseShortcut mAltPlus(int button) {
        return new MouseShortcut()
                .setMods(GLFW.GLFW_MOD_ALT)
                .setButton(button);
    }

    public static MouseShortcut mSuperPlus(int button) {
        return new MouseShortcut()
                .setMods(GLFW.GLFW_MOD_SUPER)
                .setButton(button);
    }

    public static MouseShortcut mCtrlShiftPlus(int button) {
        return new MouseShortcut()
                .setMods(GLFW.GLFW_MOD_SHIFT | GLFW.GLFW_MOD_CONTROL)
                .setButton(button);
    }

    public static MouseShortcut mNoMods(int button) {
        return new MouseShortcut()
                .setMods(0)
                .setButton(button);
    }

    public boolean onKeyDown(int key, int mods) {

        keyStates.put(key, true);
        return false;
    }

    public boolean onKeyUp(int key, int mods) {

        keyStates.put(key, false);

        KeyboardShortcut shortcut = new KeyboardShortcut()
                .setKeyCode(key)
                .setMods(mods & (GLFW.GLFW_MOD_SHIFT | GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_ALT
                        | GLFW.GLFW_MOD_SUPER));

        if (keyboardShortcuts.containsKey(shortcut)) {
            keyboardShortcuts.get(shortcut).forEach(Runnable::run);
            return true;
        }

        return false;
    }

    public final boolean onMouseDown(int button, int mods) {

        buttonStates.put(button, true);

        lastDownMouseState = new MouseShortcut()
                .setButton(button)
                .setMods(mods);
        if (downShortcuts.containsKey(lastDownMouseState)) {
            downShortcuts.get(lastDownMouseState).forEach(Runnable::run);
            return true;
        }

        downMouseCoords.set(mouseCoords);

        return false;
    }

    public final boolean onMouseUp(int button, int mods) {

        buttonStates.put(button, false);

        MouseShortcut shortcut = new MouseShortcut()
                .setMods(mods & (GLFW.GLFW_MOD_SHIFT | GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_ALT
                        | GLFW.GLFW_MOD_SUPER))
                .setButton(button);
        if (upShortcuts.containsKey(shortcut)) {
            upShortcuts.get(shortcut)
                    .forEach(Runnable::run);
        }

        return false;
    }

    public final boolean onMouseMove(float x, float y) {

        mouseMovement.set(x - lastMouseCoords.x, y - lastMouseCoords.y);
        lastMouseCoords.set(mouseCoords);
        mouseCoords.set(x, y);

        if (dragShortcuts.containsKey(lastDownMouseState) && isButtonPressed(
                lastDownMouseState.button)) {
            dragShortcuts.get(lastDownMouseState)
                    .forEach(c -> c.accept(mouseMovement));
        }

        return false;
    }

    public InputManager shortcut(KeyboardShortcut shortcut, Runnable action) {
        keyboardShortcuts.computeIfAbsent(shortcut, key -> new ArrayList<>())
                .add(action);
        return this;
    }

    public InputManager mouseUp(MouseShortcut shortcut, Runnable action) {
        upShortcuts.computeIfAbsent(shortcut, key -> new ArrayList<>())
                .add(action);
        return this;
    }

    public InputManager mouseDown(MouseShortcut shortcut, Runnable action) {
        downShortcuts.computeIfAbsent(shortcut, key -> new ArrayList<>())
                .add(action);
        return this;
    }

    public InputManager drag(MouseShortcut shortcut, Consumer<Vector2f> action) {
        dragShortcuts.computeIfAbsent(shortcut, key -> new ArrayList<>())
                .add(action);
        return this;
    }

    public boolean isKeyDown(int key) {
        return keyStates.getOrDefault(key, false);
    }

    public boolean isButtonPressed(int button) {
        return buttonStates.getOrDefault(button, false);
    }

    public Vector2f M() {
        return mouseCoords;
    }

    public Vector2f lastM() {
        return lastMouseCoords;
    }

    public Vector2f dM() {
        return mouseMovement;
    }

    @Data
    @Accessors(chain = true)
    public static class KeyboardShortcut {

        private int keyCode;
        private int mods;

    }

    @Data
    @Accessors(chain = true)
    public static class MouseShortcut {

        private int button;
        private int mods;

    }

}
