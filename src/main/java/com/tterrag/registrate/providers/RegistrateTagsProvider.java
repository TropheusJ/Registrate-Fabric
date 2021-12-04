package com.tterrag.registrate.providers;

import java.nio.file.Path;

import com.tterrag.registrate.AbstractRegistrate;

import net.fabricmc.api.EnvType;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Named;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public class RegistrateTagsProvider<T> extends FabricTagProvider<T> implements RegistrateProvider {

    private final AbstractRegistrate<?> owner;
    private final ProviderType<? extends RegistrateTagsProvider<T>> type;
    private final String name;

    public RegistrateTagsProvider(AbstractRegistrate<?> owner, ProviderType<? extends RegistrateTagsProvider<T>> type, String name, FabricDataGenerator generatorIn, Registry<T> registryIn) {
        super(generatorIn, registryIn, "", owner.getModid());
        this.owner = owner;
        this.type = type;
        this.name = name;
    }

    protected Path getPath(ResourceLocation id) {
        return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/" + name + "/" + id.getPath() + ".json");
    }

    public String getName() {
        return "Tags (" + name + ")";
    }

    @Override
    protected void generateTags() {
        owner.genData(type, this);
    }

    @Override
    public EnvType getSide() {
        return EnvType.SERVER;
    }

    @Override
    public FabricTagBuilder<T> getOrCreateTagBuilder(Named<T> tag) { return super.getOrCreateTagBuilder(tag); }

    @Override
    public Tag.Builder getOrCreateRawBuilder(Named<T> tag) { return super.getOrCreateRawBuilder(tag); }
}
