package com.tterrag.registrate.fabric;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class SimpleFluidRenderHandler implements FluidRenderHandler {
	private final Sprite[] sprites = new Sprite[2];
	private final int color;

	public SimpleFluidRenderHandler(int color) {
		this.color = color;
	}

	public SimpleFluidRenderHandler() {
		this(-1);
	}

	public void registerListeners(Identifier stillTexture, Identifier flowingTexture) {
		ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(stillTexture);
			registry.register(flowingTexture);
		});
		FluidSpriteReloadListener.INSTANCE.registerCallback(stillTexture, sprite -> {
			sprites[0] = sprite;
		});
		FluidSpriteReloadListener.INSTANCE.registerCallback(flowingTexture, sprite -> {
			sprites[1] = sprite;
		});
	}

	@Override
	public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return sprites;
	}

	@Override
	public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return color;
	}
}
