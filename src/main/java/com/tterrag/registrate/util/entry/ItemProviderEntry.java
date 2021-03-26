package com.tterrag.registrate.util.entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.fabric.RegistryObject;

public class ItemProviderEntry<T extends ItemConvertible> extends RegistryEntry<T> {

    public ItemProviderEntry(AbstractRegistrate<?> owner, RegistryObject<T> delegate) {
        super(owner, delegate);
    }

    public ItemStack asStack() {
        return new ItemStack(get());
    }

    public ItemStack asStack(int count) {
        return new ItemStack(get(), count);
    }

    public boolean isIn(ItemStack stack) {
        return is(stack.getItem());
    }

    public boolean is(Item item) {
        return get().asItem() == item;
    }
}
