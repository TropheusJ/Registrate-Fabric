package com.tterrag.registrate.util;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.fabric.RegistryObject;

/**
 * @deprecated use {@link com.tterrag.registrate.util.entry.RegistryEntry}
 */
@Deprecated
public class RegistryEntry<T> extends com.tterrag.registrate.util.entry.RegistryEntry<T> {
    
    private RegistryEntry(AbstractRegistrate<?> owner, RegistryObject<T> delegate) {
        super(owner, delegate);
    }
}
