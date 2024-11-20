package org.int13h.beverly.materials.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DustProperties implements MaterialProperty<DustProperties> {

    private int harvestLevel;
    private int burnTime;

    public DustProperties() {
        this(2, 0);
    }

    public DustProperties(final int harvestLevel, final int burnTime) {
        this.harvestLevel = harvestLevel;
        this.burnTime = burnTime;
    }

    @Override
    public void verify(MaterialProperties properties) {

    }
}
