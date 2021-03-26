//package com.tterrag.registrate.util;
//
//import java.util.Arrays;
//import java.util.function.Function;
//import java.util.function.Supplier;
//import java.util.stream.Stream;
//
//import com.google.common.collect.ObjectArrays;
//import com.tterrag.registrate.providers.RegistrateRecipeProvider;
//import com.tterrag.registrate.util.nullness.NonNullSupplier;
//
//import net.minecraft.advancement.criterion.InventoryChangedCriterion;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemConvertible;
//import net.minecraft.item.ItemStack;
//import net.minecraft.network.PacketByteBuf;
//import net.minecraft.predicate.item.ItemPredicate;
//import net.minecraft.recipe.Ingredient;
//import net.minecraft.tag.Tag.Identified;
//import net.minecraft.util.Identifier;
//import net.minecraftforge.common.crafting.IIngredientSerializer;
//import net.minecraftforge.registries.IForgeRegistryEntry;
//
///**
// * A helper for data generation when using ingredients as input(s) to recipes.<br>
// * It remembers the name of the primary ingredient for use in creating recipe names/criteria.
// * <p>
// * Create an instance of this class with the various factory methods such as {@link #items(ItemConvertible, ItemConvertible...)} and {@link #tag(Identified)}.
// * <p>
// * <strong>This class should not be used for any purpose other than data generation</strong>, it will throw an exception if it is serialized to a packet buffer.
// */
//public final class DataIngredient extends Ingredient {
//
//    private interface Excludes {
//        IIngredientSerializer<DataIngredient> getSerializer();
//
//        void write(PacketByteBuf buffer);
//
//        boolean isVanilla();
//    }
//
//    private final Ingredient parent;
//    private final Identifier id;
//    private final Function<RegistrateRecipeProvider, InventoryChangedCriterion.Conditions> criteriaFactory;
//
//    private DataIngredient(Ingredient parent, ItemConvertible item) {
//        super(Stream.empty());
//        this.parent = parent;
//        this.id = item.asItem().getRegistryName();
//        this.criteriaFactory = prov -> prov.conditionsFromItem(item);
//    }
//
//    private DataIngredient(Ingredient parent, Identified<Item> tag) {
//        super(Stream.empty());
//        this.parent = parent;
//        this.id = tag.getId();
//        this.criteriaFactory = prov -> prov.conditionsFromTag(tag);
//    }
//
//    private DataIngredient(Ingredient parent, Identifier id, ItemPredicate... predicates) {
//        super(Stream.empty());
//        this.parent = parent;
//        this.id = id;
//        this.criteriaFactory = prov -> prov.conditionsFromItemPredicates(predicates);
//    }
//
//    @Override
//    public IIngredientSerializer<DataIngredient> getSerializer() {
//        throw new UnsupportedOperationException("DataIngredient should only be used for data generation!");
//    }
//
//    public InventoryChangedCriterion.Conditions getCritereon(RegistrateRecipeProvider prov) {
//        return criteriaFactory.apply(prov);
//    }
//
//    @SuppressWarnings("unchecked")
//    @SafeVarargs
//    public static <T extends ItemConvertible & IForgeRegistryEntry<?>> DataIngredient items(NonNullSupplier<? extends T> first, NonNullSupplier<? extends T>... others) {
//        return items(first.get(), (T[]) Arrays.stream(others).map(Supplier::get).toArray(ItemConvertible[]::new));
//    }
//
//    @SafeVarargs
//    public static <T extends ItemConvertible & IForgeRegistryEntry<?>> DataIngredient items(T first, T... others) {
//        return ingredient(Ingredient.ofItems(ObjectArrays.concat(first, others)), first);
//    }
//
//    public static DataIngredient stacks(ItemStack first, ItemStack... others) {
//        return ingredient(Ingredient.ofStacks(ObjectArrays.concat(first, others)), first.getItem());
//    }
//
//    public static DataIngredient tag(Identified<Item> tag) {
//        return ingredient(Ingredient.fromTag(tag), tag);
//    }
//
//    public static DataIngredient ingredient(Ingredient parent, ItemConvertible required) {
//        return new DataIngredient(parent, required);
//    }
//
//    public static DataIngredient ingredient(Ingredient parent, Identified<Item> required) {
//        return new DataIngredient(parent, required);
//    }
//
//    public static DataIngredient ingredient(Ingredient parent, Identifier id, ItemPredicate... criteria) {
//        return new DataIngredient(parent, id, criteria);
//    }
//
//    @javax.annotation.Generated("lombok")
//    public Identifier getId() {
//        return this.id;
//    }
//
//    @javax.annotation.Generated("lombok")
//    public net.minecraft.item.ItemStack[] getMatchingStacksClient() {
//        return this.parent.getMatchingStacksClient();
//    }
//
//    @javax.annotation.Generated("lombok")
//    public boolean test(final net.minecraft.item.ItemStack p_test_1_) {
//        return this.parent.test(p_test_1_);
//    }
//
//    @javax.annotation.Generated("lombok")
//    public it.unimi.dsi.fastutil.ints.IntList getIds() {
//        return this.parent.getIds();
//    }
//
//    @javax.annotation.Generated("lombok")
//    public com.google.gson.JsonElement toJson() {
//        return this.parent.toJson();
//    }
//
//    @javax.annotation.Generated("lombok")
//    public boolean isEmpty() {
//        return this.parent.isEmpty();
//    }
//
//    @javax.annotation.Generated("lombok")
//    public boolean isSimple() {
//        return this.parent.isSimple();
//    }
//
//    @javax.annotation.Generated("lombok")
//    public java.util.function.Predicate<net.minecraft.item.ItemStack> and(final java.util.function.Predicate<? super net.minecraft.item.ItemStack> other) {
//        return this.parent.and(other);
//    }
//
//    @javax.annotation.Generated("lombok")
//    public java.util.function.Predicate<net.minecraft.item.ItemStack> negate() {
//        return this.parent.negate();
//    }
//
//    @javax.annotation.Generated("lombok")
//    public java.util.function.Predicate<net.minecraft.item.ItemStack> or(final java.util.function.Predicate<? super net.minecraft.item.ItemStack> other) {
//        return this.parent.or(other);
//    }
//}
