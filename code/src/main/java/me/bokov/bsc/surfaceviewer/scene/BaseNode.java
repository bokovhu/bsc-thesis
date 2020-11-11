package me.bokov.bsc.surfaceviewer.scene;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import org.joml.Matrix4f;

import java.util.*;

public abstract class BaseNode implements Node {

    private final int id;

    private final List<Node> children = new ArrayList<>();
    private final MeshTransform localTransform = new MeshTransform();
    private final Matrix4f worldTransformMatrix = new Matrix4f();
    private Node parentNode = null;

    public BaseNode(int id) {
        this.id = id;
    }

    public BaseNode(Node parent, int id) {
        this.id = id;
        this.parentNode = parent;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void add(Node... args) {
        for (Node n : args) {
            n.moveTo(this);
            this.children.add(n);
        }
    }

    @Override
    public void addAll(Collection<Node> nodes) {
        nodes.forEach(this::add);
    }

    @Override
    public void moveTo(Node newParent) {
        if (parentNode != null) {
            parentNode.remove(id);
        }
        this.parentNode = newParent;
    }

    @Override
    public void remove(Node toRemove) {
        this.children.remove(toRemove);
    }

    @Override
    public void remove(int id) {
        this.children.removeIf(n -> n.getId() == id);
    }

    @Override
    public Optional<Node> findById(int nodeId) {
        if (id == nodeId) {
            return Optional.of(this);
        }
        for (Node child : children) {
            Optional<Node> foundInChildSubtree = child.findById(nodeId);
            if (foundInChildSubtree.isPresent()) {
                return foundInChildSubtree;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Node> parent() {
        return Optional.ofNullable(parentNode);
    }

    @Override
    public List<Node> children() {
        return children;
    }

    @Override
    public MeshTransform localTransform() {
        return localTransform;
    }

    @Override
    public Matrix4f worldTransform() {
        return worldTransformMatrix;
    }

    @Override
    public void update() {

        this.worldTransformMatrix.identity();
        if (parentNode != null) {
            this.worldTransformMatrix.set(parentNode.worldTransform());
        }

        this.worldTransformMatrix.mul(this.localTransform.M());

        for (Node child : children) {
            child.update();
        }

    }
}
