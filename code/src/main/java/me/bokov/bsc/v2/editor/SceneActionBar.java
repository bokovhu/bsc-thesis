package me.bokov.bsc.v2.editor;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.editor.action.NewSceneObjectAction;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class SceneActionBar extends JPanel {

    private final Editor editor;

    public SceneActionBar(Editor editor) {
        this.editor = editor;
    }

    public SceneActionBar create() {

        final var layout = new MigLayout("", "[grow]", "[shrink]");

        final var addBtn = new JButton(new NewSceneObjectAction(this.editor));
        add(addBtn);

        return this;

    }

}
