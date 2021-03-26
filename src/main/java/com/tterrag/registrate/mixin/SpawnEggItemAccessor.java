package com.tterrag.registrate.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;

@Mixin(SpawnEggItem.class)
public interface SpawnEggItemAccessor {
	@Accessor("SPAWN_EGGS")
	static Map<EntityType<?>, SpawnEggItem> getEggMap() {
		throw new AssertionError();
	}
}
