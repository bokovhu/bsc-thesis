package me.bokov.bsc.v2.editor.action;

import me.bokov.bsc.v2.Editor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.function.*;

// TODO: Implement
public class SaveViewConfigAction extends AbstractAction {

    private final Supplier<Map<String, Map<String, Object>>> config;
    private final Editor editor;

    public SaveViewConfigAction(Supplier<Map<String, Map<String, Object>>> config, Editor editor) {
        super("Save view configuration");
        this.config = config;
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        editor.app().sendConfigurationToView(config.get());

    }
}
