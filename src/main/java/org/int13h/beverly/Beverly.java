package org.int13h.beverly;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.int13h.beverly.resources.DynamicResourcePack;
import org.slf4j.Logger;

@Mod(Beverly.MODID)
public class Beverly {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "beverly";

    private static IEventBus modBus;

    public static IEventBus getModBus() {
        if(modBus == null) {
            throw new IllegalStateException("Mod bus not yet initialized");
        }
        return modBus;
    }

    public static final ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public Beverly(IEventBus bus, FMLModContainer modContainer) {
        LOGGER.info("Loading beverly");
        if(modBus != null) {
            modBus = bus;
        } else {
            LOGGER.warn("Mod bus being set multiple times?");
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void addPackEvent(AddPackFindersEvent event) {
            event.addRepositorySource(consumer -> consumer.accept(DynamicResourcePack.getPack()));
        }
    }


}
