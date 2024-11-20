package org.int13h.beverly.materials;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public record MaterialStack(Material material, long amount) {

    public static final MaterialStack of(Material material, long amount) {
        return new MaterialStack(material, amount);
    }

    public static Supplier<MaterialStack> ofSupplier(final Supplier<Material> material, final long amount) {
        return () -> of(material.get(), amount);
    }

    private static final Map<String, MaterialStack> PARSE_CACHE = new WeakHashMap<>();

    public MaterialStack copy(long amount) {
        return new MaterialStack(material, amount);
    }

    public MaterialStack copy() {
        return new MaterialStack(material, amount);
    }

    public static MaterialStack fromString(CharSequence str) {
        String trimmed = str.toString().trim();
        String copy = trimmed;

        var cached = PARSE_CACHE.get(copy);
        if (cached != null) {
            return cached.isEmpty() ? null : cached.copy();
        }

        var count = 1;
        var spaceIndex = copy.indexOf(' ');

        if(spaceIndex >= 2 && copy.indexOf('x') == spaceIndex - 1) {
            count = Integer.parseInt(copy.substring(0, spaceIndex - 1));
            copy = copy.substring(spaceIndex + 1);
        }

        //cached = new MaterialStack(, count);
        //PARSE_CACHE
        //return cached.copy();
        return null;
    }

    public boolean isEmpty() {
        return this.amount < 1;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        MaterialStack other = (MaterialStack) obj;
        return other.amount == this.amount && material.equals(other.material);
    }

    @Override
    public int hashCode() {
        return material.hashCode() * 31 + (int) amount * 31;
    }

}
