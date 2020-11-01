package me.bokov.bsc.v2.editor.tab;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.Installable;
import me.bokov.bsc.v2.editor.EditorTabset;
import me.bokov.bsc.v2.editor.action.UpdateSceneAction;
import me.bokov.bsc.v2.editor.property.PropertyInput;
import me.bokov.bsc.v2.editor.surface.ShapeSurface;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.*;

public class ShapeSettingsTab extends JPanel implements Installable<EditorTabset> {

    private final ShapeSurface shapeSurface;

    private Editor editor;
    private EditorTabset tabset;

    private List<PropertyInput<?>> propertyInputs = new ArrayList<>();

    public ShapeSettingsTab(ShapeSurface shapeSurface) {
        this.shapeSurface = shapeSurface;
    }

    public ShapeSurface getShapeSurface() {
        return shapeSurface;
    }

    @Override
    public void install(EditorTabset parent) {

        this.editor = parent.getEditor();
        this.tabset = parent;

        this.tabset.addTab(
                this.shapeSurface.getDisplayName(),
                this.shapeSurface.getImageIcon(),
                this
        );

        setLayout(new MigLayout("", "grow", "grow"));

        for (var prop : shapeSurface.getShapeProperties()) {

            final var input = PropertyInput.inputFor(prop);
            add(input, "grow, wrap");

            propertyInputs.add(input);

        }

        add(
                new JButton(
                        new UpdateSceneAction(
                                () -> this.propertyInputs.forEach(
                                        input -> shapeSurface.getShapeDescriptor()
                                                .set(input.getPropertyName(), input.getValue())
                                ),
                                this.editor
                        )
                ),
                "shrink, wrap"
        );

    }

    @Override
    public void uninstall() {

        removeAll();

        this.editor = null;
        this.tabset = null;

    }
}
