package me.bokov.bsc.surfaceviewer.editor;

import me.bokov.bsc.surfaceviewer.Editor;
import me.bokov.bsc.surfaceviewer.Installable;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class EditorLayout extends JPanel implements Installable<Editor> {

    private Editor editor;

    private JSplitPane mainSplitPane;

    private SceneBrowser sceneBrowser;
    private EditorTabset editorTabset;

    public EditorLayout() {
    }

    public Editor getEditor() {
        return editor;
    }

    public SceneBrowser getSceneBrowser() {
        return sceneBrowser;
    }

    public EditorTabset getEditorTabset() {
        return editorTabset;
    }

    @Override
    public void install(Editor parent) {

        this.editor = parent;

        final var layout = new MigLayout("", "[grow, grow]", "[grow]");
        setLayout(layout);

        this.sceneBrowser = new SceneBrowser();
        this.sceneBrowser.install(this);

        this.editorTabset = new EditorTabset();
        this.editorTabset.install(this);

        this.mainSplitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                this.sceneBrowser, this.editorTabset
        );

        add(this.mainSplitPane, "push, grow");

        this.editor.getEditorFrame()
                .add(this, "grow");

    }

    @Override
    public void uninstall() {

        this.editor = null;
        this.removeAll();

    }
}
