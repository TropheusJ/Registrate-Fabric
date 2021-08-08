package com.tterrag.registrate.fabric;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class SimpleFluidRenderHandler implements FluidRenderHandler {
	private final TextureAtlasSprite[] sprites = new TextureAtlasSprite[2];
	private final int color;

	public SimpleFluidRenderHandler(int color) {
		this.color = color;
	}

	public SimpleFluidRenderHandler() {
		this(-1);
	}

	public void registerListeners(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
		ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register((atlasTexture, registry) -> {
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
	public TextureAtlasSprite[] getFluidSprites(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
		return sprites;
	}

	@Override
	public int getFluidColor(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
		return color;
	}
}
