package com.tterrag.registrate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.ContainerBuilder;
import com.tterrag.registrate.builders.ContainerBuilder.ContainerFactory;
import com.tterrag.registrate.builders.ContainerBuilder.ForgeContainerFactory;
import com.tterrag.registrate.builders.ContainerBuilder.ScreenFactory;
import com.tterrag.registrate.builders.EnchantmentBuilder;
import com.tterrag.registrate.builders.EnchantmentBuilder.EnchantmentFactory;
import com.tterrag.registrate.builders.EntityBuilder;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.builders.NoConfigBuilder;
import com.tterrag.registrate.builders.TileEntityBuilder;
import com.tterrag.registrate.fabric.RegistryObject;
import com.tterrag.registrate.fabric.RegistryUtil;
import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import com.tterrag.registrate.util.DebugMarkers;
import com.tterrag.registrate.util.NonNullLazyValue;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

/**
 * Manages all registrations and data generators for a mod.
 * <p>
 * Generally <em>not</em> thread-safe, as it holds the current name of the object being built statefully, and uses non-concurrent collections.
 * <p>
 * Begin a new object via {@link #object(String)}. This name will be used for all future entries until the next invocation of {@link #object(String)}. Alternatively, the methods that accept a name
 * parameter (such as {@link #block(String, NonNullFunction)}) can be used. These do not affect the current name state.
 * <p>
 * A simple use may look like:
 * 
 * <pre>
 * {@code
 * public static final Registrate REGISTRATE = Registrate.create("mymod");
 * 
 * public static final RegistryObject<MyBlock> MY_BLOCK = REGISTRATE.object("my_block")
 *         .block(MyBlock::new)
 *         .defaultItem()
 *         .register();
 * }
 * </pre>
 * 
 * For specifics as to building different registry entries, read the documentation on their respective builders.
 */
public abstract class AbstractRegistrate<S extends AbstractRegistrate<S>> {

    @javax.annotation.Generated("lombok")
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(AbstractRegistrate.class);

    private final class Registration<R, T extends R> {
        private final Identifier name;
        private final Class<? super R> type;
        private final NonNullLazyValue<? extends T> creator;
        private final RegistryEntry<T> delegate;
        private final List<NonNullConsumer<? super T>> callbacks = new ArrayList<>();

        Registration(Identifier name, Class<? super R> type, NonNullSupplier<? extends T> creator, NonNullFunction<RegistryObject<T>, ? extends RegistryEntry<T>> entryFactory) {
            this.name = name;
            this.type = type;
            this.creator = new NonNullLazyValue<>(creator);
            this.delegate = entryFactory.apply(RegistryObject.of(name, /*(Class<R>)*/ type/*, AbstractRegistrate.this.getModid()*/));
        }

        void register(Registry<R> registry) {
            T entry = creator.get();
            Registry.register(registry, name, entry);
            delegate.updateReference(registry);
            callbacks.forEach(c -> c.accept(entry));
            callbacks.clear();
        }

        void addRegisterCallback(NonNullConsumer<? super T> callback) {
            Preconditions.checkNotNull(callback, "Callback must not be null");
            callbacks.add(callback);
        }

        @javax.annotation.Generated("lombok")
        public Identifier getName() {
            return this.name;
        }

        @javax.annotation.Generated("lombok")
        public Class<? super R> getType() {
            return this.type;
        }

        @javax.annotation.Generated("lombok")
        public NonNullLazyValue<? extends T> getCreator() {
            return this.creator;
        }

        @javax.annotation.Generated("lombok")
        public RegistryEntry<T> getDelegate() {
            return this.delegate;
        }

        @Override
        @javax.annotation.Generated("lombok")
        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof AbstractRegistrate.Registration)) return false;
            final AbstractRegistrate<?>.Registration<?, ?> other = (AbstractRegistrate<?>.Registration<?, ?>) o;
            final Object this$name = this.getName();
            final Object other$name = other.getName();
            if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
            final Object this$type = this.getType();
            final Object other$type = other.getType();
            if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
            final Object this$creator = this.getCreator();
            final Object other$creator = other.getCreator();
            if (this$creator == null ? other$creator != null : !this$creator.equals(other$creator)) return false;
            final Object this$delegate = this.getDelegate();
            final Object other$delegate = other.getDelegate();
            if (this$delegate == null ? other$delegate != null : !this$delegate.equals(other$delegate)) return false;
            final Object this$callbacks = this.callbacks;
            final Object other$callbacks = other.callbacks;
            if (this$callbacks == null ? other$callbacks != null : !this$callbacks.equals(other$callbacks)) return false;
            return true;
        }

        @Override
        @javax.annotation.Generated("lombok")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $name = this.getName();
            result = result * PRIME + ($name == null ? 43 : $name.hashCode());
            final Object $type = this.getType();
            result = result * PRIME + ($type == null ? 43 : $type.hashCode());
            final Object $creator = this.getCreator();
            result = result * PRIME + ($creator == null ? 43 : $creator.hashCode());
            final Object $delegate = this.getDelegate();
            result = result * PRIME + ($delegate == null ? 43 : $delegate.hashCode());
            final Object $callbacks = this.callbacks;
            result = result * PRIME + ($callbacks == null ? 43 : $callbacks.hashCode());
            return result;
        }

        @Override
        @javax.annotation.Generated("lombok")
        public String toString() {
            return "AbstractRegistrate.Registration(name=" + this.getName() + ", type=" + this.getType() + ", creator=" + this.getCreator() + ", delegate=" + this.getDelegate() + ", callbacks=" + this.callbacks + ")";
        }
    }

    public static boolean isDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    private final Table<String, Class<?>, Registration<?, ?>> registrations = HashBasedTable.create();
    /**
     * Expected to be emptied by the time registration occurs, is emptied by {@link #accept(String, Class, Builder, NonNullSupplier, NonNullFunction)}
     */
    private final Multimap<Pair<String, Class<?>>, NonNullConsumer<?>> registerCallbacks = HashMultimap.create();
    /**
     * Entry-less callbacks that are invoked after the registry type has completely finished
     */
    private final Multimap<Class<?>, Runnable> afterRegisterCallbacks = HashMultimap.create();
    private final Set<Class<?>> completedRegistrations = new HashSet<>();
//    private final Table<Pair<String, Class<?>>, ProviderType<?>, Consumer<? extends RegistrateProvider>> datagensByEntry = HashBasedTable.create();
//    private final ListMultimap<ProviderType<?>, @NonnullType NonNullConsumer<? extends RegistrateProvider>> datagens = ArrayListMultimap.create();
//    private final NonNullLazyValue<Boolean> doDatagen = new NonNullLazyValue<>(DatagenModLoader::isRunningDataGen);
    /**
     */
    private final String modid;
    @Nullable
    private String currentName;
    @Nullable
    private NonNullLazyValue<? extends ItemGroup> currentGroup;
    private boolean skipErrors;

    /**
     * Construct a new Registrate for the given mod ID.
     * 
     * @param modid
     *            The mod ID for which objects will be registered
     */
    protected AbstractRegistrate(String modid) {
        this.modid = modid;
    }

    @SuppressWarnings("unchecked")
    protected S self() {
        return (S) this;
    }

//    protected S registerEventListeners(IEventBus bus) {
//        // Wildcard event listeners can only be done with reflective API right now
//        class EventProxy {
//            @SubscribeEvent
//            public void onRegister(RegistryEvent.Register<?> event) {
//                AbstractRegistrate.this.onRegister(event);
//            }
//
//            @SubscribeEvent(priority = EventPriority.LOWEST)
//            public void onRegisterLate(RegistryEvent.Register<?> event) {
//                AbstractRegistrate.this.onRegisterLate(event);
//            }
//        }
//        // Register events fire multiple times, so clean them up on common setup
//        final EventProxy proxy = new EventProxy();
//        final RegistryEvent.Register<Block> dummyEvent = new RegistryEvent.Register<>(new Identifier("blocks"), ForgeRegistries.BLOCKS);
//        try {
//            Consumer<RegistryEvent.Register<?>> onRegister = proxy::onRegister;
//            Consumer<RegistryEvent.Register<?>> onRegisterLate = proxy::onRegisterLate;
//            bus.addListener(onRegister);
//            bus.addListener(EventPriority.LOWEST, onRegisterLate);
//            OneTimeEventReceiver.addListener(bus, FMLCommonSetupEvent.class, $ -> {
//                OneTimeEventReceiver.unregister(bus, onRegister, dummyEvent);
//                OneTimeEventReceiver.unregister(bus, onRegisterLate, dummyEvent);
//            });
//        } catch (IllegalArgumentException e) {
//            log.info("Detected new forge version, registering events reflectively.");
//            bus.register(proxy);
//            OneTimeEventReceiver.addListener(bus, FMLCommonSetupEvent.class, $ -> OneTimeEventReceiver.unregister(bus, proxy, dummyEvent));
//        }
//        if (doDatagen.get()) {
//            OneTimeEventReceiver.addListener(bus, GatherDataEvent.class, this::onData);
//        }
//        return self();
//    }

    /**
     * Registers everything. This registrate should not be used after calling this method.
     */
    public void register() {
    	RegistryUtil.forAllRegistries(registry -> {
    		onRegister(registry);
    		onRegisterLate(registry);
    	});
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void onRegister(Registry<?> registry) {
        Class<?> type = RegistryUtil.getRegistrationClass(registry);
        Identifier registryId = RegistryUtil.getId(registry);
        if (type == null) {
            log.debug(DebugMarkers.REGISTER, "Skipping invalid registry with no supertype: " + registryId);
            return;
        }
        if (!registerCallbacks.isEmpty()) {
            registerCallbacks.asMap().forEach((k, v) -> log.warn("Found {} unused register callback(s) for entry {} [{}]. Was the entry ever registered?", v.size(), k.getLeft(), k.getRight().getSimpleName()));
            registerCallbacks.clear();
            if (isDevEnvironment()) {
                throw new IllegalStateException("Found unused register callbacks, see logs");
            }
        }
        Map<String, Registration<?, ?>> registrationsForType = registrations.column(type);
        if (registrationsForType.size() > 0) {
            log.debug(DebugMarkers.REGISTER, "Registering {} known objects of type {}", registrationsForType.size(), type.getName());
            for (Entry<String, Registration<?, ?>> e : registrationsForType.entrySet()) {
                try {
                    e.getValue().register((Registry) registry);
                    log.debug(DebugMarkers.REGISTER, "Registered {} to registry {}", e.getValue().getName(), registryId);
                } catch (Exception ex) {
                    String err = "Unexpected error while registering entry " + e.getValue().getName() + " to registry " + registryId;
                    if (skipErrors) {
                        log.error(DebugMarkers.REGISTER, err);
                    } else {
                        throw new RuntimeException(err, ex);
                    }
                }
            }
        }
    }

    protected void onRegisterLate(Registry<?> registry) {
        Class<?> type = RegistryUtil.getRegistrationClass(registry);
        Collection<Runnable> callbacks = afterRegisterCallbacks.get(type);
        callbacks.forEach(Runnable::run);
        callbacks.clear();
        completedRegistrations.add(type);
    }

//    @Nullable
//    private RegistrateDataProvider provider;

//    protected void onData(GatherDataEvent event) {
//        event.getGenerator().install(provider = new RegistrateDataProvider(this, modid, event));
//    }

    /**
     * Get the current name (from the last call to {@link #object(String)}), throwing an exception if it is not set.
     * 
     * @return The current entry name
     * @throws NullPointerException
     *             if {@link #currentName} is null
     */
    protected String currentName() {
        String name = currentName;
        Objects.requireNonNull(name, "Current name not set");
        return name;
    }

    /**
     * Allows retrieval of a previously created entry, of the current name (from the last invocation of {@link #object(String)}. Useful to retrieve a different entry than the final state of your
     * chain may produce, e.g.
     * 
     * <pre>
     * {@code
     * public static final RegistryObject<BlockItem> MY_BLOCK_ITEM = REGISTRATE.object("my_block")
     *         .block(MyBlock::new)
     *             .defaultItem()
     *             .lang("My Special Block")
     *             .build()
     *         .get(Item.class);
     * }
     * </pre>
     * 
     * @param <R>
     *            The type of the registry for which to retrieve the entry
     * @param <T>
     *            The type of the entry to return
     * @param type
     *            A class representing the registry type
     * @return A {@link RegistryEntry} which will supply the requested entry, if it exists
     * @throws IllegalArgumentException
     *             if no such registration has been done
     * @throws NullPointerException 
     *             if current name has not been set via {@link #object(String)}
     */
    public <R, T extends R> RegistryEntry<T> get(Class<? super R> type) {
        return this.<R, T>get(currentName(), type);
    }

    /**
     * Allows retrieval of a previously created entry. Useful to retrieve arbitrary entries that may have been created as side-effects of earlier registrations.
     * 
     * <pre>
     * {@code
     * public static final RegistryObject<MyBlock> MY_BLOCK = REGISTRATE.object("my_block")
     *         .block(MyBlock::new)
     *             .defaultItem()
     *             .register();
     * 
     * ...
     * 
     * public static final RegistryObject<BlockItem> MY_BLOCK_ITEM = REGISTRATE.get("my_block", Item.class);
     * }
     * </pre>
     * 
     * @param <R>
     *            The type of the registry for which to retrieve the entry
     * @param <T>
     *            The type of the entry to return
     * @param name
     *            The name of the registry entry to request
     * @param type
     *            A class representing the registry type
     * @return A {@link RegistryEntry} which will supply the requested entry, if it exists
     * @throws IllegalArgumentException
     *             if no such registration has been done
     */
    public <R, T extends R> RegistryEntry<T> get(String name, Class<? super R> type) {
        return this.<R, T>getRegistration(name, type).getDelegate();
    }

    @Beta
    public <R, T extends R> RegistryEntry<T> getOptional(String name, Class<? super R> type) {
        Registration<R, T> reg = this.<R, T>getRegistrationUnchecked(name, type);
        return reg == null ? RegistryEntry.empty() : reg.getDelegate();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <R, T extends R> Registration<R, T> getRegistrationUnchecked(String name, Class<? super R> type) {
        return (Registration<R, T>) registrations.get(name, type);
    }

    private <R, T extends R> Registration<R, T> getRegistration(String name, Class<? super R> type) {
        Registration<R, T> reg = this.<R, T>getRegistrationUnchecked(name, type);
        if (reg != null) {
            return reg;
        }
        throw new IllegalArgumentException("Unknown registration " + name + " for type " + type);
    }

    /**
     * Get all registered entries of a given registry type. Used internally for data generator scope, but can be used for post-processing of registered entries.
     * 
     * @param <R>
     *            The type of the registry for which to retrieve the entries
     * @param type
     *            A class representing the registry type
     * @return A {@link Collection} of {@link RegistryEntry RegistryEntries} representing all known registered entries of the given type.
     */
    @SuppressWarnings("unchecked")
    public <R> Collection<RegistryEntry<R>> getAll(Class<? super R> type) {
        return registrations.column(type).values().stream().map(r -> (RegistryEntry<R>) r.getDelegate()).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <R, T extends R> S addRegisterCallback(String name, Class<? super R> registryType, NonNullConsumer<? super T> callback) {
        Registration<R, T> reg = this.<R, T>getRegistrationUnchecked(name, registryType);
        if (reg == null) {
            registerCallbacks.put(Pair.of(name, registryType), (NonNullConsumer<?>) callback);
        } else {
            reg.addRegisterCallback(callback);
        }
        return self();
    }

    public <R> S addRegisterCallback(Class<? super R> registryType, Runnable callback) {
        afterRegisterCallbacks.put(registryType, callback);
        return self();
    }

    public <R> boolean isRegistered(Class<? super R> registryType) {
        return completedRegistrations.contains(registryType);
    }

    /**
     * Get the data provider instance for a given {@link ProviderType}. Only works within datagen context, not during registration or init.
     * 
     * @param <P>
     *            The type of the provider
     * @param type
     *            A {@link ProviderType} representing the desired provider
     * @return An {@link Optional} holding the provider, or empty if this provider was not registered. This can happen if datagen is run only for client or server providers.
     * @throws IllegalStateException
     *             if datagen has not started yet
     */
//    public <P extends RegistrateProvider> Optional<P> getDataProvider(ProviderType<P> type) {
//        RegistrateDataProvider provider = this.provider;
//        if (provider != null) {
//            return provider.getSubProvider(type);
//        }
//        throw new IllegalStateException("Cannot get data provider before datagen is started");
//    }

    /**
     * Mostly internal, sets the data generator for a certain entry/type combination. This will replace an existing data gen callback if it exists.
     * 
     * @param <P>
     *            The type of provider
     * @param <R>
     *            The registry type
     * @param builder
     *            The builder for the entry
     * @param type
     *            The {@link ProviderType} to generate data for
     * @param cons
     *            A callback to be invoked during data generation
     * @return this {@link AbstractRegistrate}
     */
//    public <P extends RegistrateProvider, R> S setDataGenerator(Builder<R, ?, ?, ?> builder, ProviderType<P> type, NonNullConsumer<? extends P> cons) {
//        return this.<P, R>setDataGenerator(builder.getName(), builder.getRegistryType(), type, cons);
//    }

    /**
     * Mostly internal, sets the data generator for a certain entry/type combination. This will replace an existing data gen callback if it exists.
     * 
     * @param <P>
     *            The type of provider
     * @param <R>
     *            The registry type
     * @param entry
     *            The name of the entry which the provider is for
     * @param registryType
     *            A {@link Class} representing the registry type of the entry
     * @param type
     *            The {@link ProviderType} to generate data for
     * @param cons
     *            A callback to be invoked during data generation
     * @return this {@link AbstractRegistrate}
     */
//    public <P extends RegistrateProvider, R> S setDataGenerator(String entry, Class<? super R> registryType, ProviderType<P> type, NonNullConsumer<? extends P> cons) {
//        if (!doDatagen.get()) return self();
//        Consumer<? extends RegistrateProvider> existing = datagensByEntry.put(Pair.of(entry, registryType), type, cons);
//        if (existing != null) {
//            datagens.remove(type, existing);
//        }
//        return addDataGenerator(type, cons);
//    }

    /**
     * Add a data generator callback that is not associated with any entry, which can never replace an existing data generator.
     * <p>
     * This is useful to add data generator callbacks for miscellaneous data not strictly associated with an entry.
     * 
     * @param <T>
     *            The type of provider
     * @param type
     *            The {@link ProviderType} to generate data for
     * @param cons
     *            A callback to be invoked during data generation
     * @return this {@link AbstractRegistrate}
     */
//    public <T extends RegistrateProvider> S addDataGenerator(ProviderType<? extends T> type, NonNullConsumer<? extends T> cons) {
//        if (doDatagen.get()) {
//            datagens.put(type, cons);
//        }
//        return self();
//    }

    private final NonNullLazyValue<List<Pair<String, String>>> extraLang = new NonNullLazyValue<>(() -> {
        final List<Pair<String, String>> ret = new ArrayList<>();
//        addDataGenerator(ProviderType.LANG, prov -> ret.forEach(p -> prov.add(p.getKey(), p.getValue())));
        return ret;
    });

    /**
     * Add a custom translation mapping, prepending this registrate's {@link #getModid() mod id} to the translation key.
     * 
     * @param key
     *            The translation key
     * @param value
     *            The (English) translation value
     * @return A {@link TranslatableText} representing the translated text
     */
    @Deprecated
    public TranslatableText addLang(String key, String value) {
        final String prefixedKey = getModid() + "." + key;
//        addDataGenerator(ProviderType.LANG, p -> p.add(prefixedKey, value));
        return new TranslatableText(prefixedKey);
    }

    /**
     * Add a custom translation mapping using the vanilla style of ResourceLocation -&gt; translation key conversion.
     * 
     * @param type
     *            Type of the object, this is used as a prefix (e.g. {@code ["block", "mymod:myblock"] -> "block.mymod.myblock"})
     * @param id
     *            ID of the object, which will be converted to a lang key via {@link Util#createTranslationKey(String, Identifier)}
     * @param localizedName
     *            (English) translation value
     * @return A {@link TranslatableText} representing the translated text
     */
    public TranslatableText addLang(String type, Identifier id, String localizedName) {
        return addRawLang(Util.createTranslationKey(type, id), localizedName);
    }

    /**
     * Add a custom translation mapping using the vanilla style of ResourceLocation -&gt; translation key conversion. Also appends a suffix to the key.
     * 
     * @param type
     *            Type of the object, this is used as a prefix (e.g. {@code ["block", "mymod:myblock"] -> "block.mymod.myblock"})
     * @param id
     *            ID of the object, which will be converted to a lang key via {@link Util#createTranslationKey(String, Identifier)}
     * @param suffix
     *            A suffix which will be appended to the generated key (separated by a dot)
     * @param localizedName
     *            (English) translation value
     * @return A {@link TranslatableText} representing the translated text
     */
    public TranslatableText addLang(String type, Identifier id, String suffix, String localizedName) {
        return addRawLang(Util.createTranslationKey(type, id) + "." + suffix, localizedName);
    }

    /**
     * Add a custom translation mapping directly to the lang provider.
     * 
     * @param key
     *            The translation key
     * @param value
     *            The (English) translation value
     * @return A {@link TranslatableText} representing the translated text
     */
    public TranslatableText addRawLang(String key, String value) {
//        if (doDatagen.get()) {
//            extraLang.get().add(Pair.of(key, value));
//        }
        return new TranslatableText(key);
    }

//    @SuppressWarnings("null")
//    private Optional<Pair<String, Class<?>>> getEntryForGenerator(ProviderType<?> type, NonNullConsumer<? extends RegistrateProvider> generator) {
//        for (Map.Entry<Pair<String, Class<?>>, Consumer<? extends RegistrateProvider>> e : datagensByEntry.column(type).entrySet()) {
//            if (e.getValue() == generator) {
//                return Optional.of(e.getKey());
//            }
//        }
//        return Optional.empty();
//    }

    /**
     * For internal use, calls upon registered data generators to actually create their data.
     * 
     * @param <T>
     *            The type of the provider
     * @param type
     *            The type of provider to run
     * @param gen
     *            The provider
     */
//    @SuppressWarnings("unchecked")
//    public <T extends RegistrateProvider> void genData(ProviderType<? extends T> type, T gen) {
//        if (!doDatagen.get()) return;
//        datagens.get(type).forEach(cons -> {
//            Optional<Pair<String, Class<?>>> entry = null;
//            if (log.isEnabled(Level.DEBUG, DebugMarkers.DATA)) {
//                entry = getEntryForGenerator(type, cons);
//                if (entry.isPresent()) {
//                    log.debug(DebugMarkers.DATA, "Generating data of type {} for entry {} [{}]", RegistrateDataProvider.getTypeName(type), entry.get().getLeft(), entry.get().getRight().getSimpleName());
//                } else {
//                    log.debug(DebugMarkers.DATA, "Generating unassociated data of type {} ({})", RegistrateDataProvider.getTypeName(type), type);
//                }
//            }
//            try {
//                ((Consumer<T>) cons).accept(gen);
//            } catch (Exception e) {
//                if (entry == null) {
//                    entry = getEntryForGenerator(type, cons);
//                }
//                Message err;
//                if (entry.isPresent()) {
//                    err = log.getMessageFactory().newMessage("Unexpected error while running data generator of type {} for entry {} [{}]", RegistrateDataProvider.getTypeName(type), entry.get().getLeft(), entry.get().getRight().getSimpleName());
//                } else {
//                    err = log.getMessageFactory().newMessage("Unexpected error while running unassociated data generator of type {} ({})", RegistrateDataProvider.getTypeName(type), type);
//                }
//                if (skipErrors) {
//                    log.error(err);
//                } else {
//                    throw new RuntimeException(err.getFormattedMessage(), e);
//                }
//            }
//        });
//    }

    /**
     * Enable skipping of registry entries and data generators that error during registration/generation.
     * <p>
     * <strong>Should only be used for debugging!</strong> {@code skipErrors(true)} will do nothing outside of a dev environment.
     * 
     * @param skipErrors
     *            {@code true} to skip errors during registration/generation
     * @return this {@link AbstractRegistrate}
     */
    public S skipErrors(boolean skipErrors) {
        if (skipErrors && !isDevEnvironment()) {
            log.error("Ignoring skipErrors(true) as this is not a development environment!");
        } else {
            this.skipErrors = skipErrors;
        }
        return self();
    }

    /**
     * Begin a new object, this is typically used at the beginning of a builder chain. The given name will be used until this method is called again. This makes it simple to create multiple entries
     * with the same name, as is often the case with blocks/items, items/entities, and blocks/TEs.
     * 
     * @param name
     *            The name to use for future entries
     * @return this {@link AbstractRegistrate}
     */
    public S object(String name) {
        this.currentName = name;
        return self();
    }

    /**
     * Set the default item group for all future items created with this Registrate, until the next time this method is called. The supplier will only be called once, and the value re-used for each
     * entry.
     * 
     * @param group
     *            The group to use for future items
     * @return this {@link AbstractRegistrate}
     */
    public S itemGroup(NonNullSupplier<? extends ItemGroup> group) {
        this.currentGroup = new NonNullLazyValue<>(group);
        return self();
    }

    /**
     * Set the default item group for all future items created with this Registrate, until the next time this method is called. The supplier will only be called once, and the value re-used for each
     * entry.
     * <p>
     * Additionally, add a translation for the item group.
     * 
     * @param group
     *            The group to use for future items
     * @param localizedName
     *            The english name to use for the item group title
     * @return this {@link AbstractRegistrate}
     */
    public S itemGroup(NonNullSupplier<? extends ItemGroup> group, String localizedName) {
//        this.addDataGenerator(ProviderType.LANG, prov -> prov.add(group.get(), localizedName));
        return itemGroup(group);
    }

    /**
     * Apply a transformation to this {@link AbstractRegistrate}. Useful to apply helper methods within a fluent chain, e.g.
     * 
     * <pre>
     * {@code
     * public static final RegistryObject<MyBlock> MY_BLOCK = REGISTRATE.object("my_block")
     *         .transform(Utils::createMyBlock)
     *         .get(Block.class);
     * }
     * </pre>
     * 
     * @param func
     *            The {@link UnaryOperator function} to apply
     * @return this {@link AbstractRegistrate}
     */
    public S transform(NonNullUnaryOperator<S> func) {
        return func.apply(self());
    }

    /**
     * Apply a transformation to this {@link AbstractRegistrate}. Similar to {@link #transform(NonNullUnaryOperator)}, but for actions that return a builder in-progress. Useful to apply helper methods
     * within a fluent chain, e.g.
     * 
     * <pre>
     * {@code
     * public static final RegistryObject<MyBlock> MY_BLOCK = REGISTRATE.object("my_block")
     *         .transform(Utils::createMyBlock)
     *         .lang("My Block") // Can modify the builder afterwards
     *         .register();
     * }
     * </pre>
     * 
     * @param <R>
     *            Registry type
     * @param <T>
     *            Entry type
     * @param <P>
     *            Parent type
     * @param <S2>
     *            Self type
     * @param func
     *            The {@link Function function} to apply
     * @return the resultant {@link Builder}
     */
    public <R, T extends R, P, S2 extends Builder<R, T, P, S2>> S2 transform(NonNullFunction<S, S2> func) {
        return func.apply(self());
    }

    /**
     * Create a builder for a new entry. This is typically not needed, unless you are implementing a <a href="https://github.com/tterrag1098/Registrate/wiki/Custom-Builders">custom builder type</a>.
     * <p>
     * Uses the currently set name (via {@link #object(String)}) as the name for the new entry, and passes it to the factory as the first parameter.
     * 
     * @param <R>
     *            Registry type
     * @param <T>
     *            Entry type
     * @param <P>
     *            Parent type
     * @param <S2>
     *            Self type
     * @param factory
     *            The factory to create the builder
     * @return The {@link Builder} instance
     */
    public <R, T extends R, P, S2 extends Builder<R, T, P, S2>> S2 entry(NonNullBiFunction<String, BuilderCallback, S2> factory) {
        return entry(currentName(), callback -> factory.apply(currentName(), callback));
    }

    /**
     * Create a builder for a new entry. This is typically not needed, unless you are implementing a <a href="https://github.com/tterrag1098/Registrate/wiki/Custom-Builders">custom builder type</a>.
     * 
     * @param <R>
     *            Registry type
     * @param <T>
     *            Entry type
     * @param <P>
     *            Parent type
     * @param <S2>
     *            Self type
     * @param name
     *            The name to use for the entry
     * @param factory
     *            The factory to create the builder
     * @return The {@link Builder} instance
     */
    public <R, T extends R, P, S2 extends Builder<R, T, P, S2>> S2 entry(String name, NonNullFunction<BuilderCallback, S2> factory) {
        return factory.apply(this::accept);
    }

    protected <R, T extends R> RegistryEntry<T> accept(String name, Class<? super R> type, Builder<R, T, ?, ?> builder, NonNullSupplier<? extends T> creator, NonNullFunction<RegistryObject<T>, ? extends RegistryEntry<T>> entryFactory) {
        Registration<R, T> reg = new Registration<>(new Identifier(modid, name), type, creator, entryFactory);
        log.debug(DebugMarkers.REGISTER, "Captured registration for entry {} of type {}", name, type.getName());
        registerCallbacks.removeAll(Pair.of(name, type)).forEach(callback -> {
            @SuppressWarnings({"unchecked", "null"})
            @NotNull
            NonNullConsumer<? super T> unsafeCallback = (NonNullConsumer<? super T>) callback;
            reg.addRegisterCallback(unsafeCallback);
        });
        registrations.put(name, type, reg);
        return reg.getDelegate();
    }

//    @Beta
//    @SuppressWarnings("unchecked")
//    public <R> Supplier<Registry<R>> makeRegistry(String name, Class<? super R> superType, Supplier<RegistryBuilder<R>> builder) {
//        OneTimeEventReceiver.addModListener(RegistryEvent.NewRegistry.class, $ -> builder.get().setName(new Identifier(getModid(), name)).setType((Class<R>) superType).create());
//        return Suppliers.memoize(() -> RegistryManager.ACTIVE.<R>getRegistry(superType));
//    }

    /* === Builder helpers === */
    // Generic
    public <R, T extends R> RegistryEntry<T> simple(Class<? super R> registryType, NonNullSupplier<T> factory) {
        return simple(currentName(), registryType, factory);
    }

    public <R, T extends R> RegistryEntry<T> simple(String name, Class<? super R> registryType, NonNullSupplier<T> factory) {
        return simple(this, name, registryType, factory);
    }

    public <R, T extends R, P> RegistryEntry<T> simple(P parent, Class<? super R> registryType, NonNullSupplier<T> factory) {
        return simple(parent, currentName(), registryType, factory);
    }

    public <R, T extends R, P> RegistryEntry<T> simple(P parent, String name, Class<? super R> registryType, NonNullSupplier<T> factory) {
        return entry(name, callback -> new NoConfigBuilder<R, T, P>(this, parent, name, callback, registryType, factory)).register();
    }

    // Items
    public <T extends Item> ItemBuilder<T, S> item(NonNullFunction<FabricItemSettings, T> factory) {
        return item(self(), factory);
    }

    public <T extends Item> ItemBuilder<T, S> item(String name, NonNullFunction<FabricItemSettings, T> factory) {
        return item(self(), name, factory);
    }

    public <T extends Item, P> ItemBuilder<T, P> item(P parent, NonNullFunction<FabricItemSettings, T> factory) {
        return item(parent, currentName(), factory);
    }

    public <T extends Item, P> ItemBuilder<T, P> item(P parent, String name, NonNullFunction<FabricItemSettings, T> factory) {
        return entry(name, callback -> ItemBuilder.create(this, parent, name, callback, factory, this.currentGroup));
    }

    // Blocks
    public <T extends Block> BlockBuilder<T, S> block(NonNullFunction<FabricBlockSettings, T> factory) {
        return block(self(), factory);
    }

    public <T extends Block> BlockBuilder<T, S> block(String name, NonNullFunction<FabricBlockSettings, T> factory) {
        return block(self(), name, factory);
    }

    public <T extends Block, P> BlockBuilder<T, P> block(P parent, NonNullFunction<FabricBlockSettings, T> factory) {
        return block(parent, currentName(), factory);
    }

    public <T extends Block, P> BlockBuilder<T, P> block(P parent, String name, NonNullFunction<FabricBlockSettings, T> factory) {
        return block(parent, name, Material.STONE, factory);
    }

    public <T extends Block> BlockBuilder<T, S> block(Material material, NonNullFunction<FabricBlockSettings, T> factory) {
        return block(self(), material, factory);
    }

    public <T extends Block> BlockBuilder<T, S> block(String name, Material material, NonNullFunction<FabricBlockSettings, T> factory) {
        return block(self(), name, material, factory);
    }

    public <T extends Block, P> BlockBuilder<T, P> block(P parent, Material material, NonNullFunction<FabricBlockSettings, T> factory) {
        return block(parent, currentName(), material, factory);
    }

    public <T extends Block, P> BlockBuilder<T, P> block(P parent, String name, Material material, NonNullFunction<FabricBlockSettings, T> factory) {
        return entry(name, callback -> BlockBuilder.create(this, parent, name, callback, factory, material));
    }

    // Entities
    // Regular
    public <T extends Entity> EntityBuilder<T, FabricEntityTypeBuilder<T>, S> entity(EntityType.EntityFactory<T> factory, SpawnGroup classification) {
        return entity(self(), factory, classification);
    }

    public <T extends Entity> EntityBuilder<T, FabricEntityTypeBuilder<T>, S> entity(String name, EntityType.EntityFactory<T> factory, SpawnGroup classification) {
        return entity(self(), name, factory, classification);
    }

    public <T extends Entity, P> EntityBuilder<T, FabricEntityTypeBuilder<T>, P> entity(P parent, EntityType.EntityFactory<T> factory, SpawnGroup classification) {
        return entity(parent, currentName(), factory, classification);
    }

    public <T extends Entity, P> EntityBuilder<T, FabricEntityTypeBuilder<T>, P> entity(P parent, String name, EntityType.EntityFactory<T> factory, SpawnGroup classification) {
        return entry(name, callback -> EntityBuilder.create(this, parent, name, callback, factory, classification));
    }

    // Living
    public <T extends LivingEntity> EntityBuilder<T, FabricEntityTypeBuilder.Living<T>, S> entityLiving(EntityType.EntityFactory<T> factory, SpawnGroup classification) {
        return entityLiving(self(), factory, classification);
    }

    public <T extends LivingEntity> EntityBuilder<T, FabricEntityTypeBuilder.Living<T>, S> entityLiving(String name, EntityType.EntityFactory<T> factory, SpawnGroup classification) {
        return entityLiving(self(), name, factory, classification);
    }

    public <T extends LivingEntity, P> EntityBuilder<T, FabricEntityTypeBuilder.Living<T>, P> entityLiving(P parent, EntityType.EntityFactory<T> factory, SpawnGroup classification) {
        return entityLiving(parent, currentName(), factory, classification);
    }

    public <T extends LivingEntity, P> EntityBuilder<T, FabricEntityTypeBuilder.Living<T>, P> entityLiving(P parent, String name, EntityType.EntityFactory<T> factory, SpawnGroup classification) {
        return entry(name, callback -> EntityBuilder.createLiving(this, parent, name, callback, factory, classification));
    }

    // Mob
    public <T extends MobEntity> EntityBuilder<T, FabricEntityTypeBuilder.Mob<T>, S> entityMob(EntityType.EntityFactory<T> factory, SpawnGroup classification) {
        return entityMob(self(), factory, classification);
    }

    public <T extends MobEntity> EntityBuilder<T, FabricEntityTypeBuilder.Mob<T>, S> entityMob(String name, EntityType.EntityFactory<T> factory, SpawnGroup classification) {
        return entityMob(self(), name, factory, classification);
    }

    public <T extends MobEntity, P> EntityBuilder<T, FabricEntityTypeBuilder.Mob<T>, P> entityMob(P parent, EntityType.EntityFactory<T> factory, SpawnGroup classification) {
        return entityMob(parent, currentName(), factory, classification);
    }

    public <T extends MobEntity, P> EntityBuilder<T, FabricEntityTypeBuilder.Mob<T>, P> entityMob(P parent, String name, EntityType.EntityFactory<T> factory, SpawnGroup classification) {
        return entry(name, callback -> EntityBuilder.createMob(this, parent, name, callback, factory, classification));
    }

    // Tile Entities
    @Deprecated
    public <T extends BlockEntity> TileEntityBuilder<T, S> tileEntity(NonNullSupplier<? extends T> factory) {
        return tileEntity(self(), factory);
    }

    @Deprecated
    public <T extends BlockEntity> TileEntityBuilder<T, S> tileEntity(String name, NonNullSupplier<? extends T> factory) {
        return tileEntity(self(), name, factory);
    }

    @Deprecated
    public <T extends BlockEntity, P> TileEntityBuilder<T, P> tileEntity(P parent, NonNullSupplier<? extends T> factory) {
        return tileEntity(parent, currentName(), factory);
    }

    @Deprecated
    public <T extends BlockEntity, P> TileEntityBuilder<T, P> tileEntity(P parent, String name, NonNullSupplier<? extends T> factory) {
        return tileEntity(parent, name, $ -> factory.get());
    }

    public <T extends BlockEntity> TileEntityBuilder<T, S> tileEntity(NonNullFunction<BlockEntityType<T>, ? extends T> factory) {
        return tileEntity(self(), factory);
    }

    public <T extends BlockEntity> TileEntityBuilder<T, S> tileEntity(String name, NonNullFunction<BlockEntityType<T>, ? extends T> factory) {
        return tileEntity(self(), name, factory);
    }

    public <T extends BlockEntity, P> TileEntityBuilder<T, P> tileEntity(P parent, NonNullFunction<BlockEntityType<T>, ? extends T> factory) {
        return tileEntity(parent, currentName(), factory);
    }

    public <T extends BlockEntity, P> TileEntityBuilder<T, P> tileEntity(P parent, String name, NonNullFunction<BlockEntityType<T>, ? extends T> factory) {
        return entry(name, callback -> TileEntityBuilder.create(this, parent, name, callback, factory));
    }

    // Fluids
    public FluidBuilder<SimpleFlowableFluid.Flowing, S> fluid() {
        return fluid(self());
    }

    public FluidBuilder<SimpleFlowableFluid.Flowing, S> fluid(Identifier stillTexture, Identifier flowingTexture) {
        return fluid(self(), stillTexture, flowingTexture);
    }

//    public FluidBuilder<SimpleFlowableFluid.Flowing, S> fluid(Identifier stillTexture, Identifier flowingTexture, NonNullBiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> attributesFactory) {
//        return fluid(self(), stillTexture, flowingTexture, attributesFactory);
//    }

    public <T extends SimpleFlowableFluid> FluidBuilder<T, S> fluid(Identifier stillTexture, Identifier flowingTexture, NonNullFunction<SimpleFlowableFluid.Properties, T> factory) {
        return fluid(self(), stillTexture, flowingTexture, factory);
    }

//    public <T extends SimpleFlowableFluid> FluidBuilder<T, S> fluid(Identifier stillTexture, Identifier flowingTexture, NonNullBiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> attributesFactory, NonNullFunction<SimpleFlowableFluid.Properties, T> factory) {
//        return fluid(self(), stillTexture, flowingTexture, attributesFactory, factory);
//    }

    public FluidBuilder<SimpleFlowableFluid.Flowing, S> fluid(String name) {
        return fluid(self(), name);
    }

    public FluidBuilder<SimpleFlowableFluid.Flowing, S> fluid(String name, Identifier stillTexture, Identifier flowingTexture) {
        return fluid(self(), name, stillTexture, flowingTexture);
    }

//    public FluidBuilder<SimpleFlowableFluid.Flowing, S> fluid(String name, Identifier stillTexture, Identifier flowingTexture, NonNullBiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> attributesFactory) {
//        return fluid(self(), name, stillTexture, flowingTexture, attributesFactory);
//    }

    public <T extends SimpleFlowableFluid> FluidBuilder<T, S> fluid(String name, Identifier stillTexture, Identifier flowingTexture, NonNullFunction<SimpleFlowableFluid.Properties, T> factory) {
        return fluid(self(), name, stillTexture, flowingTexture, factory);
    }

//    public <T extends SimpleFlowableFluid> FluidBuilder<T, S> fluid(String name, Identifier stillTexture, Identifier flowingTexture, NonNullBiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> attributesFactory, NonNullFunction<SimpleFlowableFluid.Properties, T> factory) {
//        return fluid(self(), name, stillTexture, flowingTexture, attributesFactory, factory);
//    }

    public <P> FluidBuilder<SimpleFlowableFluid.Flowing, P> fluid(P parent) {
        return fluid(parent, currentName());
    }

    public <P> FluidBuilder<SimpleFlowableFluid.Flowing, P> fluid(P parent, Identifier stillTexture, Identifier flowingTexture) {
        return fluid(parent, currentName(), stillTexture, flowingTexture);
    }

//    public <P> FluidBuilder<SimpleFlowableFluid.Flowing, P> fluid(P parent, Identifier stillTexture, Identifier flowingTexture, NonNullBiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> attributesFactory) {
//        return fluid(parent, currentName(), stillTexture, flowingTexture, attributesFactory);
//    }

    public <T extends SimpleFlowableFluid, P> FluidBuilder<T, P> fluid(P parent, Identifier stillTexture, Identifier flowingTexture, NonNullFunction<SimpleFlowableFluid.Properties, T> factory) {
        return fluid(parent, currentName(), stillTexture, flowingTexture, factory);
    }

//    public <T extends SimpleFlowableFluid, P> FluidBuilder<T, P> fluid(P parent, Identifier stillTexture, Identifier flowingTexture, NonNullBiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> attributesFactory, NonNullFunction<SimpleFlowableFluid.Properties, T> factory) {
//        return fluid(parent, currentName(), stillTexture, flowingTexture, attributesFactory, factory);
//    }

    public <P> FluidBuilder<SimpleFlowableFluid.Flowing, P> fluid(P parent, String name) {
        return fluid(parent, name, new Identifier(getModid(), "block/" + currentName() + "_still"), new Identifier(getModid(), "block/" + currentName() + "_flow"));
    }

    public <P> FluidBuilder<SimpleFlowableFluid.Flowing, P> fluid(P parent, String name, Identifier stillTexture, Identifier flowingTexture) {
        return entry(name, callback -> FluidBuilder.create(this, parent, name, callback, stillTexture, flowingTexture));
    }

//    public <P> FluidBuilder<SimpleFlowableFluid.Flowing, P> fluid(P parent, String name, Identifier stillTexture, Identifier flowingTexture, NonNullBiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> attributesFactory) {
//        return entry(name, callback -> FluidBuilder.create(this, parent, name, callback, stillTexture, flowingTexture, attributesFactory));
//    }

    public <T extends SimpleFlowableFluid, P> FluidBuilder<T, P> fluid(P parent, String name, Identifier stillTexture, Identifier flowingTexture, NonNullFunction<SimpleFlowableFluid.Properties, T> factory) {
        return entry(name, callback -> FluidBuilder.create(this, parent, name, callback, stillTexture, flowingTexture, factory));
    }

//    public <T extends SimpleFlowableFluid, P> FluidBuilder<T, P> fluid(P parent, String name, Identifier stillTexture, Identifier flowingTexture, NonNullBiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> attributesFactory, NonNullFunction<SimpleFlowableFluid.Properties, T> factory) {
//        return entry(name, callback -> FluidBuilder.create(this, parent, name, callback, stillTexture, flowingTexture, attributesFactory, factory));
//    }

    // Container
    public <T extends ScreenHandler, SC extends Screen & ScreenHandlerProvider<T>> ContainerBuilder<T, SC, S> container(ContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, SC>> screenFactory) {
        return container(currentName(), factory, screenFactory);
    }

    public <T extends ScreenHandler, SC extends Screen & ScreenHandlerProvider<T>> ContainerBuilder<T, SC, S> container(String name, ContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, SC>> screenFactory) {
        return container(self(), name, factory, screenFactory);
    }

    public <T extends ScreenHandler, SC extends Screen & ScreenHandlerProvider<T>, P> ContainerBuilder<T, SC, P> container(P parent, ContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, SC>> screenFactory) {
        return container(parent, currentName(), factory, screenFactory);
    }

    public <T extends ScreenHandler, SC extends Screen & ScreenHandlerProvider<T>, P> ContainerBuilder<T, SC, P> container(P parent, String name, ContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, SC>> screenFactory) {
        return entry(name, callback -> new ContainerBuilder<T, SC, P>(this, parent, name, callback, factory, screenFactory));
    }

    public <T extends ScreenHandler, SC extends Screen & ScreenHandlerProvider<T>> ContainerBuilder<T, SC, S> container(ForgeContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, SC>> screenFactory) {
        return container(currentName(), factory, screenFactory);
    }

    public <T extends ScreenHandler, SC extends Screen & ScreenHandlerProvider<T>> ContainerBuilder<T, SC, S> container(String name, ForgeContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, SC>> screenFactory) {
        return container(self(), name, factory, screenFactory);
    }

    public <T extends ScreenHandler, SC extends Screen & ScreenHandlerProvider<T>, P> ContainerBuilder<T, SC, P> container(P parent, ForgeContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, SC>> screenFactory) {
        return container(parent, currentName(), factory, screenFactory);
    }

    public <T extends ScreenHandler, SC extends Screen & ScreenHandlerProvider<T>, P> ContainerBuilder<T, SC, P> container(P parent, String name, ForgeContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, SC>> screenFactory) {
        return entry(name, callback -> new ContainerBuilder<T, SC, P>(this, parent, name, callback, factory, screenFactory));
    }

    // Enchantment
    public <T extends Enchantment> EnchantmentBuilder<T, S> enchantment(EnchantmentTarget type, EnchantmentFactory<T> factory) {
        return enchantment(self(), type, factory);
    }

    public <T extends Enchantment> EnchantmentBuilder<T, S> enchantment(String name, EnchantmentTarget type, EnchantmentFactory<T> factory) {
        return enchantment(self(), name, type, factory);
    }

    public <T extends Enchantment, P> EnchantmentBuilder<T, P> enchantment(P parent, EnchantmentTarget type, EnchantmentFactory<T> factory) {
        return enchantment(parent, currentName(), type, factory);
    }

    public <T extends Enchantment, P> EnchantmentBuilder<T, P> enchantment(P parent, String name, EnchantmentTarget type, EnchantmentFactory<T> factory) {
        return entry(name, callback -> EnchantmentBuilder.create(this, parent, name, callback, type, factory));
    }

    /**
     * @return The mod ID that this {@link AbstractRegistrate} is creating objects for
     */
    @javax.annotation.Generated("lombok")
    public String getModid() {
        return this.modid;
    }
}
