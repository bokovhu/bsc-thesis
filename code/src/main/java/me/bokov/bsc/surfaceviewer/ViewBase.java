package me.bokov.bsc.surfaceviewer;

import java.io.Serializable;
import java.util.*;

public abstract class ViewBase implements Configuration {

    protected final Map<String, Map<String, Object>> configurationGroupSettings = new HashMap<>();

    @Override
    public <T extends Serializable> T get(Property<T> prop) {
        return (T) configurationGroupSettings.getOrDefault(prop.getGroup(), Collections.emptyMap())
                .getOrDefault(prop.getName(), prop.getDefaultValue());
    }

    @Override
    public <T extends Serializable> T get(Property<T> prop, T defaultValue) {
        return (T) configurationGroupSettings.getOrDefault(prop.getGroup(), Collections.emptyMap())
                .getOrDefault(prop.getName(), defaultValue);
    }

    @Override
    public <T extends Serializable> T get(String group, String name, Class<T> propClass) {
        return propClass.cast(
                configurationGroupSettings.getOrDefault(group, Collections.emptyMap())
                        .getOrDefault(name, null)
        );
    }

    @Override
    public <T extends Serializable> T get(String group, String name, Class<T> propClass, T defaultValue) {
        return propClass.cast(
                configurationGroupSettings.getOrDefault(group, Collections.emptyMap())
                        .getOrDefault(name, defaultValue)
        );
    }

    @Override
    public <T extends Serializable> T get(String group, String name) {
        return (T)
                configurationGroupSettings.getOrDefault(group, Collections.emptyMap())
                        .getOrDefault(name, null);
    }

    @Override
    public <T extends Serializable> T get(String group, String name, T defaultValue) {
        return (T)
                configurationGroupSettings.getOrDefault(group, Collections.emptyMap())
                        .getOrDefault(name, defaultValue);
    }
}
