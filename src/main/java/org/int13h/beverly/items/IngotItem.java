package org.int13h.beverly.items;

import net.minecraft.world.item.Item;
import org.int13h.beverly.materials.Material;

public class IngotItem extends Item {

    private final Material material;

    public IngotItem(Material material, Properties properties) {
        super(properties);
        this.material = material;
    }
}
