package com.tterrag.registrate.fabric;

import java.util.function.Consumer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.particle.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatType;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStep.Carver;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.placer.BlockPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.tree.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import net.minecraft.world.poi.PointOfInterestType;

public class RegistryUtil {
	private static BiMap<Registry<?>, Class<?>> registryMap;

	@SuppressWarnings("unchecked")
	public static <T> Registry<T> getRegistry(Class<T> clazz) {
		return (Registry<T>) getRegistryMap().inverse().get(clazz);
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getRegistrationClass(Registry<T> registry) {
		return (Class<T>) getRegistryMap().get(registry);
	}

	public static BiMap<Registry<?>, Class<?>> getRegistryMap() {
		if (registryMap == null) {
			registryMap = createDefaultRegistryMap();
		}
		return registryMap;
	}

	public static BiMap<Registry<?>, Class<?>> createDefaultRegistryMap() {
		BiMap<Registry<?>, Class<?>> map = HashBiMap.create();

		// Fluid before Block because of FluidBlock ctor
		map.put(Registry.FLUID, Fluid.class);
		map.put(Registry.BLOCK, Block.class);
		map.put(Registry.ITEM, Item.class);

		map.put(Registry.ACTIVITY, Activity.class);
		map.put(Registry.ATTRIBUTE, EntityAttribute.class);
//		map.put(Registry.BIOME_SOURCE, Codec.class);
		map.put(Registry.BLOCK_ENTITY_TYPE, BlockEntityType.class);
		map.put(Registry.BLOCK_PLACER_TYPE, BlockPlacerType.class);
		map.put(Registry.BLOCK_STATE_PROVIDER_TYPE, BlockStateProviderType.class);
		map.put(Registry.CARVER, Carver.class);
//		map.put(Registry.CHUNK_GENERATOR, Codec.class);
//		map.put(Registry.CUSTOM_STAT, Identifier.class);
		map.put(Registry.DECORATOR, Decorator.class);
		map.put(Registry.ENCHANTMENT, Enchantment.class);
		map.put(Registry.FEATURE, Feature.class);
		map.put(Registry.FEATURE_SIZE_TYPE, FeatureSizeType.class);
		map.put(Registry.FOLIAGE_PLACER_TYPE, FoliagePlacerType.class);
		map.put(Registry.LOOT_CONDITION_TYPE, LootConditionType.class);
		map.put(Registry.LOOT_FUNCTION_TYPE, LootFunctionType.class);
		map.put(Registry.LOOT_POOL_ENTRY_TYPE, LootPoolEntryType.class);
		map.put(Registry.PARTICLE_TYPE, ParticleType.class);
		map.put(Registry.POS_RULE_TEST, PosRuleTestType.class);
		map.put(Registry.RECIPE_SERIALIZER, RecipeSerializer.class);
		map.put(Registry.RECIPE_TYPE, RecipeType.class);
		map.put(Registry.REGISTRIES, Registry.class);
		map.put(Registry.RULE_TEST, RuleTestType.class);
		map.put(Registry.SCHEDULE, Schedule.class);
		map.put(Registry.SCREEN_HANDLER, ScreenHandlerType.class);
		map.put(Registry.SOUND_EVENT, SoundEvent.class);
		map.put(Registry.STAT_TYPE, StatType.class);
		map.put(Registry.STATUS_EFFECT, StatusEffect.class);
		map.put(Registry.STRUCTURE_FEATURE, StructureFeature.class);
		map.put(Registry.STRUCTURE_PIECE, StructurePieceType.class);
		map.put(Registry.STRUCTURE_POOL_ELEMENT, StructurePoolElementType.class);
		map.put(Registry.STRUCTURE_PROCESSOR, StructureProcessorType.class);
		map.put(Registry.SURFACE_BUILDER, SurfaceBuilder.class);
		map.put(Registry.TREE_DECORATOR_TYPE, TreeDecoratorType.class);
		map.put(Registry.TRUNK_PLACER_TYPE, TrunkPlacerType.class);
//		map.put(Registry.BLOCK, Block.class);
		map.put(Registry.CHUNK_STATUS, ChunkStatus.class);
		map.put(Registry.ENTITY_TYPE, EntityType.class);
//		map.put(Registry.FLUID, Fluid.class);
//		map.put(Registry.ITEM, Item.class);
		map.put(Registry.MEMORY_MODULE_TYPE, MemoryModuleType.class);
		map.put(Registry.PAINTING_MOTIVE, PaintingMotive.class);
		map.put(Registry.POINT_OF_INTEREST_TYPE, PointOfInterestType.class);
		map.put(Registry.POTION, Potion.class);
		map.put(Registry.SENSOR_TYPE, SensorType.class);
		map.put(Registry.VILLAGER_PROFESSION, VillagerProfession.class);
		map.put(Registry.VILLAGER_TYPE, VillagerType.class);

		return map;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Identifier getId(Registry<?> registry) {
		return ((Registry) Registry.REGISTRIES).getId(registry);
	}

	public static void forAllRegistries(Consumer<Registry<?>> consumer) {
		// Fluid, Block, and Item need to run first
		getRegistryMap().keySet().forEach(consumer);
//		Registry.REGISTRIES.forEach(consumer);
	}
}
