package org.int13h.beverly.materials;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import org.int13h.beverly.materials.generation.MaterialFlag;
import org.int13h.beverly.materials.generation.MaterialFlags;
import org.int13h.beverly.materials.properties.DustProperties;
import org.int13h.beverly.materials.properties.MaterialProperties;
import org.int13h.beverly.materials.properties.MaterialProperty;
import org.int13h.beverly.materials.properties.PropertyKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class Material {

    @NotNull
    @Getter
    private final MaterialInfo info;

    @NotNull
    @Getter
    private final MaterialProperties properties;

    @NotNull
    private final MaterialFlags flags;

    private Material(MaterialInfo materialInfo, MaterialProperties properties, MaterialFlags flags) {
        this.info = materialInfo;
        this.properties = properties;
        this.flags = flags;
        this.properties.setMaterial(this);
        verify();
    }

    public String getName() {
        return info.location.getPath();
    }


    public <T extends MaterialProperty<T>> boolean hasProperty(PropertyKey<T> key) {
        return properties.has(key);
    }

    public void verify() {
        properties.verify();
        flags.verify(this);
    }

    public static class Builder {

        private final MaterialInfo materialInfo;
        private final MaterialProperties properties;
        private final MaterialFlags flags;

        private List<MaterialStack> composition = new ArrayList<>();
        private List<Supplier<MaterialStack>> compositionSuppliers = new ArrayList<>();

        private boolean averageRGB = false;


        public Builder(ResourceLocation location) {
            String name = location.getPath();
            if(name.endsWith("_")) {
                throw new IllegalArgumentException("Material name may not end in _");
            }
            materialInfo = new MaterialInfo(location);
            properties = new MaterialProperties();
            flags = new MaterialFlags();
        }

        public Builder dust() {
            properties.ensure(PropertyKey.DUST);
            return this;
        }

        public Builder dust(int harvestLevel) {
            return dust(harvestLevel, 0);
        }

        public Builder dust(int harvestLevel, int burnTime) {
            properties.set(PropertyKey.DUST, new DustProperties(harvestLevel, burnTime));
            return this;
        }

        public Builder ingot() {
            properties.ensure(PropertyKey.INGOT);
            return this;
        }

        public Builder ingot(int harvestLevel) {
            return ingot(harvestLevel, 0);
        }

        public Builder ingot(int harvestLevel, int burnTime) {
            DustProperties dust = properties.get(PropertyKey.DUST);
            if(dust == null) { dust(harvestLevel, burnTime); }
            else {
                if(dust.getHarvestLevel() == 2) dust.setHarvestLevel(harvestLevel);
                if(dust.getBurnTime() == 0) dust.setBurnTime(burnTime);
            }
            return this;
        }

        public Builder colour(int colour) {
            colour(colour, true);
            return this;
        }

        public Builder colour(int colour, boolean hasFluidColor) {
            this.materialInfo.colour.set(0, colour);
            //this.materialInfo.hasFluidColor = hasFluidColor;
            return this;
        }

        public Builder secondaryColour(int colour) {
            this.materialInfo.colour.set(1, colour);
            return this;
        }

        public Builder colourAverage() {
            this.averageRGB = true;
            return this;
        }

//        public Builder components(Object... components) {
//            Preconditions.checkArgument(
//                    components.length % 2 == 0,
//                    "Material Components list malformed!");
//
//            for (int i = 0; i < components.length; i += 2) {
//                if (components[i] == null) {
//                    throw new IllegalArgumentException(
//                            "Material in Components List is null for Material " + this.materialInfo.location);
//                }
//                composition.add(new MaterialStack(
//                        components[i] instanceof CharSequence chars ? Materials.get(chars.toString()) :
//                                (Material) components[i],
//                        ((Number) components[i + 1]).longValue()));
//            }
//            return this;
//        }

        @SafeVarargs
        public final Builder components(Supplier<MaterialStack>... components) {
            compositionSuppliers = Arrays.asList(components);
            return this;
        }

        public final Builder components(MaterialStack... components) {
            composition = Arrays.asList(components);
            return this;
        }

        public Builder flags(MaterialFlag... flags) {
            this.flags.addFlags(flags);
            return this;
        }

        public Builder element(Element element) {
            this.materialInfo.element = element;
            return this;
        }

        public Builder ore() {
            properties.ensure(PropertyKey.ORE);
            return this;
        }

        public Material build() {
            MaterialStack[] stacks = new MaterialStack[compositionSuppliers.size() + composition.size()];
            int i = 0;
            if(!composition.isEmpty()) {
                for(var material : composition) {
                    stacks[i++] = material;
                }
            }
            if(!compositionSuppliers.isEmpty()) {
                for(Supplier<MaterialStack> supplier : compositionSuppliers) {
                    stacks[i++] = supplier.get();
                }
            }
            materialInfo.setComponents(stacks);
            var mat = new Material(materialInfo, properties, flags);
            mat.info.verify(properties, averageRGB);
            return mat;
        }

        public String getName() {
            return materialInfo.location.getPath();
        }
    }



    @Accessors(chain = true)
    private static class MaterialInfo {
        private final ResourceLocation location;

        @Getter
        @Setter
        private IntList colour = new IntArrayList(List.of(-1, -1));

        @Getter
        @Setter
        private ImmutableList<MaterialStack> componentList;

        @Getter
        @Setter
        private Element element;

        private MaterialInfo(ResourceLocation location) {
            this.location = location;
        }

        private void verify(MaterialProperties p, boolean avgRGB) {
            if(colour.getInt(0) == -1) {
                if(!avgRGB || componentList.isEmpty()) {
                    colour.set(0, 0xFFFFFF);
                } else {
                    long colourTemp = 0;
                    int divisor = 0;
                    for(MaterialStack stack : componentList) {
                        colourTemp += stack.material().getMaterialARGB() * stack.amount();
                        divisor += stack.amount();
                    }
                    colour.set(0, (int)((colourTemp / divisor)));
                }
            }
        }

        public MaterialInfo setComponents(MaterialStack... components) {
            this.componentList = ImmutableList.copyOf(Arrays.asList(components));
            return this;
        }
    }

    public int getMaterialARGB() {
        return getMaterialARGB(0);
    }

    public int getMaterialARGB(int index) {
        return info.colour.getInt(index) | 0xff000000;
    }
}
