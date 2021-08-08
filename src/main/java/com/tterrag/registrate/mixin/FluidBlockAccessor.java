package com.tterrag.registrate.mixin;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LiquidBlock.class)
public interface FluidBlockAccessor {
	@Invoker("<init>")
	static LiquidBlock callInit(FlowingFluid fluid, BlockBehaviour.Properties settings) {
		throw new AssertionError();
	}
}
