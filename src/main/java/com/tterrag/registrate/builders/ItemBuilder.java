package com.tterrag.registrate.builders;

import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tag.Tag.Identified;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.fabric.EnvExecutor;
import com.tterrag.registrate.fabric.RegistryObject;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

/**
 * A builder for items, allows for customization of the {@link FabricItemSettings} and configuration of data associated with items (models, recipes, etc.).
 * 
 * @param <T>
 *            The type of item being built
 * @param <P>
 *            Parent object type
 */
public class ItemBuilder<T extends Item, P> extends AbstractBuilder<Item, T, P, ItemBuilder<T, P>> {

    /**
     * Create a new {@link ItemBuilder} and configure data. Used in lieu of adding side-effects to constructor, so that alternate initialization strategies can be done in subclasses.
     * <p>
     * The item will be assigned the following data:
     * <ul>
     * <li>A simple generated model with one texture (via {@link #defaultModel()})</li>
     * <li>The default translation (via {@link #defaultLang()})</li>
     * </ul>
     * 
     * @param <T>
     *            The type of the builder
     * @param <P>
     *            Parent object type
     * @param owner
     *            The owning {@link AbstractRegistrate} object
     * @param parent
     *            The parent object
     * @param name
     *            Name of the entry being built
     * @param callback
     *            A callback used to actually register the built entry
     * @param factory
     *            Factory to create the item
     * @return A new {@link ItemBuilder} with reasonable default data generators.
     */
    public static <T extends Item, P> ItemBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<FabricItemSettings, T> factory) {
        return create(owner, parent, name, callback, factory, null);
    }
    
    /**
     * Create a new {@link ItemBuilder} and configure data. Used in lieu of adding side-effects to constructor, so that alternate initialization strategies can be done in subclasses.
     * <p>
     * The item will be assigned the following data:
     * <ul>
     * <li>A simple generated model with one texture (via {@link #defaultModel()})</li>
     * <li>The default translation (via {@link #defaultLang()})</li>
     * <li>An {@link ItemGroup} set in the properties from the group supplier parameter, if non-null</li>
     * </ul>
     * 
     * @param <T>
     *            The type of the builder
     * @param <P>
     *            Parent object type
     * @param owner
     *            The owning {@link AbstractRegistrate} object
     * @param parent
     *            The parent object
     * @param name
     *            Name of the entry being built
     * @param callback
     *            A callback used to actually register the built entry
     * @param factory
     *            Factory to create the item
     * @param group
     *            The {@link ItemGroup} for the object, can be null for none
     * @return A new {@link ItemBuilder} with reasonable default data generators.
     */
    public static <T extends Item, P> ItemBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<FabricItemSettings, T> factory, @Nullable NonNullSupplier<? extends ItemGroup> group) {
        return new ItemBuilder<>(owner, parent, name, callback, factory)
                .defaultModel().defaultLang()
                .transform(ib -> group == null ? ib : ib.group(group));
    }

    private final NonNullFunction<FabricItemSettings, T> factory;
    
    private NonNullSupplier<FabricItemSettings> initialProperties = FabricItemSettings::new;
    private NonNullFunction<FabricItemSettings, FabricItemSettings> propertiesCallback = NonNullUnaryOperator.identity();
    
    @Nullable
    private NonNullSupplier<Supplier<ItemColorProvider>> colorHandler;
    
    protected ItemBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<FabricItemSettings, T> factory) {
        super(owner, parent, name, callback, Item.class);
        this.factory = factory;
    }

    /**
     * Modify the properties of the item. Modifications are done lazily, but the passed function is composed with the current one, and as such this method can be called multiple times to perform
     * different operations.
     * <p>
     * If a different properties instance is returned, it will replace the existing one entirely.
     * 
     * @param func
     *            The action to perform on the properties
     * @return this {@link ItemBuilder}
     */
    public ItemBuilder<T, P> properties(NonNullUnaryOperator<FabricItemSettings> func) {
        propertiesCallback = propertiesCallback.andThen(func);
        return this;
    }

    /**
     * Replace the initial state of the item properties, without replacing or removing any modifications done via {@link #properties(NonNullUnaryOperator)}.
     * 
     * @param properties
     *            A supplier to to create the initial properties
     * @return this {@link ItemBuilder}
     */
    public ItemBuilder<T, P> initialProperties(NonNullSupplier<FabricItemSettings> properties) {
        initialProperties = properties;
        return this;
    }

    public ItemBuilder<T, P> group(NonNullSupplier<? extends ItemGroup> group) {
        return properties(p -> p.group(group.get()));
    }
    
    /**
     * Register a block color handler for this item. The {@link ItemColorProvider} instance can be shared across many items.
     * 
     * @param colorHandler
     *            The color handler to register for this item
     * @return this {@link ItemBuilder}
     */
    public ItemBuilder<T, P> color(NonNullSupplier<Supplier<ItemColorProvider>> colorHandler) {
        if (this.colorHandler == null) {
            EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::registerItemColor);
        }
        this.colorHandler = colorHandler;
        return this;
    }
    
    protected void registerItemColor() {
//        OneTimeEventReceiver.addModListener(ColorHandlerEvent.Item.class, e -> {
//            NonNullSupplier<Supplier<ItemColorProvider>> colorHandler = this.colorHandler;
//            if (colorHandler != null) {
//                e.getItemColors().register(colorHandler.get().get(), getEntry());
//            }
//        });
    	ColorProviderRegistry.ITEM.register(colorHandler.get().get(), getEntry());
    }
    
    /**
     * Assign the default model to this item, which is simply a generated model with a single texture of the same name.
     * 
     * @return this {@link ItemBuilder}
     */
    public ItemBuilder<T, P> defaultModel() {
//        return model((ctx, prov) -> prov.generated(ctx::getEntry));
    	return this;
    }

    /**
     * Configure the model for this item.
     * 
     * @param cons
     *            The callback which will be invoked during data creation
     * @return this {@link ItemBuilder}
     * @see #setData(ProviderType, NonNullBiConsumer)
     */
//    public ItemBuilder<T, P> model(NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> cons) {
//        return setData(ProviderType.ITEM_MODEL, cons);
//    }
    
    /**
     * Assign the default translation, as specified by {@link RegistrateLangProvider#getAutomaticName(NonNullSupplier)}. This is the default, so it is generally not necessary to call, unless for undoing
     * previous changes.
     * 
     * @return this {@link ItemBuilder}
     */
    public ItemBuilder<T, P> defaultLang() {
        return lang(Item::getTranslationKey);
    }
    
    /**
     * Set the translation for this item.
     * 
     * @param name
     *            A localized English name
     * @return this {@link ItemBuilder}
     */
    public ItemBuilder<T, P> lang(String name) {
        return lang(Item::getTranslationKey, name);
    }

    /**
     * Configure the recipe(s) for this item.
     * 
     * @param cons
     *            The callback which will be invoked during data generation.
     * @return this {@link ItemBuilder}
     * @see #setData(ProviderType, NonNullBiConsumer)
     */
//    public ItemBuilder<T, P> recipe(NonNullBiConsumer<DataGenContext<Item, T>, RegistrateRecipeProvider> cons) {
//        return setData(ProviderType.RECIPE, cons);
//    }
    
    /**
     * Assign {@link Identified}{@code s} to this item. Multiple calls will add additional tags.
     * 
     * @param tags
     *            The tag to assign
     * @return this {@link ItemBuilder}
     */
    @SafeVarargs
    public final ItemBuilder<T, P> tag(Identified<Item>... tags) {
//        return tag(ProviderType.ITEM_TAGS, tags);
    	return this;
    }
    
    @Override
    protected T createEntry() {
        FabricItemSettings properties = this.initialProperties.get();
        properties = propertiesCallback.apply(properties);
        return factory.apply(properties);
    }
    
    @Override
    protected RegistryEntry<T> createEntryWrapper(RegistryObject<T> delegate) {
        return new ItemEntry<>(getOwner(), delegate);
    }
    
    @Override
    public ItemEntry<T> register() {
        return (ItemEntry<T>) super.register();
    }
}