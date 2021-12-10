package com.tterrag.registrate.providers;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockStateDefinitionProvider;

public class RegistrateItemModelProvider extends FabricBlockStateDefinitionProvider implements RegistrateProvider {

    private final AbstractRegistrate<?> parent;

    public RegistrateItemModelProvider(AbstractRegistrate<?> parent, FabricDataGenerator generator) {
        super(generator);
        this.parent = parent;
    }

    @Override
    public EnvType getSide() {
        return EnvType.CLIENT;
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        parent.genData(ProviderType.ITEM_MODEL, this);
    }

    @Override
    public String getName() {
        return "Item models";
    }

    public String modid(NonNullSupplier<? extends ItemLike> item) {
        return Registry.ITEM.getKey(item.get().asItem()).getNamespace();
    }

    public String name(NonNullSupplier<? extends ItemLike> item) {
        return Registry.ITEM.getKey(item.get().asItem()).getPath();
    }

    public ResourceLocation itemTexture(NonNullSupplier<? extends ItemLike> item) {
        return new ResourceLocation("item/" + name(item));
    }
//
//    public ItemModelBuilder blockItem(NonNullSupplier<? extends ItemLike> block) {
//        return blockItem(block, "");
//    }
//
//    public ItemModelBuilder blockItem(NonNullSupplier<? extends ItemLike> block, String suffix) {
//        return withExistingParent(name(block), new ResourceLocation(modid(block), "block/" + name(block) + suffix));
//    }
//
//    public ItemModelBuilder blockWithInventoryModel(NonNullSupplier<? extends ItemLike> block) {
//        return withExistingParent(name(block), new ResourceLocation(modid(block), "block/" + name(block) + "_inventory"));
//    }
//
//    public ItemModelBuilder blockSprite(NonNullSupplier<? extends ItemLike> block) {
//        return blockSprite(block, modLoc("block/" + name(block)));
//    }
//
//    public ItemModelBuilder blockSprite(NonNullSupplier<? extends ItemLike> block, ResourceLocation texture) {
//        return generated(() -> block.get().asItem(), texture);
//    }
//
    public RegistrateItemModelProvider generated(NonNullSupplier<? extends ItemLike> item) {
        return generated(item, itemTexture(item));
    }

    public RegistrateItemModelProvider generated(NonNullSupplier<? extends ItemLike> item, ResourceLocation... layers) {
//        ItemModelBuilder ret = getBuilder(name(item)).parent(new UncheckedModelFile("item/generated"));
//        for (int i = 0; i < layers.length; i++) {
//            ret = ret.texture("layer" + i, layers[i]);
//        }
//        return ret;
        return this;
    }
//
//    public ItemModelBuilder handheld(NonNullSupplier<? extends ItemLike> item) {
//        return handheld(item, itemTexture(item));
//    }
//
//    public ItemModelBuilder handheld(NonNullSupplier<? extends ItemLike> item, ResourceLocation texture) {
//        return withExistingParent(name(item), "item/handheld").texture("layer0", texture);
//    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {}


}