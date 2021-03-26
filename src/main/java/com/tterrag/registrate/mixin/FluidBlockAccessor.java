package com.tterrag.registrate.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;

@Mixin(FluidBlock.class)
public interface FluidBlockAccessor {
	@Invoker("<init>")
	static FluidBlock callInit(FlowableFluid fluid, AbstractBlock.Settings settings) {
		throw new AssertionError();
	}
}
