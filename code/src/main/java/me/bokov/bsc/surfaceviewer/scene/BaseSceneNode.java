package me.bokov.bsc.surfaceviewer.scene;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import org.joml.Matrix4f;

import java.util.*;
import java.util.stream.*;

import static me.bokov.bsc.surfaceviewer.sdf.Evaluables.*;

public class BaseSceneNode implements SceneNode {

    private static final Matrix4f IDENTITY = new Matrix4f().identity();
    private static final Matrix4f IDENTITY_INV = new Matrix4f().identity().invert();

    private final int id;

    private final List<SceneNode> children = new ArrayList<>();
    private final Map<String, SceneNode> portMap = new HashMap<>();
    private final MeshTransform localTransform = new MeshTransform();
    private final Matrix4f worldTransformMatrix = new Matrix4f();
    private final NodeProperties properties = new NodeProperties();
    private SceneNode parentNode = null;

    private NodeTemplate template;
    private NodeDisplay display = new NodeDisplay().setName("Unnamed node");

    public BaseSceneNode(int id, NodeTemplate template) {
        this.id = id;
        this.template = template;
    }

    public BaseSceneNode(SceneNode parent, int id, NodeTemplate template) {
        this.id = id;
        this.parentNode = parent;
        this.template = template;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void add(SceneNode... args) {
        for (SceneNode n : args) {
            n.moveTo(this);
            this.children.add(n);
        }
    }

    @Override
    public void addAll(Collection<SceneNode> nodes) {
        nodes.forEach(this::add);
    }

    @Override
    public void moveTo(SceneNode newParent) {
        if (parentNode != null) {
            parentNode.remove(id);
        }
        this.parentNode = newParent;
    }

    @Override
    public void remove(SceneNode toRemove) {
        this.children.remove(toRemove);
    }

    @Override
    public void remove(int id) {
        this.children.removeIf(n -> n.getId() == id);
        Set<String> removeKeys = pluggedPorts().keySet()
                .stream().filter(k -> pluggedPorts().get(k).getId() == id)
                .collect(Collectors.toSet());
        removeKeys.forEach(pluggedPorts()::remove);
    }

    @Override
    public void plug(String portName, SceneNode pluggedChild) {
        portMap.put(portName, pluggedChild);
    }

    @Override
    public void unplug(String portName) {
        portMap.put(portName, null);
    }

    @Override
    public Optional<SceneNode> findById(int nodeId) {
        if (id == nodeId) {
            return Optional.of(this);
        }
        for (SceneNode child : children) {
            Optional<SceneNode> foundInChildSubtree = child.findById(nodeId);
            if (foundInChildSubtree.isPresent()) {
                return foundInChildSubtree;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<SceneNode> parent() {
        return Optional.ofNullable(parentNode);
    }

    @Override
    public List<SceneNode> children() {
        return children;
    }

    @Override
    public Map<String, SceneNode> pluggedPorts() {
        return portMap;
    }

    @Override
    public NodeProperties properties() {
        return this.properties;
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
    public NodeDisplay getDisplay() {
        return display;
    }

    @Override
    public Evaluable<Float, CPUContext, GPUContext> toEvaluable() {

        if (template == null) {
            throw new IllegalStateException("Cannot create evaluable");
        }

        update();

        final var requestBuilder = SurfaceFactoryRequest.builder()
                .children(this.children)
                .ports(this.portMap);

        properties.apply(requestBuilder);

        final var evaluable = template.factory.apply(requestBuilder.build());
        if (evaluable == null) {
            return null;
        }

        if (worldTransformMatrix.equals(IDENTITY) || worldTransformMatrix.equals(IDENTITY_INV)) {
            return evaluable;
        }

        return transform(localTransform, evaluable);
    }

    @Override
    public void update() {

        this.worldTransformMatrix.identity();
        if (parentNode != null) {
            this.worldTransformMatrix.set(parentNode.worldTransform());
        }

        this.worldTransformMatrix.mul(this.localTransform.M());

        for (SceneNode child : children) {
            child.update();
        }

    }

    @Override
    public NodeTemplate getTemplate() {
        return template;
    }

    @Override
    public String toString() {
        return getDisplay().getName();
    }
}
