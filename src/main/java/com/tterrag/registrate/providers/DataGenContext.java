package com.tterrag.registrate.providers;

import net.minecraft.util.Identifier;

import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

/**
 * A context bean passed to data generator callbacks. Contains the entry that data is being created for, and some metadata about the entry.
 *
 * @param <R>
 *            Type of the registry to which the entry belongs
 * @param <E>
 *            Type of the object for which data is being generated
 */
public final class DataGenContext<R, E extends R> implements NonNullSupplier<E> {
    private final NonNullSupplier<E> entry;
    private final String name;
    private final Identifier id;

    @SuppressWarnings("null")
    @NonnullType
    public E getEntry() {
        return entry.get();
    }

    public static <R, E extends R> DataGenContext<R, E> from(Builder<R, E, ?, ?> builder, Class<? super R> clazz) {
        return new DataGenContext<R, E>(NonNullSupplier.of(builder.getOwner().<R, E>get(builder.getName(), clazz)), builder.getName(), new Identifier(builder.getOwner().getModid(), builder.getName()));
    }

    @javax.annotation.Generated("lombok")
    public DataGenContext(final NonNullSupplier<E> entry, final String name, final Identifier id) {
        this.entry = entry;
        this.name = name;
        this.id = id;
    }

    @javax.annotation.Generated("lombok")
    public String getName() {
        return this.name;
    }

    @javax.annotation.Generated("lombok")
    public Identifier getId() {
        return this.id;
    }

    @Override
    @javax.annotation.Generated("lombok")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DataGenContext)) return false;
        final DataGenContext<?, ?> other = (DataGenContext<?, ?>) o;
        final Object this$entry = this.getEntry();
        final Object other$entry = other.getEntry();
        if (this$entry == null ? other$entry != null : !this$entry.equals(other$entry)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        return true;
    }

    @Override
    @javax.annotation.Generated("lombok")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $entry = this.getEntry();
        result = result * PRIME + ($entry == null ? 43 : $entry.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    @Override
    @javax.annotation.Generated("lombok")
    public String toString() {
        return "DataGenContext(entry=" + this.getEntry() + ", name=" + this.getName() + ", id=" + this.getId() + ")";
    }

    @javax.annotation.Generated("lombok")
    public E get() {
        return this.entry.get();
    }
}
