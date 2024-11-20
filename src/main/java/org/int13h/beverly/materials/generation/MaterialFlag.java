package org.int13h.beverly.materials.generation;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.int13h.beverly.materials.Material;
import org.int13h.beverly.materials.properties.PropertyKey;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MaterialFlag {

    private static final Set<MaterialFlag> FLAGS = new HashSet<>();

    private final String name;

    private final Set<MaterialFlag> requiredFlags;
    private final Set<PropertyKey<?>> requiredProperties;

    private MaterialFlag(String name, Set<MaterialFlag> requiredFlags, Set<PropertyKey<?>> requiredProperties) {
        this.name = name;
        this.requiredFlags = requiredFlags;
        this.requiredProperties = requiredProperties;
        FLAGS.add(this);
    }

    protected Set<MaterialFlag> verify(Material material) {
        for (PropertyKey<?> key : requiredProperties) {
            if(!material.hasProperty(key)) {

            }
        }

        Set<MaterialFlag> thisAndDeps = new HashSet<>(requiredFlags);
        thisAndDeps.addAll(requiredFlags.stream().map(f -> f.verify(material)).flatMap(Collection::stream).collect(Collectors.toSet()));
        return thisAndDeps;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        MaterialFlag other = (MaterialFlag) obj;
        return this.name.equals(other.name);
    }

    public static class Builder {
        final String name;
        final Set<MaterialFlag> requiredFlags = new ObjectOpenHashSet<>();
        final Set<PropertyKey<?>> requiredProperties = new ObjectOpenHashSet<>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder requireFlags(MaterialFlag... flags) {
            requiredFlags.addAll(Arrays.asList(flags));
            return this;
        }

        public Builder requireProperties(PropertyKey<?>... properties) {
            requiredProperties.addAll(Arrays.asList(properties));
            return this;
        }

        public MaterialFlag build() {
            return new MaterialFlag(name, requiredFlags, requiredProperties);
        }
    }
}
