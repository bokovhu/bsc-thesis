package me.bokov.bsc.surfaceviewer.scene;

import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.Evaluetables;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

public class BaseWorld implements World {

    private final AtomicInteger idGenerator = new AtomicInteger(0);
    private final Vector3f b000 = new Vector3f(-1f, -1f, -1f);
    private final Vector3f b111 = new Vector3f(1f, 1f, 1f);
    private List<Node> rootNodes = new ArrayList<>();

    @Override
    public List<Node> roots() {
        return rootNodes;
    }

    @Override
    public void add(Node... args) {
        for (Node root : args) {
            rootNodes.add(root);
        }
    }

    @Override
    public void addAll(Collection<Node> nodes) {
        rootNodes.addAll(nodes);
    }

    @Override
    public void remove(Node node) {
        if (node.parent().isPresent()) {
            node.parent().get()
                    .remove(node);
        } else {
            rootNodes.remove(node);
        }
    }

    @Override
    public void remove(int id) {
        Optional<Node> foundNode = findById(id);
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
    public Optional<Node> findById(int nodeId) {
        for (Node root : rootNodes) {
            Optional<Node> foundInRootSubtree = root.findById(nodeId);
            if (foundInRootSubtree.isPresent()) {
                return foundInRootSubtree;
            }
        }
        return Optional.empty();
    }

    @Override
    public int nextId() {
        return idGenerator.getAndIncrement();
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
    public Evaluatable<Float, CPUContext, GPUContext> toEvaluatable() {
        return Evaluetables.union(
                rootNodes.stream().map(Node::toEvaluatable)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }
}
