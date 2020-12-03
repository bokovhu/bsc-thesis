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
    private Integer lastId = null;
    private List<SceneNode> rootNodes = new ArrayList<>();
    private List<LightSource> lightSources = new ArrayList<>();
    private List<Materializer> materializers = new ArrayList<>();
    private List<Prefab> prefabs = new ArrayList<>();

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
        if (lastId == null) {
            lastId = 0;
        }
        return lastId++;
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
    public List<LightSource> getLightSources() {
        return lightSources;
    }

    @Override
    public void add(LightSource... args) {
        for (LightSource ls : args) {
            lightSources.add(ls);
        }
    }

    @Override
    public void remove(LightSource ls) {
        lightSources.removeIf(l -> l.getId() == ls.getId());
    }

    @Override
    public void removeLightSource(int id) {
        lightSources.removeIf(l -> l.getId() == id);
    }

    @Override
    public List<Materializer> getMaterializers() {
        return materializers;
    }

    @Override
    public void add(Materializer... args) {
        for (Materializer m : args) {
            materializers.add(m);
        }
    }

    @Override
    public void remove(Materializer materializer) {
        materializers.removeIf(m -> m.getId() == materializer.getId());
    }

    @Override
    public void removeMaterializer(int id) {
        materializers.removeIf(m -> m.getId() == id);
    }

    @Override
    public List<Prefab> getPrefabs() {
        return prefabs;
    }

    @Override
    public void add(Prefab... args) {
        for (Prefab p : args) {
            prefabs.add(p);
        }
    }

    @Override
    public void remove(Prefab prefab) {
        prefabs.removeIf(p -> p.getId() == prefab.getId());
    }

    @Override
    public void removePrefab(int id) {
        prefabs.removeIf(p -> p.getId() == id);
    }

    @Override
    public Optional<Prefab> findPrefabByName(String name) {
        return prefabs.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
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
