package me.bokov.bsc.surfaceviewer.scene;

import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import org.joml.Vector3f;

import java.util.*;

public interface World {

    List<Node> roots();

    void add(Node ... args);
    void addAll(Collection<Node> nodes);
    void remove(Node node);
    void remove(int id);

    Optional<Node> findById(int nodeId);
    int nextId();

    Vector3f bounds000();
    Vector3f bounds111();

    void applyBounds(Vector3f b000, Vector3f b111);

    Evaluatable<Float, CPUContext, GPUContext> toEvaluatable();

}
