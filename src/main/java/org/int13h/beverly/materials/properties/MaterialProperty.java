package org.int13h.beverly.materials.properties;

@FunctionalInterface
public interface MaterialProperty<T> {
    void verify(MaterialProperties properties);
}
