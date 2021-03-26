package com.tterrag.registrate.fabric;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;

import com.tterrag.registrate.mixin.FluidBlockAccessor;

public class FluidBlockHelper {
	public static FluidBlock createFluidBlock(FlowableFluid fluid, AbstractBlock.Settings settings) {
		return FluidBlockAccessor.callInit(fluid, settings);
	}
}
