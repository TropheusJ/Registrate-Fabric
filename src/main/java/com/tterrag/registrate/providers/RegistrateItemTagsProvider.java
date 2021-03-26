//package com.tterrag.registrate.providers;
//
//import java.util.function.Function;
//
//import com.tterrag.registrate.AbstractRegistrate;
//
//import net.minecraft.block.Block;
//import net.minecraft.data.DataGenerator;
//import net.minecraft.item.Item;
//import net.minecraft.tag.Tag;
//import net.minecraft.util.registry.Registry;
//import net.minecraftforge.common.data.ExistingFileHelper;
//
//public class RegistrateItemTagsProvider extends RegistrateTagsProvider<Item> {
//
//    private final Function<Tag.Identified<Block>, Tag.Builder> builderLookup;
//
//    @SuppressWarnings({ "deprecation", "null" })
//    public RegistrateItemTagsProvider(AbstractRegistrate<?> owner, ProviderType<RegistrateItemTagsProvider> type, String name, DataGenerator generatorIn, ExistingFileHelper existingFileHelper, RegistrateTagsProvider<Block> blockTags) {
//        super(owner, type, name, generatorIn, Registry.ITEM, existingFileHelper);
//        this.builderLookup = blockTags::method_27169;
//    }
//
//    public void copy(Tag.Identified<Block> p_240521_1_, Tag.Identified<Item> p_240521_2_) {
//        Tag.Builder itag$builder = this.method_27169(p_240521_2_);
//        Tag.Builder itag$builder1 = this.builderLookup.apply(p_240521_1_);
//        itag$builder1.streamEntries().forEach(itag$builder::add);
//    }
//}
