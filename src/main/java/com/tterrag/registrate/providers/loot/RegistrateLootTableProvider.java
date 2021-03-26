//package com.tterrag.registrate.providers.loot;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.function.BiConsumer;
//import java.util.function.Consumer;
//import java.util.function.Supplier;
//
//import com.google.common.collect.BiMap;
//import com.google.common.collect.HashMultimap;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.Multimap;
//import com.mojang.datafixers.util.Pair;
//import com.tterrag.registrate.AbstractRegistrate;
//import com.tterrag.registrate.providers.ProviderType;
//import com.tterrag.registrate.providers.RegistrateProvider;
//import com.tterrag.registrate.util.nullness.NonNullBiFunction;
//import com.tterrag.registrate.util.nullness.NonNullConsumer;
//
//import net.fabricmc.api.EnvType;
//import net.minecraft.data.DataGenerator;
//import net.minecraft.data.server.LootTablesProvider;
//import net.minecraft.loot.LootTable;
//import net.minecraft.loot.LootTableReporter;
//import net.minecraft.loot.context.LootContextType;
//import net.minecraft.loot.context.LootContextTypes;
//import net.minecraft.util.Identifier;
//import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
//
//public class RegistrateLootTableProvider extends LootTablesProvider implements RegistrateProvider {
//    
//    public interface LootType<T extends RegistrateLootTables> {
//        
//        static LootType<RegistrateBlockLootTables> BLOCK = register("block", LootContextTypes.BLOCK, RegistrateBlockLootTables::new);
//        static LootType<RegistrateEntityLootTables> ENTITY = register("entity", LootContextTypes.ENTITY, RegistrateEntityLootTables::new);
//
//        T getLootCreator(AbstractRegistrate<?> parent, Consumer<T> callback);
//        
//        LootContextType getLootSet();
//        
//        static <T extends RegistrateLootTables> LootType<T> register(String name, LootContextType set, NonNullBiFunction<AbstractRegistrate, Consumer<T>, T> factory) {
//            LootType<T> type = new LootType<T>() {
//                @Override
//                public T getLootCreator(AbstractRegistrate<?> parent, Consumer<T> callback) {
//                    return factory.apply(parent, callback);
//                }
//                
//                @Override
//                public LootContextType getLootSet() {
//                    return set;
//                }
//            };
//            LOOT_TYPES.put(name, type);
//            return type;
//        }
//    }
//    
//    private static final Map<String, LootType<?>> LOOT_TYPES = new HashMap<>();
//    
//    private final AbstractRegistrate<?> parent;
//    
//    private final Multimap<LootType<?>, Consumer<? super RegistrateLootTables>> specialLootActions = HashMultimap.create();
//    private final Multimap<LootContextType, Consumer<BiConsumer<Identifier, LootTable.Builder>>> lootActions = HashMultimap.create();
//    private final Set<RegistrateLootTables> currentLootCreators = new HashSet<>();
//
//    public RegistrateLootTableProvider(AbstractRegistrate<?> parent, DataGenerator dataGeneratorIn) {
//        super(dataGeneratorIn);
//        this.parent = parent;
//    }
//
//    @Override
//    public String getName() {
//        return "Loot tables";
//    }
//    
//    @Override
//    public EnvType getSide() {
//        return EnvType.SERVER;
//    }
//    
//    @Override
//    protected void validate(Map<Identifier, LootTable> map, LootTableReporter validationresults) {
//        currentLootCreators.forEach(c -> c.validate(map, validationresults));
//    }
//    
//    @SuppressWarnings("unchecked")
//    public <T extends RegistrateLootTables> void addLootAction(LootType<T> type, NonNullConsumer<T> action) {
//        this.specialLootActions.put(type, (Consumer<? super RegistrateLootTables>) action);
//    }
//    
//    public void addLootAction(LootContextType set, Consumer<BiConsumer<Identifier, LootTable.Builder>> action) {
//        this.lootActions.put(set, action);
//    }
//    
//    private Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>> getLootCreator(AbstractRegistrate<?> parent, LootType<?> type) {
//        return () -> {
//            RegistrateLootTables creator = type.getLootCreator(parent, cons -> specialLootActions.get(type).forEach(c -> c.accept(cons)));
//            currentLootCreators.add(creator);
//            return creator;
//        };
//    }
//    
//    private static final BiMap<Identifier, LootContextType> SET_REGISTRY = ObfuscationReflectionHelper.getPrivateValue(LootContextTypes.class, null, "field_216268_i");
//    
//    @Override
//    protected List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, LootContextType>> getTables() {
//        parent.genData(ProviderType.LOOT, this);
//        currentLootCreators.clear();
//        ImmutableList.Builder<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, LootContextType>> builder = ImmutableList.builder();
//        for (LootType<?> type : LOOT_TYPES.values()) {
//            builder.add(Pair.of(getLootCreator(parent, type), type.getLootSet()));
//        }
//        for (LootContextType set : SET_REGISTRY.values()) {
//            builder.add(Pair.of(() -> callback -> lootActions.get(set).forEach(a -> a.accept(callback)), set));
//        }
//        return builder.build();
//    }
//}
