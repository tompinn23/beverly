package org.int13h.beverly.materials.properties;

import lombok.Getter;
import lombok.Setter;
import org.int13h.beverly.materials.Material;

import java.util.*;

public class MaterialProperties {

    private final Set<PropertyKey<?>> baseTypes = new HashSet<>(Arrays.asList(
            PropertyKey.INGOT
    ));

    public void addBaseType(PropertyKey<?> type) {
        baseTypes.add(type);
    }

    private final Map<PropertyKey<? extends MaterialProperty<?>>, MaterialProperty<?>> properties = new HashMap<>();
    @Setter
    @Getter
    private Material material;


    public <T extends MaterialProperty<T>> T get(PropertyKey<T> key) {
        return key.cast(properties.get(key));
    }

    public <T extends MaterialProperty<T>> boolean has(PropertyKey<T> key) {
        return properties.get(key) != null;
    }

    public <T extends MaterialProperty<T>> void set(PropertyKey<T> key, T value) {
        if(value == null) throw new IllegalArgumentException("Property cannot be null");
        if(has(key)) {
            throw new IllegalStateException("Property is already set");
        }
        properties.put(key, value);
        properties.remove(PropertyKey.EMPTY);
    }

    public <T extends MaterialProperty<T>> void remove(PropertyKey<T> key) {
        if(!has(key)) throw new IllegalStateException("Property is not set");
        properties.remove(key);
        if(properties.isEmpty()) {
            properties.put(PropertyKey.EMPTY, PropertyKey.EMPTY.defaultValue());
        }
    }

    public <T extends MaterialProperty<T>> void ensure(PropertyKey<T> key, boolean verify) {
        if(!has(key)) {
            properties.put(key, key.defaultValue());
            properties.remove(PropertyKey.EMPTY);
            if(verify) verify();
        }
    }

    public <T extends MaterialProperty<T>> void ensure(PropertyKey<T> key) {
        ensure(key, false);
    }

    public void verify() {
        List<MaterialProperty<?>> oldList;
        do {
            oldList = new ArrayList<>(properties.values());
            oldList.forEach(p -> p.verify(this));
        } while(oldList.size() != properties.size());

        if(properties.keySet().stream().noneMatch(baseTypes::contains)) {
            if(properties.isEmpty()) {
                properties.put(PropertyKey.EMPTY, PropertyKey.EMPTY.defaultValue());
            } else {
                throw new IllegalStateException("Material must have at least one of " + baseTypes + " set");
            }
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        properties.forEach((k,v) -> builder.append(k.toString()).append('\n'));
        return builder.toString();
    }
}
