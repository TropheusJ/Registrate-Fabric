package com.tterrag.registrate.fabric;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;

public class FluidSpriteReloadListener implements IdentifiableResourceReloadListener, SynchronousResourceReloadListener {
	public static final Identifier ID = new Identifier("registrate", "fluid_sprites");
	public static final FluidSpriteReloadListener INSTANCE = new FluidSpriteReloadListener();

	private final Multimap<Identifier, Consumer<Sprite>> callbacks = HashMultimap.create();

	public void registerCallback(Identifier id, Consumer<Sprite> callback) {
		callbacks.put(id, callback);
	}

	@Override
	public void apply(ResourceManager manager) {
		// Fluid rendering always uses the block atlas
		Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
		for (Identifier id : callbacks.keySet()) {
			Sprite sprite = atlas.apply(id);
			for (Consumer<Sprite> consumer : callbacks.get(id)) {
				consumer.accept(sprite);
			}
		}
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	public Collection<Identifier> getFabricDependencies() {
		return Arrays.asList(ResourceReloadListenerKeys.TEXTURES, ResourceReloadListenerKeys.MODELS);
	}
}
