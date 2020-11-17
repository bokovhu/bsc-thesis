package me.bokov.bsc.surfaceviewer.scene;

import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

public interface World extends Serializable {

    List<SceneNode> roots();

    void add(SceneNode... args);
    void addAll(Collection<SceneNode> nodes);
    void remove(SceneNode node);
    void remove(int id);
    void replace(Map<Integer, SceneNode> replacements);

    Optional<SceneNode> findById(int nodeId);
    int nextId();

    Vector3f bounds000();
    Vector3f bounds111();

    void applyBounds(Vector3f b000, Vector3f b111);

    Evaluable<Float, CPUContext, GPUContext> toEvaluable();

    List<LightSource> getLightSources();
    void add(LightSource ... args);
    void remove(LightSource ls);
    void removeLightSource(int id);

    List<Materializer> getMaterializers();
    void add(Materializer ... args);
    void remove(Materializer materializer);
    void removeMaterializer(int id);

}
