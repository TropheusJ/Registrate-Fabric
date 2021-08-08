package com.tterrag.registrate.fabric;

import com.tterrag.registrate.mixin.FluidBlockAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public class FluidBlockHelper {
	public static LiquidBlock createFluidBlock(FlowingFluid fluid, BlockBehaviour.Properties settings) {
		return FluidBlockAccessor.callInit(fluid, settings);
	}
}
