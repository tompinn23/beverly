package org.int13h.beverly.items;

import net.minecraft.world.item.Item;
import org.int13h.beverly.materials.Material;

public class DustItem extends Item {

    private final Material material;

    public DustItem(Material material, Properties properties) {
        super(properties);
        this.material = material;
    }
}
