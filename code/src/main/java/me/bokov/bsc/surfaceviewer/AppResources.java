package me.bokov.bsc.surfaceviewer;

import java.util.*;
import java.util.Map.*;

import static java.util.stream.Collectors.*;

@Deprecated
public final class AppResources {

    private final Map<String, Object> resourceMap = new HashMap<>();

    public AppResources put(String name, Object resource) {
        resourceMap.put(name, resource);
        return this;
    }

    public <T> T get(String name, Class<T> type) {
        return type.cast(resourceMap.get(name));
    }

    public <T> Map<String, T> all(Class<T> type) {
        return resourceMap.entrySet().stream()
                .filter(e -> type.isAssignableFrom(e.getValue().getClass()))
                .collect(toMap(Entry::getKey, e -> type.cast(e.getValue())));
    }

}
