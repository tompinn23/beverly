package org.int13h.beverly.materials.generation;


import org.int13h.beverly.materials.Material;
import org.int13h.beverly.materials.properties.PropertyKey;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MaterialFlags {
    private final Set<MaterialFlag> flags = new HashSet<>();

    public MaterialFlags addFlags(MaterialFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    public void verify(Material material) {
        flags.addAll(flags.stream()
                .map(f -> f.verify(material))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
    }

    public boolean hasFlag(MaterialFlag flag) {
        return flags.contains(flag);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        flags.forEach(f -> builder.append(f.toString()).append('\n'));
        return builder.toString();
    }

    public static final MaterialFlag GENERATE_PLATE = new MaterialFlag.Builder("generate_plate")
            .requireProperties(PropertyKey.DUST)
            .build();

    public static final MaterialFlag GENERATE_ROD = new MaterialFlag.Builder("generate_rod")
            .requireProperties(PropertyKey.DUST)
            .build();

    public static final MaterialFlag GENERATE_GEAR = new MaterialFlag.Builder("generate_gear")
            .requireFlags(GENERATE_PLATE, GENERATE_ROD)
            .requireProperties(PropertyKey.DUST)
            .build();
}
