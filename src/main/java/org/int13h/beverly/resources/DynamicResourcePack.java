package org.int13h.beverly.resources;

import com.google.gson.JsonElement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.flag.FeatureFlagSet;
import org.int13h.beverly.Beverly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DynamicResourcePack implements PackResources {

    public static Pack getPack() {
        var info = new PackLocationInfo("cradle:dynamic", Component.literal("Cradle Resources"), PackSource.DEFAULT, Optional.empty());
        return new Pack(info, new Pack.ResourcesSupplier() {
            @Override
            public @NotNull PackResources openPrimary(@NotNull PackLocationInfo packLocationInfo) {
                return new DynamicResourcePack(info);
            }

            @Override
            public PackResources openFull(PackLocationInfo packLocationInfo, Pack.Metadata metadata) {
                return openPrimary(packLocationInfo);
            }
        },
        new Pack.Metadata(Component.literal("Cradle Resources"), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of()),
        new PackSelectionConfig(true, Pack.Position.BOTTOM, true));
    }

    protected static final Set<String> DOMAINS = new HashSet<>();
    protected static final ConcurrentMap<ResourceLocation, byte[]> DATA = new ConcurrentHashMap<>();

    private final PackLocationInfo info;

    static {
        DOMAINS.addAll(Set.of(Beverly.MODID, "minecraft", "neoforge"));
    }

    public DynamicResourcePack(final PackLocationInfo info) {
        this.info = info;
    }

    public static void clear() {
        DATA.clear();
    }

    public static void addItemModel(ResourceLocation location, JsonElement obj) {
        byte[] model = obj.toString().getBytes(StandardCharsets.UTF_8);
        ResourceLocation l = getItemModelLocation(location);
        DATA.put(l, model);
    }

    public static void addItemTexture(ResourceLocation location, byte[] data) {
        ResourceLocation l = getTextureLocation("item", location);
        DATA.put(l, data);
    }


    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String @NotNull ... strings) {
        return null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if(type == PackType.CLIENT_RESOURCES) {
            if(DATA.containsKey(location)) {
                return () -> new ByteArrayInputStream(DATA.get(location));
            }
        }
        return null;
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput resourceOutput) {
        if(type == PackType.CLIENT_RESOURCES) {
            if(!path.endsWith("/")) path += '/';
            final String finalPath = path;
            DATA.keySet().stream().filter(Objects::nonNull).filter(loc -> loc.getPath().startsWith(finalPath))
                    .forEach((id) -> {
                        IoSupplier<InputStream> resource = getResource(type, id);
                        if(resource != null) {
                            resourceOutput.accept(id, resource);
                        }
                    });
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return type == PackType.CLIENT_RESOURCES ? DOMAINS : Set.of();
    }

    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionSerializer<T> metadataSectionSerializer) throws IOException {
        return null;
    }

    @Override
    public PackLocationInfo location() {
        return info;
    }

    @Override
    public void close() {

    }

    public static ResourceLocation getItemModelLocation(ResourceLocation location) {
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), String.join("", "models/item/", location.getPath(), ".json"));
    }

    public static ResourceLocation getTextureLocation(String path, ResourceLocation location) {
        if(path == null) {
            return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), String.join("", "textures/", location.getPath(), ".png"));
        }
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(),
                String.join("", "textures/", path, "/", location.getPath(), ".png"));
    }
}
