package com.tterrag.registrate.mixin;

import java.util.Map;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpawnEggItem.class)
public interface SpawnEggItemAccessor {
	@Accessor("BY_ID")
	static Map<EntityType<?>, SpawnEggItem> getEggMap() {
		throw new AssertionError();
	}
}
