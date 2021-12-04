package com.tterrag.registrate.util;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.collect.ObjectArrays;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import lombok.Getter;
import lombok.experimental.Delegate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * A helper for data generation when using ingredients as input(s) to recipes.<br>
 * It remembers the name of the primary ingredient for use in creating recipe names/criteria.
 * <p>
 * Create an instance of this class with the various factory methods such as {@link #items(ItemLike, ItemLike...)} and {@link #tag(Named)}.
 * <p>
 * <strong>This class should not be used for any purpose other than data generation</strong>, it will throw an exception if it is serialized to a packet buffer.
 */
public final class DataIngredient extends Ingredient {

    private interface Excludes {

        void toNetwork(FriendlyByteBuf buffer);

        boolean isVanilla();
    }

    @Delegate(excludes = Excludes.class)
    private final Ingredient parent;
    private final ResourceLocation id;
    private final Function<RegistrateRecipeProvider, InventoryChangeTrigger.TriggerInstance> criteriaFactory;

    private DataIngredient(Ingredient parent, ItemLike item) {
        super(Stream.empty());
        this.parent = parent;
        this.id = Registry.ITEM.getKey(item.asItem());
        this.criteriaFactory = prov -> RegistrateRecipeProvider.has(item);
    }

    private DataIngredient(Ingredient parent, Named<Item> tag) {
        super(Stream.empty());
        this.parent = parent;
        this.id = tag.getName();
        this.criteriaFactory = prov -> RegistrateRecipeProvider.has(tag);
    }

    private DataIngredient(Ingredient parent, ResourceLocation id, ItemPredicate... predicates) {
        super(Stream.empty());
        this.parent = parent;
        this.id = id;
        this.criteriaFactory = prov -> RegistrateRecipeProvider.inventoryTrigger(predicates);
    }

    public InventoryChangeTrigger.TriggerInstance getCritereon(RegistrateRecipeProvider prov) {
        return criteriaFactory.apply(prov);
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T extends ItemLike> DataIngredient items(NonNullSupplier<? extends T> first, NonNullSupplier<? extends T>... others) {
        return items(first.get(), (T[]) Arrays.stream(others).map(Supplier::get).toArray(ItemLike[]::new));
    }

    @SafeVarargs
    public static <T extends ItemLike> DataIngredient items(T first, T... others) {
        return ingredient(Ingredient.of(ObjectArrays.concat(first, others)), first);
    }

    public static DataIngredient stacks(ItemStack first, ItemStack... others) {
        return ingredient(Ingredient.of(ObjectArrays.concat(first, others)), first.getItem());
    }

    public static DataIngredient tag(Named<Item> tag) {
        return ingredient(Ingredient.of(tag), tag);
    }

    public static DataIngredient ingredient(Ingredient parent, ItemLike required) {
        return new DataIngredient(parent, required);
    }

    public static DataIngredient ingredient(Ingredient parent, Named<Item> required) {
        return new DataIngredient(parent, required);
    }

    public static DataIngredient ingredient(Ingredient parent, ResourceLocation id, ItemPredicate... criteria) {
        return new DataIngredient(parent, id, criteria);
    }

    public ResourceLocation getId() {
        return id;
    }
}