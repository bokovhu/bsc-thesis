package me.bokov.bsc.v2.editor;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.Installable;
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

    @Override
    public void uninstall() {

        if (this.viewSettingsTab != null) { this.viewSettingsTab.uninstall(); }

        removeAll();
        this.editor = null;

    }
}
