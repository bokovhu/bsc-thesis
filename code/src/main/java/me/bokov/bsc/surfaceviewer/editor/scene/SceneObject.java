package me.bokov.bsc.surfaceviewer.editor.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SceneObject {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

    private final int id = ID_GENERATOR.getAndIncrement();
    private final Transform localTransform = new Transform();
    private final Transform globalTransform = new Transform();
    private final Map<String, SceneObjectProperty<?>> propertyMap = new HashMap<>();
    private SceneObject parent = null;
    private final List<SceneObject> children = new ArrayList<>();

    public SceneObject() {
        init();
    }

    private void init() {
        final PropertyRegistry registry = new PropertyRegistry();
        this.registerProperties(registry);
        registry.properties.forEach(p -> propertyMap.put(p.getName(), p));
    }

    private void removeChildBeforeTransfer(SceneObject child) {
        this.children.removeIf(o -> o.id == child.id);
    }

    private void addChildAfterTransfer(SceneObject child) {
        if (children.stream().noneMatch(o -> o.id == child.id)) {
            children.add(child);
        }
    }

    public SceneObject addChild(SceneObject newChild) {
        if (newChild.parent != null) {
            newChild.parent.removeChildBeforeTransfer(newChild);
        }
        newChild.parent = this;
        addChildAfterTransfer(newChild);
        return this;
    }

    public void updateTransform() {
        if (parent != null) {
            globalTransform.set(parent.globalTransform)
                    .combine(localTransform);
        } else {
            globalTransform.set(localTransform);
        }
        for (SceneObject child : children) {
            child.updateTransform();
        }
    }

    public Transform local() {
        return localTransform;
    }

    public Transform global() {
        return globalTransform;
    }

    protected abstract void registerProperties(final PropertyRegistry registry);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SceneObject that = (SceneObject) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    protected class PropertyRegistry {

        private final List<SceneObjectProperty<?>> properties = new ArrayList<>();

        public PropertyRegistry add(SceneObjectProperty<?>... args) {
            for (SceneObjectProperty<?> p : args) {
                properties.add(p);
            }
            return this;
        }

    }
}
