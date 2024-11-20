package org.int13h.beverly.materials.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class PropertyKey<T extends MaterialProperty<T>> {

    public static final PropertyKey<IngotProperties> INGOT = PropertyKey.create("ingot", IngotProperties.class);
    public static final PropertyKey<DustProperties> DUST = PropertyKey.create("dust", DustProperties.class);
    public static final PropertyKey<OreProperties> ORE = PropertyKey.create("ore", OreProperties.class);
    public static final PropertyKey<EmptyProperties> EMPTY = PropertyKey.create("empty", EmptyProperties.class);

    private final String name;
    private final Class<T> type;

    private PropertyKey(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public static <T extends MaterialProperty<T>> PropertyKey<T> create(String name, Class<T> clazz) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(clazz);
        return new PropertyKey<>(name, clazz);
    }

    protected String getName() {
        return name;
    }

    public T defaultValue() {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            return null;
        }
    }

    public T cast(MaterialProperty<?> property) {
        return type.cast(property);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PropertyKey<?> other) {
            return other.name.equals(this.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static class EmptyProperties implements MaterialProperty<EmptyProperties> {
        @Override
        public void verify(MaterialProperties properties) {
            //noop
        }
    }
}

