//package com.tterrag.registrate.providers.loot;
//
//import java.util.function.Consumer;
//import java.util.function.Supplier;
//import java.util.stream.Collectors;
//
//import com.tterrag.registrate.AbstractRegistrate;
//
//import net.minecraft.data.server.EntityLootTableGenerator;
//import net.minecraft.entity.EntityType;
//import net.minecraft.entity.SpawnGroup;
//import net.minecraft.loot.LootTable;
//import net.minecraft.util.Identifier;
//
//public class RegistrateEntityLootTables extends EntityLootTableGenerator implements RegistrateLootTables {
//    private final AbstractRegistrate<?> parent;
//    private final Consumer<RegistrateEntityLootTables> callback;
//
//    @Override
//    protected void addTables() {
//        callback.accept(this);
//    }
//
//    @Override
//    protected Iterable<EntityType<?>> getKnownEntities() {
//        return parent.<EntityType<?>>getAll(EntityType.class).stream().map(Supplier::get).collect(Collectors.toList());
//    }
//
//    @Override
//    protected boolean isNonLiving(EntityType<?> entitytype) {
//        return entitytype.getSpawnGroup() == SpawnGroup.MISC; // TODO open this to customization?
//    }
//
//    // @formatter:off
//    // GENERATED START
//    @Override
//    public void register(EntityType<?> type, LootTable.Builder table) {
//        super.register(type, table);
//    }
//
//    @Override
//    public void register(Identifier id, LootTable.Builder table) {
//        super.register(id, table);
//    }
//
//    @javax.annotation.Generated("lombok")
//    public RegistrateEntityLootTables(final AbstractRegistrate<?> parent, final Consumer<RegistrateEntityLootTables> callback) {
//        this.parent = parent;
//        this.callback = callback;
//    }
//    // GENERATED END
//}
