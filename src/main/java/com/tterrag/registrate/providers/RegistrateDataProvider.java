package com.tterrag.registrate.providers;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.DebugMarkers;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.fabricmc.api.EnvType;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;

public class RegistrateDataProvider implements DataProvider {
    @javax.annotation.Generated("lombok")
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(RegistrateDataProvider.class);
    @SuppressWarnings("null")
    static final BiMap<String, ProviderType<?>> TYPES = HashBiMap.create();

    @Nullable
    public static String getTypeName(ProviderType<?> type) {
        return TYPES.inverse().get(type);
    }

    private final String mod;
    private final Map<ProviderType<?>, RegistrateProvider> subProviders = new LinkedHashMap<>();

    public RegistrateDataProvider(AbstractRegistrate<?> parent, String modid, FabricDataGenerator generator) {
        this.mod = modid;
        EnumSet<EnvType> sides = EnumSet.noneOf(EnvType.class);
//        if (event.includeServer()) {
            sides.add(EnvType.SERVER);
//        }
//        if (event.includeClient()) {
            sides.add(EnvType.CLIENT);
//        }
        log.debug(DebugMarkers.DATA, "Gathering providers for sides: {}", sides);
        Map<ProviderType<?>, RegistrateProvider> known = new HashMap<>();
        for (String id : TYPES.keySet()) {
            ProviderType<?> type = TYPES.get(id);
            RegistrateProvider prov = type.create(parent, generator, known);
            known.put(type, prov);
            if (sides.contains(prov.getSide())) {
                log.debug(DebugMarkers.DATA, "Adding provider for type: {}", id);
                subProviders.put(type, prov);
            }
        }
    }

    @Override
    public void run(HashCache cache) throws IOException {
        for (Map.Entry<@NonnullType ProviderType<?>, RegistrateProvider> e : subProviders.entrySet()) {
            log.debug(DebugMarkers.DATA, "Generating data for type: {}", getTypeName(e.getKey()));
            e.getValue().run(cache);
        }
    }

    @Override
    public String getName() {
        return "Registrate Provider for " + mod + " [" + subProviders.values().stream().map(DataProvider::getName).collect(Collectors.joining(", ")) + "]";
    }

    @SuppressWarnings("unchecked")
    public <P extends RegistrateProvider> Optional<P> getSubProvider(ProviderType<P> type) {
        return Optional.ofNullable((P) subProviders.get(type));
    }
}
