//package com.tterrag.registrate.providers;
//
//import java.nio.file.Path;
//
//import com.tterrag.registrate.AbstractRegistrate;
//
//import net.fabricmc.api.EnvType;
//import net.minecraft.data.DataGenerator;
//import net.minecraft.data.server.AbstractTagProvider;
//import net.minecraft.tag.Tag;
//import net.minecraft.tag.Tag.Identified;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.registry.Registry;
//import net.minecraftforge.common.data.ExistingFileHelper;
//
//public class RegistrateTagsProvider<T> extends AbstractTagProvider<T> implements RegistrateProvider {
//
//    private final AbstractRegistrate<?> owner;
//    private final ProviderType<? extends RegistrateTagsProvider<T>> type;
//    private final String name;
//
//    public RegistrateTagsProvider(AbstractRegistrate<?> owner, ProviderType<? extends RegistrateTagsProvider<T>> type, String name, DataGenerator generatorIn, Registry<T> registryIn, ExistingFileHelper existingFileHelper) {
//        super(generatorIn, registryIn, owner.getModid(), existingFileHelper);
//        this.owner = owner;
//        this.type = type;
//        this.name = name;
//    }
//
//    protected Path getOutput(Identifier id) {
//        return this.root.getOutput().resolve("data/" + id.getNamespace() + "/tags/" + name + "/" + id.getPath() + ".json");
//    }
//
//    public String getName() {
//        return "Tags (" + name + ")";
//    }
//
//    @Override
//    protected void configure() {
//        owner.genData(type, this);
//    }
//
//    @Override
//    public EnvType getSide() {
//        return EnvType.SERVER;
//    }
//
//    @Override
//    public ObjectBuilder<T> getOrCreateTagBuilder(Identified<T> tag) { return super.getOrCreateTagBuilder(tag); }
//
//    @Override
//    public Tag.Builder method_27169(Identified<T> tag) { return super.method_27169(tag); }
//}
