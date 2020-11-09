package me.bokov.bsc.surfaceviewer.editor;

import me.bokov.bsc.surfaceviewer.Editor;
import me.bokov.bsc.surfaceviewer.Installable;
import me.bokov.bsc.surfaceviewer.event.EditorSceneChanged;
import me.bokov.bsc.surfaceviewer.editor.scenebrowser.SceneMeshSurfaceNode;
import me.bokov.bsc.surfaceviewer.editor.scenebrowser.SceneNode;
import me.bokov.bsc.surfaceviewer.editor.surface.ShapeSurface;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.*;
import java.util.function.*;

public class SceneBrowser extends JPanel implements Installable<EditorLayout> {

    private static final Map<Class<?>, ImageIcon> NODE_TYPE_ICONS = Map.of(
            SceneNode.class, Icons.FA_THEATRE_MASKS_SOLID_BLACK,
            Void.class, Icons.FA_QUESTION_SOLID_BLACK,
            SceneMeshSurfaceNode.class, Icons.FA_THEATRE_MASKS_SOLID_BLACK
    );
    private static final Map<Class<?>, Function<Object, ImageIcon>> NODE_TYPE_ICON_FACTORIES = Map.of(
            SceneMeshSurfaceNode.class, (o) -> ((SceneMeshSurfaceNode) o).getSurface().getImageIcon()
    );
    private Editor editor;
    private SceneActionBar actionBar;
    private JScrollPane treeScrollPane;
    private DefaultTreeModel treeModel;
    private JTree tree;

    public SceneBrowser() {
    }

    // FIXME: Should not always return a new scene node!
    private SceneNode sceneToRoot() {

        if (editor.getScene() != null) {
            return new SceneNode(editor.getScene());
        }

        return new SceneNode();

    }

    @Override
    public void install(EditorLayout parent) {

        this.editor = parent.getEditor();

        this.editor.getEventBus()
                .subscribe(EditorSceneChanged.class, (e) -> {
                    SwingUtilities.invokeLater(
                            () -> {
                                this.treeModel.setRoot(sceneToRoot());
                            }
                    );
                });

        treeModel = new DefaultTreeModel(sceneToRoot());

        this.tree = new JTree(treeModel);
        this.tree.setCellRenderer(new SceneBrowserTreeCellRenderer());
        this.tree.addTreeSelectionListener(
                e -> {
                    final TreeNode selected = (TreeNode) e.getPath().getLastPathComponent();
                    if (selected instanceof SceneMeshSurfaceNode && e.isAddedPath()) {
                        final var surfaceNode = (SceneMeshSurfaceNode) selected;
                        if (surfaceNode.getSurface() instanceof ShapeSurface) {
                            editor.getEditorLayout()
                                    .getEditorTabset().openShapeSettingsTab(
                                    (ShapeSurface) surfaceNode.getSurface()
                            );
                        }
                    }
                }
        );

        this.treeScrollPane = new JScrollPane(this.tree);

        final var layout = new MigLayout("", "[grow]", "[shrink][grow]");
        setLayout(layout);

        actionBar = new SceneActionBar(this.editor)
                .create();

        add(actionBar, "wrap");
        add(this.treeScrollPane, "push, grow");

    }

    @Override
    public void uninstall() {

        removeAll();
        this.editor = null;

    }

    private class SceneBrowserTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(
                JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus
        ) {
            final var component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (component instanceof JLabel) {
                final var label = (JLabel) component;
                if (NODE_TYPE_ICON_FACTORIES.containsKey(value.getClass())) {
                    label.setIcon(NODE_TYPE_ICON_FACTORIES.get(value.getClass()).apply(value));
                } else if (NODE_TYPE_ICONS.containsKey(value.getClass())) {
                    label.setIcon(NODE_TYPE_ICONS.get(value.getClass()));
                } else {
                    label.setIcon(NODE_TYPE_ICONS.get(Void.class));
                }
            }

            return component;
        }
    }

}
