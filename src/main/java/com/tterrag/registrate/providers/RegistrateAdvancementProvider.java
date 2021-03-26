//package com.tterrag.registrate.providers;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.HashSet;
//import java.util.Objects;
//import java.util.Set;
//import java.util.function.Consumer;
//
//import org.jetbrains.annotations.Nullable;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.tterrag.registrate.AbstractRegistrate;
//
//import net.fabricmc.api.EnvType;
//import net.minecraft.advancement.Advancement;
//import net.minecraft.data.DataCache;
//import net.minecraft.data.DataGenerator;
//import net.minecraft.data.DataProvider;
//import net.minecraft.text.TranslatableText;
//import net.minecraft.util.Identifier;
//
//public class RegistrateAdvancementProvider implements RegistrateProvider, Consumer<Advancement> {
//    @javax.annotation.Generated("lombok")
//    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(RegistrateAdvancementProvider.class);
//    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
//    private final AbstractRegistrate<?> owner;
//    private final DataGenerator generator;
//
//    public RegistrateAdvancementProvider(AbstractRegistrate<?> owner, DataGenerator generatorIn) {
//        this.owner = owner;
//        this.generator = generatorIn;
//    }
//
//    @Override
//    public EnvType getSide() {
//        return EnvType.SERVER;
//    }
//
//    public TranslatableText title(String category, String name, String title) {
//        return owner.addLang("advancements", new Identifier(category, name), "title", title);
//    }
//
//    public TranslatableText desc(String category, String name, String desc) {
//        return owner.addLang("advancements", new Identifier(category, name), "description", desc);
//    }
//
//    @Nullable
//    private DataCache cache;
//    private Set<Identifier> seenAdvancements = new HashSet<>();
//
//    @Override
//    public void run(DataCache cache) throws IOException {
//        try {
//            this.cache = cache;
//            this.seenAdvancements.clear();
//            owner.genData(ProviderType.ADVANCEMENT, this);
//        } finally {
//            this.cache = null;
//        }
//    }
//
//    @Override
//    public void accept(@Nullable Advancement t) {
//        DataCache cache = this.cache;
//        if (cache == null) {
//            throw new IllegalStateException("Cannot accept advancements outside of act");
//        }
//        Objects.requireNonNull(t, "Cannot accept a null advancement");
//        Path path = this.generator.getOutput();
//        if (!seenAdvancements.add(t.getId())) {
//            throw new IllegalStateException("Duplicate advancement " + t.getId());
//        } else {
//            Path path1 = getPath(path, t);
//            try {
//                DataProvider.writeToPath(GSON, cache, t.createTask().toJson(), path1);
//            } catch (IOException ioexception) {
//                log.error("Couldn\'t save advancement {}", path1, ioexception);
//            }
//        }
//    }
//
//    private static Path getPath(Path pathIn, Advancement advancementIn) {
//        return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
//    }
//
//    public String getName() {
//        return "Advancements";
//    }
//}
