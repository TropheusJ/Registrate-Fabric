package com.tterrag.registrate.builders;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.fabric.EnvExecutor;
import com.tterrag.registrate.fabric.RegistryObject;
import com.tterrag.registrate.util.LazySpawnEggItem;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

/**
 * A builder for entities, allows for customization of the {@link FabricEntityTypeBuilder}, easy creation of spawn egg items, and configuration of data associated with entities (loot tables, etc.).
 * 
 * @param <T>
 *            The type of entity being built
 * @param <P>
 *            Parent object type
 */
public class EntityBuilder<T extends Entity, B extends FabricEntityTypeBuilder<T>, P> extends AbstractBuilder<EntityType<?>, EntityType<T>, P, EntityBuilder<T, B, P>> {

    /**
     * Create a new {@link EntityBuilder} and configure data. Used in lieu of adding side-effects to constructor, so that alternate initialization strategies can be done in subclasses.
     * <p>
     * The entity will be assigned the following data:
     * <ul>
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
     *            Factory to create the entity
     * @param classification
     *            The {@link MobCategory} of the entity
     * @return A new {@link EntityBuilder} with reasonable default data generators.
     */
    public static <T extends Entity, P> EntityBuilder<T, FabricEntityTypeBuilder<T>, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, EntityType.EntityFactory<T> factory,
            MobCategory classification) {
        return new EntityBuilder<>(owner, parent, name, callback, factory, classification, FabricEntityTypeBuilder::create)
                .defaultLang();
    }
    
    public static <T extends LivingEntity, P> EntityBuilder<T, FabricEntityTypeBuilder.Living<T>, P> createLiving(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, EntityType.EntityFactory<T> factory,
            MobCategory classification) {
        return new EntityBuilder<>(owner, parent, name, callback, factory, classification, (s, f) -> FabricEntityTypeBuilder.createLiving().spawnGroup(s).entityFactory(f))
                .defaultLang();
    }
    
    public static <T extends Mob, P> EntityBuilder<T, FabricEntityTypeBuilder.Mob<T>, P> createMob(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, EntityType.EntityFactory<T> factory,
            MobCategory classification) {
        return new EntityBuilder<>(owner, parent, name, callback, factory, classification, (s, f) -> FabricEntityTypeBuilder.createMob().spawnGroup(s).entityFactory(f))
                .defaultLang();
    }

    private final NonNullSupplier<B> builder;
    
    private NonNullConsumer<B> builderCallback = $ -> {};
    
    @Nullable
    private NonNullSupplier<EntityRendererProvider<T>> renderer;
    
    private boolean spawnConfigured;
    
    private @Nullable ItemBuilder<LazySpawnEggItem<T>, EntityBuilder<T, B, P>> spawnEggBuilder;
    
    protected EntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, EntityType.EntityFactory<T> factory, MobCategory classification, NonNullBiFunction<MobCategory, EntityType.EntityFactory<T>, B> function) {
        super(owner, parent, name, callback, EntityType.class);
        this.builder = () -> function.apply(classification, factory);
    }

    /**
     * Modify the properties of the entity. Modifications are done lazily, but the passed function is composed with the current one, and as such this method can be called multiple times to perform
     * different operations.
     *
     * @param cons
     *            The action to perform on the properties
     * @return this {@link EntityBuilder}
     */
    public EntityBuilder<T, B, P> properties(NonNullConsumer<B> cons) {
        builderCallback = builderCallback.andThen(cons);
        return this;
    }

    /**
     * Register an {@link EntityRenderer} for this entity.
     * <p>
     * 
     * @apiNote This requires the {@link Class} of the entity object, which can only be gotten by inspecting an instance of it. Thus, the entity will be constructed with a {@code null} {@link Level}
     *          to register the renderer.
     * 
     * @param renderer
     *            A (server safe) supplier to an {@link EntityRendererProvider} that will provide this entity's renderer
     * @return this {@link EntityBuilder}
     */
    public EntityBuilder<T, B, P> renderer(NonNullSupplier<EntityRendererProvider<T>> renderer) {
        if (this.renderer == null) { // First call only
            EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::registerRenderer);
        }
        this.renderer = renderer;
        return this;
    }
    
    protected void registerRenderer() {
        if (renderer != null) {
            onRegister(entry -> {
                try {
                    EntityRendererRegistry.INSTANCE.register(entry, renderer.get());
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to register renderer for Entity " + get().getId(), e);
                }
            });
        }
    }
    
    /**
     * Register a spawn placement for this entity. The entity must extend {@link Mob} and allow construction with a {@code null} {@link Level}.
     * <p>
     * Cannot be called more than once per builder.
     * 
     * @param type
     *            The type of placement to use
     * @param heightmap
     *            Which heightmap to use to choose placement locations
     * @param predicate
     *            A predicate to check spawn locations for validity
     * @return this {@link EntityBuilder}
     * @throws IllegalStateException
     *             When called more than once
     */
    @Deprecated
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public EntityBuilder<T, B, P> spawnPlacement(Type type, Heightmap.Types heightmap, SpawnPredicate<T> predicate) {
        if (spawnConfigured) {
            throw new IllegalStateException("Cannot configure spawn placement more than once");
        }
        spawnConfigured = true;
        if (builder.get() instanceof FabricEntityTypeBuilder.Mob) {
            ((FabricEntityTypeBuilder.Mob) builder.get()).spawnRestriction(type, heightmap, predicate);
        }
        return this;
    }

    /**
     * Create a spawn egg item for this entity using the given colors, not allowing for any extra configuration.
     * 
     * @deprecated This does not work properly, see <a href="https://github.com/MinecraftForge/MinecraftForge/pull/6299">this issue</a>.
     *             <p>
     *             As a temporary measure, uses a custom egg class that imperfectly emulates the functionality
     * 
     * @param primaryColor
     *            The primary color of the egg
     * @param secondaryColor
     *            The secondary color of the egg
     * @return this {@link EntityBuilder}
     */
    @Deprecated
    public EntityBuilder<T, B, P> defaultSpawnEgg(int primaryColor, int secondaryColor) {
        return spawnEgg(primaryColor, secondaryColor).build();
    }

    /**
     * Create a spawn egg item for this entity using the given colors, and return the builder for further configuration.
     * 
     * @deprecated This does not work properly, see <a href="https://github.com/MinecraftForge/MinecraftForge/pull/6299">this issue</a>.
     *             <p>
     *             As a temporary measure, uses a custom egg class that imperfectly emulates the functionality
     * 
     * @param primaryColor
     *            The primary color of the egg
     * @param secondaryColor
     *            The secondary color of the egg
     * @return the {@link ItemBuilder} for the egg item
     */
    @Deprecated
    public ItemBuilder<? extends SpawnEggItem, EntityBuilder<T, B, P>> spawnEgg(int primaryColor, int secondaryColor) {
        ItemBuilder<LazySpawnEggItem<T>, EntityBuilder<T, B, P>> ret = getOwner().item(this, getName() + "_spawn_egg", p -> new LazySpawnEggItem<>(asSupplier(), primaryColor, secondaryColor, p)).properties(p -> (FabricItemSettings) p.tab(CreativeModeTab.TAB_MISC))
                /*.model((ctx, prov) -> prov.withExistingParent(ctx.getName(), new Identifier("item/template_spawn_egg")))*/;
        if (this.spawnEggBuilder == null) { // First call only
            this.onRegister(this::injectSpawnEggType);
        }
        this.spawnEggBuilder = ret;
        return ret;
    }

    /**
     * Assign the default translation, as specified by {@link RegistrateLangProvider#getAutomaticName(NonNullSupplier)}. This is the default, so it is generally not necessary to call, unless for undoing
     * previous changes.
     * 
     * @return this {@link EntityBuilder}
     */
    public EntityBuilder<T, B, P> defaultLang() {
        return lang(EntityType::getDescriptionId);
    }

    /**
     * Set the translation for this entity.
     * 
     * @param name
     *            A localized English name
     * @return this {@link EntityBuilder}
     */
    public EntityBuilder<T, B, P> lang(String name) {
        return lang(EntityType::getDescriptionId, name);
    }

    /**
     * Configure the loot table for this entity. This is different than most data gen callbacks as the callback does not accept a {@link DataGenContext}, but instead a
     * {@link RegistrateEntityLootTables}, for creating specifically entity loot tables.
     * 
     * @param cons
     *            The callback which will be invoked during entity loot table creation.
     * @return this {@link EntityBuilder}
     */
//    public EntityBuilder<T, P> loot(NonNullBiConsumer<RegistrateEntityLootTables, EntityType<T>> cons) {
//        return setData(ProviderType.LOOT, (ctx, prov) -> prov.addLootAction(LootType.ENTITY, tb -> cons.accept(tb, ctx.getEntry())));
//    }

    /**
     * Assign {@link Named}{@code s} to this entity. Multiple calls will add additional tags.
     * 
     * @param tags
     *            The tags to assign
     * @return this {@link EntityBuilder}
     */
    @SafeVarargs
    public final EntityBuilder<T, B, P> tag(Named<EntityType<?>>... tags) {
        return this/*tag(ProviderType.ENTITY_TAGS, tags)*/;
    }

    @Override
    protected EntityType<T> createEntry() {
        B builder = this.builder.get();
        builderCallback.accept(builder);
        return builder.build(/*getName()*/);
    }
   
    protected void injectSpawnEggType(EntityType<T> entry) {
        ItemBuilder<LazySpawnEggItem<T>, EntityBuilder<T, B, P>> spawnEggBuilder = this.spawnEggBuilder;
        if (spawnEggBuilder != null) {
            spawnEggBuilder.getEntry().injectType();
        }
    }

    @Override
    protected RegistryEntry<EntityType<T>> createEntryWrapper(RegistryObject<EntityType<T>> delegate) {
        return new EntityEntry<>(getOwner(), delegate);
    }

    @Override
    public EntityEntry<T> register() {
        return (EntityEntry<T>) super.register();
    }
}
