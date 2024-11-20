package org.int13h.beverly.utils;

import com.mojang.logging.LogUtils;
import lombok.RequiredArgsConstructor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class OneTimeEventReceiver<T extends Event> implements Consumer<T> {

    private static final Logger LOG = LogUtils.getLogger();

    public static <T extends Event & IModBusEvent> void addModListener(IEventBus bus, Class<? super T> evtClass, Consumer<? super T> listener) {
        OneTimeEventReceiver.<T>addModListener(bus, EventPriority.NORMAL, evtClass, listener);
    }

    public static <T extends Event & IModBusEvent> void addModListener(IEventBus bus, EventPriority priority, Class<? super T> evtClass, Consumer<? super T> listener) {
        if (!seenModBus) {
            seenModBus = true;
            addModListener(bus, FMLLoadCompleteEvent.class, OneTimeEventReceiver::onLoadComplete);
        }
        OneTimeEventReceiver.<T>addListener(bus, priority, evtClass, listener);
    }

    public static <T extends Event> void addForgeListener(Class<? super T> evtClass, Consumer<? super T> listener) {
        OneTimeEventReceiver.<T>addForgeListener(EventPriority.NORMAL, evtClass, listener);
    }

    public static <T extends Event> void addForgeListener(EventPriority priority, Class<? super T> evtClass, Consumer<? super T> listener) {
        OneTimeEventReceiver.<T>addListener(NeoForge.EVENT_BUS, priority, evtClass, listener);
    }

    @Deprecated
    public static <T extends Event> void addListener(IEventBus bus, Class<? super T> evtClass, Consumer<? super T> listener) {
        OneTimeEventReceiver.<T>addListener(bus, EventPriority.NORMAL, evtClass, listener);
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T extends Event> void addListener(IEventBus bus, EventPriority priority, Class<? super T> evtClass, Consumer<? super T> listener) {
        bus.addListener(priority, false, (Class<T>) evtClass, new OneTimeEventReceiver<>(bus, listener));
    }

    private static boolean seenModBus = false;

    private final IEventBus bus;
    private final Consumer<? super T> listener;
    private final AtomicBoolean consumed = new AtomicBoolean();

    @Override
    public void accept(T event) {
        if (consumed.compareAndSet(false, true)) {
            listener.accept(event);
            unregister(bus, this, event);
        }
    }

    private static final List<Triple<IEventBus, Object, Class<? extends Event>>> toUnregister = new ArrayList<>();

    private static synchronized void unregister(IEventBus bus, Object listener, Event event) {
        unregister(bus, listener, event.getClass());
    }

    private static synchronized void unregister(IEventBus bus, Object listener, Class<? extends Event> event) {
        toUnregister.add(Triple.of(bus, listener, event));
    }

    private static void onLoadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            toUnregister.forEach(t -> {
                t.getLeft().unregister(t.getMiddle());
            });
            toUnregister.clear();
        });
    }
}
