package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.editor.EditorEventBus;
import me.bokov.bsc.surfaceviewer.editor.EditorLayout;
import me.bokov.bsc.surfaceviewer.editor.EditorMenubar;
import me.bokov.bsc.surfaceviewer.editor.event.EditorSceneChanged;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class Editor implements Runnable {

    private final App app;

    private Scene scene = new Scene();

    private EditorMenubar editorMenubar = null;
    private JFrame editorFrame = null;
    private EditorLayout editorLayout = null;
    private EditorEventBus editorEventBus = null;

    public Editor(App app) {
        this.app = app;
    }

    public JFrame getEditorFrame() {
        return this.editorFrame;
    }

    public EditorEventBus getEventBus() {
        return this.editorEventBus;
    }

    public EditorLayout getEditorLayout() {
        return this.editorLayout;
    }

    public EditorMenubar getEditorMenubar() {
        return editorMenubar;
    }

    public synchronized void applySceneChanges() {

        if (this.scene != null) {
            editorEventBus.fire(EditorSceneChanged.class);
            this.app.sendSceneToView(
                    Scene.cloneScene(this.scene)
            );
        }

    }

    public App app() {
        return app;
    }

    private void createEditorFrame() {

        this.editorEventBus = new EditorEventBus();
        this.editorEventBus.install(this);

        this.editorFrame = new JFrame("Editor");

        this.editorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.editorFrame.setResizable(true);

        this.editorFrame.setLayout(new MigLayout("", "[grow]", "[grow]"));

        this.editorLayout = new EditorLayout();
        this.editorLayout.install(this);

        this.editorMenubar = new EditorMenubar();
        this.editorMenubar.install(this);

        this.editorFrame.pack();
        this.editorFrame.setVisible(true);

    }

    @Override
    public void run() {

        createEditorFrame();

        this.editorFrame.setVisible(true);

    }

    public void close() {

        if (editorLayout != null) { this.editorLayout.uninstall(); }

        if (editorMenubar != null) { this.editorMenubar.uninstall(); }

        if (editorEventBus != null) { this.editorEventBus.uninstall(); }

        if (editorFrame != null) {
            this.editorFrame.setVisible(false);
            this.editorFrame.dispose();
        }

    }

    public Scene getScene() {
        return scene;
    }

}
