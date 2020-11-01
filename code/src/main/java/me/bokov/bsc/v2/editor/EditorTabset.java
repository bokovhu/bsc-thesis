package me.bokov.bsc.v2.editor;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.Installable;
import me.bokov.bsc.v2.editor.surface.ShapeSurface;
import me.bokov.bsc.v2.editor.tab.ShapeSettingsTab;
import me.bokov.bsc.v2.editor.tab.ViewSettingsTab;

import javax.swing.*;

public class EditorTabset extends JTabbedPane implements Installable<EditorLayout> {

    private Editor editor;

    private ViewSettingsTab viewSettingsTab = null;

    public EditorTabset() {
    }

    public Editor getEditor() {
        return editor;
    }

    @Override
    public void install(EditorLayout parent) {

        this.editor = parent.getEditor();

        this.viewSettingsTab = new ViewSettingsTab();
        this.viewSettingsTab.install(this);

    }

    public void openShapeSettingsTab(ShapeSurface shape) {

        for (int i = 0; i < getTabCount(); i++) {
            final var tab = getTabComponentAt(i);

            if (tab instanceof ShapeSettingsTab) {
                final var shapeTab = (ShapeSettingsTab) tab;
                if (shape.equals(shapeTab.getShapeSurface())) {
                    this.setSelectedIndex(i);
                    return;
                }
            }
        }

        final var newTab = new ShapeSettingsTab(shape);
        newTab.install(this);
        setSelectedIndex(getTabCount() - 1);

    }

    @Override
    public void uninstall() {

        if (this.viewSettingsTab != null) { this.viewSettingsTab.uninstall(); }

        removeAll();
        this.editor = null;

    }
}
