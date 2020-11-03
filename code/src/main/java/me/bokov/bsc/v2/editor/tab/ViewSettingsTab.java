package me.bokov.bsc.v2.editor.tab;

import me.bokov.bsc.v2.Editor;
import me.bokov.bsc.v2.Installable;
import me.bokov.bsc.v2.Property;
import me.bokov.bsc.v2.editor.EditorTabset;
import me.bokov.bsc.v2.editor.action.SaveViewConfigAction;
import me.bokov.bsc.v2.editor.event.RendererInitialized;
import me.bokov.bsc.v2.editor.event.ViewInitialized;
import me.bokov.bsc.v2.editor.property.PropertyInput;
import me.bokov.bsc.v2.view.renderer.RendererType;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.*;

import static java.util.stream.Collectors.*;

public class ViewSettingsTab extends JPanel implements Installable<EditorTabset> {

    private Editor editor = null;
    private EditorTabset tabset = null;

    private JComboBox<RendererType> rendererComboBox;

    private JPanel propertyGroupsContainer = null;
    private JComponent propertyGroupsScroll = null;
    private List<PropertyGroupPanel> propertyGroupPanels = new ArrayList<>();
    private JButton saveButton = null;

    private void buildPropertyGroupsContainer() {

        if (propertyGroupsContainer != null) {
            propertyGroupsContainer.removeAll();
            propertyGroupPanels.clear();
        } else {
            propertyGroupsContainer = new JPanel(new MigLayout("", "[grow]", "[grow]"));
        }


        final var viewProperties = this.editor.app()
                .retrieveViewConfigurableProperties();
        final var viewConfig = this.editor.app()
                .retrieveCurrentViewConfiguration();

        final Map<String, List<Property<?>>> propertyGroups = new HashMap<>();

        for (var prop : viewProperties) {
            propertyGroups.computeIfAbsent(prop.getGroup(), key -> new ArrayList<>())
                    .add(prop);
        }

        propertyGroups.forEach(
                (groupName, properties) -> {

                    propertyGroupPanels.add(new PropertyGroupPanel(groupName, properties, viewConfig.getOrDefault(groupName, Collections.emptyMap())));

                }
        );

        propertyGroupPanels.forEach(
                panel -> propertyGroupsContainer.add(panel, "grow, wrap")
        );

        tabset.revalidate();
        tabset.repaint();


    }

    @Override
    public void install(EditorTabset parent) {

        this.editor = parent.getEditor();
        this.tabset = parent;

        this.tabset.addTab("View settings", this);

        final var layout = new MigLayout("", "[grow]", "[shrink][grow]");
        setLayout(layout);

        this.rendererComboBox = new JComboBox<>(RendererType.values());
        this.rendererComboBox.addActionListener(
                e -> {
                    final var newRenderer = (RendererType) this.rendererComboBox.getSelectedItem();
                    SwingUtilities.invokeLater(
                            () -> editor.app()
                                    .sendRendererChangeToView(newRenderer)
                    );
                }
        );

        add(this.rendererComboBox, "grow, wrap");

        buildPropertyGroupsContainer();
        propertyGroupsScroll = new JScrollPane(propertyGroupsContainer);
        add(propertyGroupsScroll, "grow, wrap");

        this.saveButton = new JButton(
                new SaveViewConfigAction(
                        () ->
                                propertyGroupPanels.stream().collect(
                                        toMap(
                                                gp -> gp.groupName,
                                                gp -> gp.propertyInputs.stream().collect(
                                                        toMap(
                                                                input -> input.getPropertyName(),
                                                                input -> input.getValue()
                                                        )
                                                )
                                        )
                                ),
                        editor
                )
        );

        add(this.saveButton, "shrink, wrap");

        this.editor.getEventBus().subscribe(ViewInitialized.class, e -> this.buildPropertyGroupsContainer());
        this.editor.getEventBus().subscribe(RendererInitialized.class, e -> this.buildPropertyGroupsContainer());

    }


    @Override
    public void uninstall() {

        removeAll();
        this.editor = null;

    }

    private static class PropertyGroupPanel extends JPanel {

        final String groupName;
        final List<Property<?>> properties;
        final List<PropertyInput<?>> propertyInputs = new ArrayList<>();

        private PropertyGroupPanel(
                String groupName,
                List<Property<?>> properties,
                Map<String, Object> settings
        ) {
            this.groupName = groupName;
            this.properties = properties;

            setBorder(BorderFactory.createTitledBorder(groupName));
            final var layout = new MigLayout("", "grow", "grow");
            setLayout(layout);

            for (var prop : properties) {

                final var input = PropertyInput.inputFor(
                        new Property<>(
                                prop.getType(), prop.getGroup(), prop.getName(),
                                settings.getOrDefault(prop.getName(), prop.getDefaultValue())
                        )
                );

                propertyInputs.add(input);

            }

            propertyInputs.forEach(
                    input -> add(input, "grow, wrap")
            );

        }

    }

}
