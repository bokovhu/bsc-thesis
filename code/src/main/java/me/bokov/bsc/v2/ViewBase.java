package me.bokov.bsc.v2;

import java.util.*;

public abstract class ViewBase implements Configuration {

    protected final Map<String, Map<String, Object>> configurationGroupSettings = new HashMap<>();

    @Override
    public <T> T get(Property<T> prop) {
        return (T) configurationGroupSettings.getOrDefault(prop.getGroup(), Collections.emptyMap())
                .getOrDefault(prop.getName(), prop.getDefaultValue());
    }

    @Override
    public <T> T get(Property<T> prop, T defaultValue) {
        return (T) configurationGroupSettings.getOrDefault(prop.getGroup(), Collections.emptyMap())
                .getOrDefault(prop.getName(), defaultValue);
    }

    @Override
    public <T> T get(String group, String name, Class<T> propClass) {
        return propClass.cast(
                configurationGroupSettings.getOrDefault(group, Collections.emptyMap())
                        .getOrDefault(name, null)
        );
    }

    @Override
    public <T> T get(String group, String name, Class<T> propClass, T defaultValue) {
        return propClass.cast(
                configurationGroupSettings.getOrDefault(group, Collections.emptyMap())
                        .getOrDefault(name, defaultValue)
        );
    }

    @Override
    public <T> T get(String group, String name) {
        return (T)
                configurationGroupSettings.getOrDefault(group, Collections.emptyMap())
                        .getOrDefault(name, null);
    }

    @Override
    public <T> T get(String group, String name, T defaultValue) {
        return (T)
                configurationGroupSettings.getOrDefault(group, Collections.emptyMap())
                        .getOrDefault(name, defaultValue);
    }
}
