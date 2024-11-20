package org.int13h.beverly.registries;

import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.BiConsumer;

public class NonGameRegistry<T> {
    private final Map<String, Map<String, T>> entriesByNamespace;
    private final Map<ResourceLocation, T> entries;

    private final String defaultNamespace;

    public NonGameRegistry(String defaultNamespace) {
        entriesByNamespace = new HashMap<>();
        entries = new HashMap<>();
        this.defaultNamespace = defaultNamespace;
    }

    public boolean contains(ResourceLocation location) {
        return entries.containsKey(location);
    }

    public T register(ResourceLocation location, T value) {
        if(entries.containsKey(location)) {
            throw new IllegalStateException("Duplicate key: " + location);
        }
        entriesByNamespace.putIfAbsent(location.getNamespace(), new HashMap<>()).put(location.getPath(), value);
        entries.put(location, value);
        return value;
    }

    public T register(String name, T value) {
        return this.register(ResourceLocation.fromNamespaceAndPath(defaultNamespace, name), value);
    }
    public T get(ResourceLocation location) {
        return entries.get(location);
    }

    public Collection<T> getByNamespace(String namespace) {
        if(entriesByNamespace.containsKey(namespace)) {
            return entriesByNamespace.get(namespace).values();
        }
        return List.of();
    }

    public Set<ResourceLocation> getKeys() {
        return entries.keySet();
    }

    public Collection<T> getEntries() {
        return entries.values();
    }

    public void forEach(BiConsumer<ResourceLocation, T> consumer) {
        entries.forEach(consumer);
    }
}
