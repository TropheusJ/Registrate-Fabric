//package com.tterrag.registrate.providers;
//
//import com.tterrag.registrate.AbstractRegistrate;
//import com.tterrag.registrate.util.nullness.NonNullSupplier;
//
//import net.fabricmc.api.EnvType;
//import net.minecraft.data.DataGenerator;
//import net.minecraft.item.ItemConvertible;
//import net.minecraft.util.Identifier;
//import net.minecraftforge.client.model.generators.ItemModelBuilder;
//import net.minecraftforge.client.model.generators.ItemModelProvider;
//import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
//import net.minecraftforge.common.data.ExistingFileHelper;
//
//public class RegistrateItemModelProvider extends ItemModelProvider implements RegistrateProvider {
//    
//    private final AbstractRegistrate<?> parent;
//
//    public RegistrateItemModelProvider(AbstractRegistrate<?> parent, DataGenerator generator, ExistingFileHelper existingFileHelper) {
//        super(generator, parent.getModid(), existingFileHelper);
//        this.parent = parent;
//    }
//
//    @Override
//    public EnvType getSide() {
//        return EnvType.CLIENT;
//    }
//    
//    @Override
//    protected void registerModels() {
//        parent.genData(ProviderType.ITEM_MODEL, this);
//    }
//    
//    @Override
//    public String getName() {
//        return "Item models";
//    }
//    
//    public String modid(NonNullSupplier<? extends ItemConvertible> item) {
//        return item.get().asItem().getRegistryName().getNamespace();
//    }
//    
//    public String name(NonNullSupplier<? extends ItemConvertible> item) {
//        return item.get().asItem().getRegistryName().getPath();
//    }
//    
//    public Identifier itemTexture(NonNullSupplier<? extends ItemConvertible> item) {
//        return modLoc("item/" + name(item));
//    }
//    
//    public ItemModelBuilder blockItem(NonNullSupplier<? extends ItemConvertible> block) {
//        return blockItem(block, "");
//    }
//    
//    public ItemModelBuilder blockItem(NonNullSupplier<? extends ItemConvertible> block, String suffix) {
//        return withExistingParent(name(block), new Identifier(modid(block), "block/" + name(block) + suffix));
//    }
//
//    public ItemModelBuilder blockWithInventoryModel(NonNullSupplier<? extends ItemConvertible> block) {
//        return withExistingParent(name(block), new Identifier(modid(block), "block/" + name(block) + "_inventory"));
//    }
//    
//    public ItemModelBuilder blockSprite(NonNullSupplier<? extends ItemConvertible> block) {
//        return blockSprite(block, modLoc("block/" + name(block)));
//    }
//    
//    public ItemModelBuilder blockSprite(NonNullSupplier<? extends ItemConvertible> block, Identifier texture) {
//        return generated(() -> block.get().asItem(), texture);
//    }
//    
//    public ItemModelBuilder generated(NonNullSupplier<? extends ItemConvertible> item) {
//        return generated(item, itemTexture(item));
//    }
//
//    public ItemModelBuilder generated(NonNullSupplier<? extends ItemConvertible> item, Identifier... layers) {
//        ItemModelBuilder ret = getBuilder(name(item)).parent(new UncheckedModelFile("item/generated"));
//        for (int i = 0; i < layers.length; i++) {
//            ret = ret.texture("layer" + i, layers[i]);
//        }
//        return ret;
//    }
//    
//    public ItemModelBuilder handheld(NonNullSupplier<? extends ItemConvertible> item) {
//        return handheld(item, itemTexture(item));
//    }
//    
//    public ItemModelBuilder handheld(NonNullSupplier<? extends ItemConvertible> item, Identifier texture) {
//        return withExistingParent(name(item), "item/handheld").texture("layer0", texture);
//    }
//}
