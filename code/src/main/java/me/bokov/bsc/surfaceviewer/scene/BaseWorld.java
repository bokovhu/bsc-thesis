package me.bokov.bsc.surfaceviewer.scene;

import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.Evaluables;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.*;

public class BaseWorld implements World {

    private final Vector3f b000 = new Vector3f(-1f, -1f, -1f);
    private final Vector3f b111 = new Vector3f(1f, 1f, 1f);
    private int lastId = 0;
    private List<SceneNode> rootNodes = new ArrayList<>();

    @Override
    public List<SceneNode> roots() {
        return rootNodes;
    }

    @Override
    public void add(SceneNode... args) {
        for (SceneNode root : args) {
            rootNodes.add(root);
        }
    }

    @Override
    public void addAll(Collection<SceneNode> nodes) {
        rootNodes.addAll(nodes);
    }

    @Override
    public void remove(SceneNode node) {
        if (node.parent().isPresent()) {
            node.parent().get()
                    .remove(node);
        } else {
            rootNodes.remove(node);
        }
    }

    @Override
    public void remove(int id) {
        Optional<SceneNode> foundNode = findById(id);
        if (foundNode.isPresent()) {
            if (foundNode.get().parent().isPresent()) {
                foundNode.get().parent().get()
                        .remove(id);
            } else {
                rootNodes.removeIf(r -> r.getId() == id);
            }
        }
    }

    @Override
    public void replace(Map<Integer, SceneNode> replacements) {
        replacements.forEach(
                (id, node) -> {
                    Optional<SceneNode> foundNode = findById(id);
                    if (foundNode.isPresent()) {
                        if (foundNode.get().parent().isPresent()) {
                            final var parent = foundNode.get().parent().get();
                            parent.remove(id);
                            parent.add(node);
                        } else {
                            remove(id);
                            add(node);
                        }
                    }
                }
        );
    }

    @Override
    public Optional<SceneNode> findById(int nodeId) {
        for (SceneNode root : rootNodes) {
            Optional<SceneNode> foundInRootSubtree = root.findById(nodeId);
            if (foundInRootSubtree.isPresent()) {
                return foundInRootSubtree;
            }
        }
        return Optional.empty();
    }

    @Override
    public int nextId() {
        return 1 + rootNodes.stream().mapToInt(r -> walkMaxId(r, 0)).max().orElse(0);
    }

    @Override
    public Vector3f bounds000() {
        return b000;
    }

    @Override
    public Vector3f bounds111() {
        return b111;
    }

    @Override
    public void applyBounds(Vector3f b000, Vector3f b111) {
        this.b000.set(b000);
        this.b111.set(b111);
    }

    @Override
    public Evaluable<Float, CPUContext, GPUContext> toEvaluable() {
        return Evaluables.union(
                rootNodes.stream().map(SceneNode::toEvaluable)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public String toString() {
        return "The world";
    }

    private int walkMaxId(SceneNode node, int max) {
        final int res1 = Math.max(node.getId(), max);
        return node.children().stream().mapToInt(n -> walkMaxId(n, res1)).max().orElse(res1);
    }

}
