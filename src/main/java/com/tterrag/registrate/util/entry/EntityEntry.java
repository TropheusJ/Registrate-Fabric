package com.tterrag.registrate.util.entry;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.fabric.RegistryObject;

public class EntityEntry<T extends Entity> extends RegistryEntry<EntityType<T>> {

    public EntityEntry(AbstractRegistrate<?> owner, RegistryObject<EntityType<T>> delegate) {
        super(owner, delegate);
    }

    public @Nullable T create(World world) {
        return get().create(world);
    }

    public boolean is(Entity t) {
        return t != null && t.getType() == get();
    }

    public static <T extends Entity> EntityEntry<T> cast(RegistryEntry<EntityType<T>> entry) {
        return RegistryEntry.cast(EntityEntry.class, entry);
    }
}
