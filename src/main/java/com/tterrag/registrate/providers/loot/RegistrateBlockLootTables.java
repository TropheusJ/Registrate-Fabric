//package com.tterrag.registrate.providers.loot;
//
//import java.util.function.Consumer;
//import java.util.function.Supplier;
//import java.util.stream.Collectors;
//
//import com.tterrag.registrate.AbstractRegistrate;
//
//import net.minecraft.block.Block;
//import net.minecraft.data.server.BlockLootTableGenerator;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemConvertible;
//import net.minecraft.loot.LootTable;
//import net.minecraft.loot.LootTableRange;
//import net.minecraft.loot.condition.LootCondition;
//import net.minecraft.loot.condition.LootConditionConsumingBuilder;
//import net.minecraft.loot.entry.LootPoolEntry;
//import net.minecraft.loot.function.LootFunctionConsumingBuilder;
//
//public class RegistrateBlockLootTables extends BlockLootTableGenerator implements RegistrateLootTables {
//    private final AbstractRegistrate<?> parent;
//    private final Consumer<RegistrateBlockLootTables> callback;
//
//    @Override
//    protected void addTables() {
//        callback.accept(this);
//    }
//
//    @Override
//    protected Iterable<Block> getKnownBlocks() {
//        return parent.getAll(Block.class).stream().map(Supplier::get).collect(Collectors.toList());
//    }
//
//    // @formatter:off
//    // GENERATED START
//    public static <T> T applyExplosionDecay(ItemConvertible item, LootFunctionConsumingBuilder<T> function) {
//        return BlockLootTableGenerator.applyExplosionDecay(item, function);
//    }
//
//    public static <T> T addSurvivesExplosionCondition(ItemConvertible item, LootConditionConsumingBuilder<T> condition) {
//        return BlockLootTableGenerator.addSurvivesExplosionCondition(item, condition);
//    }
//
//    public static LootTable.Builder drops(ItemConvertible item) {
//        return BlockLootTableGenerator.drops(item);
//    }
//
//    public static LootTable.Builder drops(Block block, LootCondition.Builder conditionBuilder, LootPoolEntry.Builder<?> p_218494_2_) {
//        return BlockLootTableGenerator.drops(block, conditionBuilder, p_218494_2_);
//    }
//
//    public static LootTable.Builder dropsWithSilkTouch(Block block, LootPoolEntry.Builder<?> builder) {
//        return BlockLootTableGenerator.dropsWithSilkTouch(block, builder);
//    }
//
//    public static LootTable.Builder dropsWithShears(Block block, LootPoolEntry.Builder<?> noShearAlternativeEntry) {
//        return BlockLootTableGenerator.dropsWithShears(block, noShearAlternativeEntry);
//    }
//
//    public static LootTable.Builder dropsWithSilkTouchOrShears(Block block, LootPoolEntry.Builder<?> alternativeLootEntry) {
//        return BlockLootTableGenerator.dropsWithSilkTouchOrShears(block, alternativeLootEntry);
//    }
//
//    public static LootTable.Builder drops(Block block, ItemConvertible noSilkTouch) {
//        return BlockLootTableGenerator.drops(block, noSilkTouch);
//    }
//
//    public static LootTable.Builder drops(ItemConvertible item, LootTableRange range) {
//        return BlockLootTableGenerator.drops(item, range);
//    }
//
//    public static LootTable.Builder drops(Block block, ItemConvertible item, LootTableRange range) {
//        return BlockLootTableGenerator.drops(block, item, range);
//    }
//
//    public static LootTable.Builder dropsWithSilkTouch(ItemConvertible item) {
//        return BlockLootTableGenerator.dropsWithSilkTouch(item);
//    }
//
//    public static LootTable.Builder pottedPlantDrops(ItemConvertible flower) {
//        return BlockLootTableGenerator.pottedPlantDrops(flower);
//    }
//
//    public static LootTable.Builder slabDrops(Block slab) {
//        return BlockLootTableGenerator.slabDrops(slab);
//    }
//
//    public static LootTable.Builder nameableContainerDrops(Block block) {
//        return BlockLootTableGenerator.nameableContainerDrops(block);
//    }
//
//    public static LootTable.Builder shulkerBoxDrops(Block shulker) {
//        return BlockLootTableGenerator.shulkerBoxDrops(shulker);
//    }
//
//    public static LootTable.Builder bannerDrops(Block banner) {
//        return BlockLootTableGenerator.bannerDrops(banner);
//    }
//
//    public static LootTable.Builder oreDrops(Block block, Item item) {
//        return BlockLootTableGenerator.oreDrops(block, item);
//    }
//
//    public static LootTable.Builder mushroomBlockDrops(Block block, ItemConvertible item) {
//        return BlockLootTableGenerator.mushroomBlockDrops(block, item);
//    }
//
//    public static LootTable.Builder grassDrops(Block block) {
//        return BlockLootTableGenerator.grassDrops(block);
//    }
//
//    public static LootTable.Builder cropStemDrops(Block stemFruit, Item item) {
//        return BlockLootTableGenerator.cropStemDrops(stemFruit, item);
//    }
//
//    public static LootTable.Builder dropsWithShears(ItemConvertible item) {
//        return BlockLootTableGenerator.dropsWithShears(item);
//    }
//
//    public static LootTable.Builder leavesDrop(Block block, Block sapling, float... chances) {
//        return BlockLootTableGenerator.leavesDrop(block, sapling, chances);
//    }
//
//    public static LootTable.Builder oakLeavesDrop(Block block, Block sapling, float... chances) {
//        return BlockLootTableGenerator.oakLeavesDrop(block, sapling, chances);
//    }
//
//    public static LootTable.Builder cropDrops(Block block, Item itemConditional, Item withBonus, LootCondition.Builder conditionBuilder) {
//        return BlockLootTableGenerator.cropDrops(block, itemConditional, withBonus, conditionBuilder);
//    }
//
//    @Override
//    public void addDrop(Block blockIn, LootTable.Builder table) {
//        super.addDrop(blockIn, table);
//    }
//
//    @javax.annotation.Generated("lombok")
//    public RegistrateBlockLootTables(final AbstractRegistrate<?> parent, final Consumer<RegistrateBlockLootTables> callback) {
//        this.parent = parent;
//        this.callback = callback;
//    }
//    // GENERATED END
//}
