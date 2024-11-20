package org.int13h.beverly.utils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class DistExecutor {

    public static void runOn(Dist side, Runnable runnable) {
        if(side == FMLEnvironment.dist) {
            runnable.run();
        }
    }
}
