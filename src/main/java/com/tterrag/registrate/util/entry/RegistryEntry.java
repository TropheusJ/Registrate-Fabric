package com.tterrag.registrate.util.entry;

import java.util.Objects;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.registry.Registry;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.fabric.RegistryObject;
import com.tterrag.registrate.fabric.RegistryUtil;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

/**
 * Wraps a {@link RegistryObject}, providing a cleaner API with null-safe access, and registrate-specific extensions such as {@link #getSibling(Class)}.
 *
 * @param <T>
 *            The type of the entry
 */
public class RegistryEntry<T> implements NonNullSupplier<T> {
    private static RegistryEntry<?> EMPTY = new RegistryEntry(null, RegistryObject.empty());

    public static <T> RegistryEntry<T> empty() {
        @SuppressWarnings("unchecked")
        RegistryEntry<T> t = (RegistryEntry<T>) EMPTY;
        return t;
    }


    private interface Exclusions<T> {
        T get();

        RegistryObject<T> filter(Predicate<? super T> predicate);

        void updateReference(Registry<? extends T> registry);
    }

    private final AbstractRegistrate<?> owner;
    @Nullable
    private final RegistryObject<T> delegate;

    @SuppressWarnings("unused")
    public RegistryEntry(AbstractRegistrate<?> owner, RegistryObject<T> delegate) {
        if (EMPTY != null && owner == null) throw new NullPointerException("Owner must not be null");
        if (EMPTY != null && delegate == null) throw new NullPointerException("Delegate must not be null");
        this.owner = owner;
        this.delegate = delegate;
    }

    /**
     * Update the underlying entry manually from the given registry.
     * 
     * @param registry
     *            The registry to pull the entry from.
     */
    @SuppressWarnings("unchecked")
    public void updateReference(Registry<? super T> registry) {
        RegistryObject<T> delegate = this.delegate;
        Objects.requireNonNull(delegate, "Registry entry is empty").updateReference(/*(Registry<? extends T>)*/ registry);
    }

    /**
     * Get the entry, throwing an exception if it is not present for any reason.
     * 
     * @return The (non-null) entry
     */
    @Override
    @NonnullType
    public T get() {
        RegistryObject<T> delegate = this.delegate;
        return Objects.requireNonNull(getUnchecked(), () -> delegate == null ? "Registry entry is empty" : "Registry entry not present: " + delegate.getId());
    }

    /**
     * Get the entry without performing any checks.
     * 
     * @return The (nullable) entry
     */
    @Nullable
    public T getUnchecked() {
        RegistryObject<T> delegate = this.delegate;
        return delegate == null ? null : delegate.orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <R, E extends R> RegistryEntry<E> getSibling(Class<? super R> registryType) {
        return this == EMPTY ? empty() : owner.get(getId().getPath(), (Class<R>) registryType);
    }

    public <R, E extends R> RegistryEntry<E> getSibling(Registry<R> registry) {
        return getSibling(RegistryUtil.getRegistrationClass(registry));
    }

    /**
     * If an entry is present, and the entry matches the given predicate, return an {@link RegistryEntry} describing the value, otherwise return an empty {@link RegistryEntry}.
     *
     * @param predicate
     *            a {@link Predicate predicate} to apply to the entry, if present
     * @return an {@link RegistryEntry} describing the value of this {@link RegistryEntry} if the entry is present and matches the given predicate, otherwise an empty {@link RegistryEntry}
     * @throws NullPointerException
     *             if the predicate is null
     */
    public RegistryEntry<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent() || predicate.test(get())) {
            return this;
        }
        return empty();
    }

    public <R> boolean is(R entry) {
        return get() == entry;
    }

    @SuppressWarnings("unchecked")
    protected static <E extends RegistryEntry<?>> E cast(Class<? super E> clazz, RegistryEntry<?> entry) {
        if (clazz.isInstance(entry)) {
            return (E) entry;
        }
        throw new IllegalArgumentException("Could not convert RegistryEntry: expecting " + clazz + ", found " + entry.getClass());
    }

    @Override
    @javax.annotation.Generated("lombok")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof RegistryEntry)) return false;
        final RegistryEntry<?> other = (RegistryEntry<?>) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$delegate = this.delegate;
        final Object other$delegate = other.delegate;
        if (this$delegate == null ? other$delegate != null : !this$delegate.equals(other$delegate)) return false;
        return true;
    }

    @javax.annotation.Generated("lombok")
    protected boolean canEqual(final Object other) {
        return other instanceof RegistryEntry;
    }

    @Override
    @javax.annotation.Generated("lombok")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $delegate = this.delegate;
        result = result * PRIME + ($delegate == null ? 43 : $delegate.hashCode());
        return result;
    }

    @javax.annotation.Generated("lombok")
    public net.minecraft.util.Identifier getId() {
        return this.delegate.getId();
    }

    @javax.annotation.Generated("lombok")
    public java.util.stream.Stream<T> stream() {
        return this.delegate.stream();
    }

    @javax.annotation.Generated("lombok")
    public boolean isPresent() {
        return this.delegate.isPresent();
    }

    @javax.annotation.Generated("lombok")
    public void ifPresent(final java.util.function.Consumer<? super T> consumer) {
        this.delegate.ifPresent(consumer);
    }

    @javax.annotation.Generated("lombok")
    public <U extends java.lang.Object> java.util.Optional<U> map(final java.util.function.Function<? super T, ? extends U> mapper) {
        return this.delegate.<U>map(mapper);
    }

    @javax.annotation.Generated("lombok")
    public <U extends java.lang.Object> java.util.Optional<U> flatMap(final java.util.function.Function<? super T, java.util.Optional<U>> mapper) {
        return this.delegate.<U>flatMap(mapper);
    }

    @javax.annotation.Generated("lombok")
    public <U extends java.lang.Object> java.util.function.Supplier<U> lazyMap(final java.util.function.Function<? super T, ? extends U> mapper) {
        return this.delegate.<U>lazyMap(mapper);
    }

    @javax.annotation.Generated("lombok")
    public T orElse(final T other) {
        return this.delegate.orElse(other);
    }

    @javax.annotation.Generated("lombok")
    public T orElseGet(final java.util.function.Supplier<? extends T> other) {
        return this.delegate.orElseGet(other);
    }

    @javax.annotation.Generated("lombok")
    public <X extends java.lang.Throwable> T orElseThrow(final java.util.function.Supplier<? extends X> exceptionSupplier) throws X {
        return this.delegate.<X>orElseThrow(exceptionSupplier);
    }
}
