package me.bokov.bsc.surfaceviewer.scene;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import org.joml.Matrix4f;

import java.io.Serializable;
import java.util.*;

public interface SceneNode extends SceneComponent {

    void add(SceneNode... args);
    void addAll(Collection<SceneNode> nodes);
    void moveTo(SceneNode newParent);
    void remove(SceneNode toRemove);
    void remove(int id);
    void plug(String portName, SceneNode pluggedChild);
    void unplug(String portName);

    Optional<SceneNode> findById(int nodeId);

    Optional<SceneNode> parent();
    List<SceneNode> children();
    Map<String, SceneNode> pluggedPorts();
    NodeProperties properties();

    MeshTransform localTransform();
    Matrix4f worldTransform();

    NodeDisplay getDisplay();

    Evaluable<Float, CPUContext, GPUContext> toEvaluable();
    void update();

    String getTemplateName();

    Prefab getPrefab();
    void setPrefab(Prefab prefab);

}
