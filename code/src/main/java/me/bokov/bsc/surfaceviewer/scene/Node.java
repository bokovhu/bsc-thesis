package me.bokov.bsc.surfaceviewer.scene;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import org.joml.Matrix4f;

import java.util.*;

public interface Node {

    int getId();

    void add(Node ... args);
    void addAll(Collection<Node> nodes);
    void moveTo(Node newParent);
    void remove(Node toRemove);
    void remove(int id);

    Optional<Node> findById(int nodeId);

    Optional<Node> parent();
    List<Node> children();

    MeshTransform localTransform();
    Matrix4f worldTransform();

    NodeDisplay getDisplay();

    Evaluatable<Float, CPUContext, GPUContext> toEvaluatable();
    void update();

}
