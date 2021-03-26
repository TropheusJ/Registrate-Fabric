package com.tterrag.registrate.providers.loot;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.util.Identifier;

public interface RegistrateLootTables extends Consumer<BiConsumer<Identifier, LootTable.Builder>> {

    default void validate(Map<Identifier, LootTable> map, LootTableReporter validationresults) {}
    
}
