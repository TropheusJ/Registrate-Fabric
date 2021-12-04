package com.tterrag.registrate.mixin;

import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.file.Path;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;

@Mixin(LootTableProvider.class)
public interface LootTableProviderAccessor {
    @Accessor
    static Gson getGSON() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Logger getLOGGER() {
        throw new UnsupportedOperationException();
    }

    @Invoker
    static Path callCreatePath(Path path, ResourceLocation resourceLocation) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    DataGenerator getGenerator();
}
