package com.tterrag.registrate.util.entry;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.fabric.RegistryObject;

public class FluidEntry<T extends FlowableFluid> extends RegistryEntry<T> {

    private final @Nullable BlockEntry<? extends Block> block;

    public FluidEntry(AbstractRegistrate<?> owner, RegistryObject<T> delegate) {
        super(owner, delegate);
        BlockEntry<? extends Block> block = null;
        try {
            block = BlockEntry.cast(getSibling(Block.class));
        } catch (IllegalArgumentException e) {} // TODO add way to get entry optionally
        this.block = block;
    }

    @Override
    public <R> boolean is(R entry) {
        return get().matchesType((Fluid) entry);
    }

    @SuppressWarnings({ "unchecked", "null" })
    <S extends FlowableFluid> S getSource() {
        return (S) get().getStill();
    }

    @SuppressWarnings({ "unchecked", "null" })
    <B extends Block> Optional<B> getBlock() {
        return (Optional<B>) Optional.ofNullable(block).map(RegistryEntry::get);
    }

    @SuppressWarnings({ "unchecked", "null" })
    <I extends Item> Optional<I> getBucket() {
        return Optional.ofNullable((I) get().getBucketItem());
    }
}
