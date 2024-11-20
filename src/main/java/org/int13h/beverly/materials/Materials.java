package org.int13h.beverly.materials;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.int13h.beverly.Beverly;
import org.int13h.beverly.items.DustItem;
import org.int13h.beverly.items.IngotItem;
import org.int13h.beverly.materials.properties.PropertyKey;
import org.int13h.beverly.registries.NonGameRegistry;
import org.int13h.beverly.utils.DistExecutor;
import org.int13h.beverly.utils.OneTimeEventReceiver;

public class Materials {


    public static final NonGameRegistry<Material> MATERIALS = new NonGameRegistry<>(Beverly.MODID);

    public static final Material IRON = register(new Material.Builder(Beverly.location("iron")).element(Elements.IRON).dust().ingot());
    public static final Material TIN = register(new Material.Builder(Beverly.location("tin")).element(Elements.TIN).dust().ingot());
    public static final Material COPPER = register(new Material.Builder(Beverly.location("copper")).element(Elements.COPPER).dust().ingot());
    public static final Material BRONZE = register(new Material.Builder(Beverly.location("bronze")).components(MaterialStack.of(TIN, 1), MaterialStack.of(COPPER, 3)).dust().ingot());

    private static Material register(Material.Builder builder) {
        return MATERIALS.register(builder.getName(), builder.build());
    }

    public static Material get(String name) {
        return MATERIALS.get(ResourceLocation.fromNamespaceAndPath(Beverly.MODID, name));
    }

    public static void init(DeferredRegister.Items ITEMS, DeferredRegister.Blocks BLOCKS) {
        for(var material : MATERIALS.getEntries()) {
            if(material.getProperties().has(PropertyKey.DUST)) {
                ITEMS.registerItem(material.getName() + "_dust", itemProperties -> new DustItem(material, itemProperties));
                DistExecutor.runOn(Dist.CLIENT, () -> {
                    OneTimeEventReceiver.addModListener(Beverly.getModBus(), RegisterColorHandlersEvent.Item.class, e -> {
                        e.register();
                    });
                });
            }
            if(material.getProperties().has(PropertyKey.INGOT)) {
                ITEMS.registerItem(material.getName() + "_ingot", itemProperties -> new IngotItem(material, itemProperties));
            }
        }
    }
}
