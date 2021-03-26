package com.tterrag.registrate.util;

import net.minecraft.util.Lazy;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

public class NonNullLazyValue<T> extends Lazy<T> implements Hack<T> {

    public NonNullLazyValue(NonNullSupplier<T> supplier) {
        super(supplier);
    }

    @Deprecated
    @Override
    public T method_15332() {
    	return super.get();
    }
}

/**
 * This is needed because Supplier's and Lazy's get method have the same name, so NonNullSupplier's get is remapped to intermediary and it stops being a functional interface as it now has two methods.
 */
interface Hack<T> extends NonNullSupplier<T> {
	@Override
	default @NonnullType T get() {
		return method_15332();
	}

	@Deprecated
	T method_15332();
}
