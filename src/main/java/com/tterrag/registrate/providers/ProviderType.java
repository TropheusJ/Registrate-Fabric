package com.tterrag.registrate.providers;

import java.util.Map;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider;
import com.tterrag.registrate.util.nullness.FieldsAreNonnullByDefault;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * Represents a type of data that can be generated, and specifies a factory for the provider.
 * <p>
 * Used as a key for data generator callbacks.
 * <p>
 * This file also defines the built-in provider types, but third-party types can be created with {@link #register(String, ProviderType)}.
 *
 * @param <T>
 *            The type of the provider
 */
@FunctionalInterface
@SuppressWarnings("deprecation")
@FieldsAreNonnullByDefault
/*@ParametersAreNonnullByDefault*/
public interface ProviderType<T extends RegistrateProvider> {

    // SERVER DATA
    public static final ProviderType<RegistrateRecipeProvider> RECIPE = register("recipe", RegistrateRecipeProvider::new);
    public static final ProviderType<RegistrateAdvancementProvider> ADVANCEMENT = register("advancement", RegistrateAdvancementProvider::new);
    public static final ProviderType<RegistrateLootTableProvider> LOOT = register("loot", RegistrateLootTableProvider::new);
    public static final ProviderType<RegistrateTagsProvider<Block>> BLOCK_TAGS = register("tags/block", type -> (p, g) -> new RegistrateTagsProvider<Block>(p, type, "blocks", g, Registry.BLOCK));
    public static final ProviderType<RegistrateItemTagsProvider> ITEM_TAGS = registerDelegate("tags/item", type -> (p, g, existing) -> new RegistrateItemTagsProvider(p, type, "items", g, (RegistrateTagsProvider<Block>)existing.get(BLOCK_TAGS)));
    public static final ProviderType<RegistrateTagsProvider<Fluid>> FLUID_TAGS = register("tags/fluid", type -> (p, g) -> new RegistrateTagsProvider<Fluid>(p, type, "fluids", g, Registry.FLUID));
    public static final ProviderType<RegistrateTagsProvider<EntityType<?>>> ENTITY_TAGS = register("tags/entity", type -> (p, g) -> new RegistrateTagsProvider<EntityType<?>>(p, type, "entity_types", g, Registry.ENTITY_TYPE));

    // CLIENT DATA
    public static final ProviderType<RegistrateBlockstateProvider> BLOCKSTATE = register("blockstate", RegistrateBlockstateProvider::new);
    public static final ProviderType<RegistrateItemModelProvider> ITEM_MODEL = register("item_model", RegistrateItemModelProvider::new);
    public static final ProviderType<RegistrateLangProvider> LANG = register("lang", RegistrateLangProvider::new);

    T create(AbstractRegistrate<?> parent, FabricDataGenerator generator, Map<ProviderType<?>, RegistrateProvider> existing);

    // TODO this is clunky af
//    @Nonnull
    static <T extends RegistrateProvider> ProviderType<T> registerDelegate(String name, NonNullUnaryOperator<ProviderType<T>> type) {
        ProviderType<T> ret = new ProviderType<T>() {

            @Override
            public T create(/*@Nonnull */AbstractRegistrate<?> parent, FabricDataGenerator generator, Map<ProviderType<?>, RegistrateProvider> existing) {
                return type.apply(this).create(parent, generator, existing);
            }
        };
        return register(name, ret);
    }

//    @Nonnull
    static <T extends RegistrateProvider> ProviderType<T> register(String name, NonNullFunction<ProviderType<T>, NonNullBiFunction<AbstractRegistrate<?>, FabricDataGenerator, T>> type) {
        ProviderType<T> ret = new ProviderType<T>() {

            @Override
            public T create(/*@Nonnull */AbstractRegistrate<?> parent, FabricDataGenerator generator, Map<ProviderType<?>, RegistrateProvider> existing) {
                return type.apply(this).apply(parent, generator);
            }
        };
        return register(name, ret);
    }

//    @Nonnull
    static <T extends RegistrateProvider> ProviderType<T> register(String name, NonNullBiFunction<AbstractRegistrate<?>, FabricDataGenerator, T> type) {
        ProviderType<T> ret = new ProviderType<T>() {

            @Override
            public T create(AbstractRegistrate<?> parent, FabricDataGenerator generator, Map<ProviderType<?>, RegistrateProvider> existing) {
                return type.apply(parent, generator);
            }
        };
        return register(name, ret);
    }

//    @Nonnull
    static <T extends RegistrateProvider> ProviderType<T> register(String name, ProviderType<T> type) {
        RegistrateDataProvider.TYPES.put(name, type);
        return type;
    }
}
