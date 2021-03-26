//package com.tterrag.registrate.providers;
//
//import static net.minecraft.data.server.RecipesProvider.saveRecipeAdvancement;
//
//import java.nio.file.Path;
//import java.util.function.Consumer;
//import java.util.function.Supplier;
//
//import javax.annotation.CheckReturnValue;
//
//import org.jetbrains.annotations.Nullable;
//
//import com.google.common.collect.ImmutableMap;
//import com.google.gson.JsonObject;
//import com.tterrag.registrate.AbstractRegistrate;
//import com.tterrag.registrate.util.DataIngredient;
//import com.tterrag.registrate.util.nullness.NonNullSupplier;
//
//import net.fabricmc.api.EnvType;
//import net.minecraft.advancement.criterion.EnterBlockCriterion;
//import net.minecraft.advancement.criterion.InventoryChangedCriterion;
//import net.minecraft.block.Block;
//import net.minecraft.data.DataCache;
//import net.minecraft.data.DataGenerator;
//import net.minecraft.data.server.RecipesProvider;
//import net.minecraft.data.server.recipe.CookingRecipeJsonFactory;
//import net.minecraft.data.server.recipe.RecipeJsonProvider;
//import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
//import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
//import net.minecraft.data.server.recipe.SingleItemRecipeJsonFactory;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemConvertible;
//import net.minecraft.predicate.item.ItemPredicate;
//import net.minecraft.recipe.CookingRecipeSerializer;
//import net.minecraft.recipe.RecipeSerializer;
//import net.minecraft.tag.Tag;
//import net.minecraft.util.Identifier;
//import net.minecraftforge.common.Tags;
//import net.minecraftforge.registries.IForgeRegistryEntry;
//
//public class RegistrateRecipeProvider extends RecipesProvider implements RegistrateProvider, Consumer<RecipeJsonProvider> {
//    
//    private final AbstractRegistrate<?> owner;
//
//    public RegistrateRecipeProvider(AbstractRegistrate<?> owner, DataGenerator generatorIn) {
//        super(generatorIn);
//        this.owner = owner;
//    }
//
//    @Override
//    public EnvType getSide() {
//        return EnvType.SERVER;
//    }
//    
//    @Nullable
//    private Consumer<RecipeJsonProvider> callback;
//    
//    @Override
//    public void accept(@Nullable RecipeJsonProvider t) {
//        if (callback == null) {
//            throw new IllegalStateException("Cannot accept recipes outside of a call to registerRecipes");
//        }
//        callback.accept(t);
//    }
//
//    @Override
//    protected void generate(Consumer<RecipeJsonProvider> consumer) {
//        this.callback = consumer;
//        owner.genData(ProviderType.RECIPE, this);
//        this.callback = null;
//    }
//    
//    public Identifier safeId(Identifier id) {
//        return new Identifier(owner.getModid(), safeName(id));
//    }
//
//    public Identifier safeId(DataIngredient source) {
//        return safeId(source.getId());
//    }
//
//    public Identifier safeId(IForgeRegistryEntry<?> registryEntry) {
//        return safeId(registryEntry.getRegistryName());
//    }
//
//    public String safeName(Identifier id) {
//        return id.getPath().replace('/', '_');
//    }
//
//    public String safeName(DataIngredient source) {
//        return safeName(source.getId());
//    }
//
//    public String safeName(IForgeRegistryEntry<?> registryEntry) {
//        return safeName(registryEntry.getRegistryName());
//    }
//
//    public static final int DEFAULT_SMELT_TIME = 200;
//    public static final int DEFAULT_BLAST_TIME = DEFAULT_SMELT_TIME / 2;
//    public static final int DEFAULT_SMOKE_TIME = DEFAULT_BLAST_TIME;
//    public static final int DEFAULT_CAMPFIRE_TIME = DEFAULT_SMELT_TIME * 3;
//    
//    private static final String SMELTING_NAME = "smelting";
//    @SuppressWarnings("null")
//    private static final ImmutableMap<CookingRecipeSerializer<?>, String> COOKING_TYPE_NAMES = ImmutableMap.<CookingRecipeSerializer<?>, String>builder()
//            .put(RecipeSerializer.SMELTING, SMELTING_NAME)
//            .put(RecipeSerializer.BLASTING, "blasting")
//            .put(RecipeSerializer.SMOKING, "smoking")
//            .put(RecipeSerializer.CAMPFIRE_COOKING, "campfire")
//            .build();
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void cooking(DataIngredient source, Supplier<? extends T> result, float experience, int cookingTime, CookingRecipeSerializer<?> serializer) {
//        cooking(source, result, experience, cookingTime, COOKING_TYPE_NAMES.get(serializer), serializer);
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void cooking(DataIngredient source, Supplier<? extends T> result, float experience, int cookingTime, String typeName, CookingRecipeSerializer<?> serializer) {
//        CookingRecipeJsonFactory.create(source, result.get(), experience, cookingTime, serializer)
//            .criterion("has_" + safeName(source), source.getCritereon(this))
//            .offerTo(this, safeId(result.get()) + "_from_" + safeName(source) + "_" + typeName);
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void smelting(DataIngredient source, Supplier<? extends T> result, float experience) {
//        smelting(source, result, experience, DEFAULT_SMELT_TIME);
//    }
//
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void smelting(DataIngredient source, Supplier<? extends T> result, float experience, int cookingTime) {
//        cooking(source, result, experience, cookingTime, RecipeSerializer.SMELTING);
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void blasting(DataIngredient source, Supplier<? extends T> result, float experience) {
//        blasting(source, result, experience, DEFAULT_BLAST_TIME);
//    }
//
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void blasting(DataIngredient source, Supplier<? extends T> result, float experience, int cookingTime) {
//        cooking(source, result, experience, cookingTime, RecipeSerializer.BLASTING);
//    }
//
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void smoking(DataIngredient source, Supplier<? extends T> result, float experience) {
//        smoking(source, result, experience, DEFAULT_SMOKE_TIME);
//    }
//
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void smoking(DataIngredient source, Supplier<? extends T> result, float experience, int cookingTime) {
//        cooking(source, result, experience, cookingTime, RecipeSerializer.SMOKING);
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void campfire(DataIngredient source, Supplier<? extends T> result, float experience) {
//        campfire(source, result, experience, DEFAULT_CAMPFIRE_TIME);
//    }
//
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void campfire(DataIngredient source, Supplier<? extends T> result, float experience, int cookingTime) {
//        cooking(source, result, experience, cookingTime, RecipeSerializer.CAMPFIRE_COOKING);
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void stonecutting(DataIngredient source, Supplier<? extends T> result) {
//        stonecutting(source, result, 1);
//    }
//
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void stonecutting(DataIngredient source, Supplier<? extends T> result, int resultAmount) {
//        SingleItemRecipeJsonFactory.create(source, result.get(), resultAmount)
//            .create("has_" + safeName(source), source.getCritereon(this))
//            .offerTo(this, safeId(result.get()) + "_from_" + safeName(source) + "_stonecutting");
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void smeltingAndBlasting(DataIngredient source, Supplier<? extends T> result, float xp) {
//        smelting(source, result, xp);
//        blasting(source, result, xp);
//    }
//
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void food(DataIngredient source, Supplier<? extends T> result, float xp) {
//        smelting(source, result, xp);
//        smoking(source, result, xp);
//        campfire(source, result, xp);
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void square(DataIngredient source, Supplier<? extends T> output, boolean small) {
//        ShapedRecipeJsonFactory builder = ShapedRecipeJsonFactory.create(output.get())
//                .input('X', source);
//        if (small) {
//            builder.pattern("XX").pattern("XX");
//        } else {
//            builder.pattern("XXX").pattern("XXX").pattern("XXX");
//        }
//        builder.criterion("has_" + safeName(source), source.getCritereon(this))
//            .offerTo(this, safeId(output.get()));
//    }
//
//    /**
//     * @param <T>
//     * @param source
//     * @param output
//     * @deprecated Broken, use {@link #storage(NonNullSupplier, NonNullSupplier)} or {@link #storage(DataIngredient, NonNullSupplier, DataIngredient, NonNullSupplier)}.
//     */
//    @Deprecated
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void storage(DataIngredient source, NonNullSupplier<? extends T> output) {
//        square(source, output, false);
//        // This is backwards, but leaving in for binary compat
//        singleItemUnfinished(source, output, 1, 9)
//            .offerTo(this, safeId(source) + "_from_" + safeName(output.get()));
//    }
//
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void storage(NonNullSupplier<? extends T> source, NonNullSupplier<? extends T> output) {
//        storage(DataIngredient.items(source), source, DataIngredient.items(output), output);
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void storage(DataIngredient sourceIngredient, NonNullSupplier<? extends T> source, DataIngredient outputIngredient, NonNullSupplier<? extends T> output) {
//        square(sourceIngredient, output, false);
//        singleItemUnfinished(outputIngredient, source, 1, 9)
//            .offerTo(this, safeId(sourceIngredient) + "_from_" + safeName(output.get()));
//    }
//
//    @CheckReturnValue
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> ShapelessRecipeJsonFactory singleItemUnfinished(DataIngredient source, Supplier<? extends T> result, int required, int amount) {
//        return ShapelessRecipeJsonFactory.create(result.get(), amount)
//            .input(source, required)
//            .criterion("has_" + safeName(source), source.getCritereon(this));
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void singleItem(DataIngredient source, Supplier<? extends T> result, int required, int amount) {
//        singleItemUnfinished(source, result, required, amount).offerTo(this, safeId(result.get()));
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void planks(DataIngredient source, Supplier<? extends T> result) {
//        singleItemUnfinished(source, result, 1, 4)
//            .group("planks")
//            .offerTo(this, safeId(result.get()));
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void stairs(DataIngredient source, Supplier<? extends T> result, @Nullable String group, boolean stone) {
//        ShapedRecipeJsonFactory.create(result.get(), 4)
//            .pattern("X  ").pattern("XX ").pattern("XXX")
//            .input('X', source)
//            .group(group)
//            .criterion("has_" + safeName(source), source.getCritereon(this))
//            .offerTo(this, safeId(result.get()));
//        if (stone) {
//            stonecutting(source, result);
//        }
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void slab(DataIngredient source, Supplier<? extends T> result, @Nullable String group, boolean stone) {
//        ShapedRecipeJsonFactory.create(result.get(), 6)
//            .pattern("XXX")
//            .input('X', source)
//            .group(group)
//            .criterion("has_" + safeName(source), source.getCritereon(this))
//            .offerTo(this, safeId(result.get()));
//        if (stone) {
//            stonecutting(source, result, 2);
//        }
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void fence(DataIngredient source, Supplier<? extends T> result, @Nullable String group) {
//        ShapedRecipeJsonFactory.create(result.get(), 3)
//            .pattern("W#W").pattern("W#W")
//            .input('W', source)
//            .input('#', Tags.Items.RODS_WOODEN)
//            .group(group)
//            .criterion("has_" + safeName(source), source.getCritereon(this))
//            .offerTo(this, safeId(result.get()));
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void fenceGate(DataIngredient source, Supplier<? extends T> result, @Nullable String group) {
//        ShapedRecipeJsonFactory.create(result.get())
//            .pattern("#W#").pattern("#W#")
//            .input('W', source)
//            .input('#', Tags.Items.RODS_WOODEN)
//            .group(group)
//            .criterion("has_" + safeName(source), source.getCritereon(this))
//            .offerTo(this, safeId(result.get()));
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void wall(DataIngredient source, Supplier<? extends T> result) {
//        ShapedRecipeJsonFactory.create(result.get(), 6)
//            .pattern("XXX").pattern("XXX")
//            .input('X', source)
//            .criterion("has_" + safeName(source), source.getCritereon(this))
//            .offerTo(this, safeId(result.get()));
//        stonecutting(source, result);
//    }
//    
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void door(DataIngredient source, Supplier<? extends T> result, @Nullable String group) {
//        ShapedRecipeJsonFactory.create(result.get(), 3)
//            .pattern("XX").pattern("XX").pattern("XX")
//            .input('X', source)
//            .group(group)
//            .criterion("has_" + safeName(source), source.getCritereon(this))
//            .offerTo(this, safeId(result.get()));
//    }
//
//    public <T extends ItemConvertible & IForgeRegistryEntry<?>> void trapDoor(DataIngredient source, Supplier<? extends T> result, @Nullable String group) {
//        ShapedRecipeJsonFactory.create(result.get(), 2)
//            .pattern("XXX").pattern("XXX")
//            .input('X', source)
//            .group(group)
//            .criterion("has_" + safeName(source), source.getCritereon(this))
//            .offerTo(this, safeId(result.get()));
//    }
//
//    // @formatter:off
//    // GENERATED START
//
//    @Override
//    public void saveRecipeAdvancement(DataCache cache, JsonObject cache2, Path advancementJson) { super.saveRecipeAdvancement(cache, cache2, advancementJson); }
//
//    public static EnterBlockCriterion.Conditions requireEnteringFluid(Block block) { return RecipesProvider.requireEnteringFluid(block); }
//
//    public static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible item) { return RecipesProvider.conditionsFromItem(item); }
//
//    public static InventoryChangedCriterion.Conditions conditionsFromTag(Tag<Item> tag) { return RecipesProvider.conditionsFromTag(tag); }
//
//    public static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate... predicate) { return RecipesProvider.conditionsFromItemPredicates(predicate); }
//
//    // GENERATED END
//}
