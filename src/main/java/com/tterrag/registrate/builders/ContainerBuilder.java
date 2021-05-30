package com.tterrag.registrate.builders;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.fabric.EnvExecutor;
import com.tterrag.registrate.fabric.RegistryObject;
import com.tterrag.registrate.fabric.ScreenHandlerRegistryExtension;
import com.tterrag.registrate.util.entry.ContainerEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

public class ContainerBuilder<T extends ScreenHandler, S extends Screen & ScreenHandlerProvider<T>,  P> extends AbstractBuilder<ScreenHandlerType<?>, ScreenHandlerType<T>, P, ContainerBuilder<T, S, P>> {
    
    public interface ContainerFactory<T extends ScreenHandler> {
        
        T create(ScreenHandlerType<T> type, int windowId, PlayerInventory inv);
    }

    public interface ForgeContainerFactory<T extends ScreenHandler> {

        T create(ScreenHandlerType<T> type, int windowId, PlayerInventory inv, @Nullable PacketByteBuf buffer);
    }
    
    // same as ScreenRegistry.Factory
    public interface ScreenFactory<C extends ScreenHandler, T extends Screen & ScreenHandlerProvider<C>> {
        
        T create(C container, PlayerInventory inv, Text displayName);
    }
    
    private final ContainerFactory<T> factory;
    private final ForgeContainerFactory<T> forgeFactory;
    private final NonNullSupplier<ScreenFactory<T, S>> screenFactory;

    public ContainerBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, S>> screenFactory) {
        super(owner, parent, name, callback, ScreenHandlerType.class);
        this.factory = factory;
        this.forgeFactory = null;
        this.screenFactory = screenFactory;
    }

    public ContainerBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ForgeContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, S>> screenFactory) {
        super(owner, parent, name, callback, ScreenHandlerType.class);
        this.factory = null;
        this.forgeFactory = factory;
        this.screenFactory = screenFactory;
    }

    @Override
    protected @NonnullType ScreenHandlerType<T> createEntry() {
        NonNullSupplier<ScreenHandlerType<T>> supplier = this.asSupplier();
        ScreenHandlerType<T> ret;
        ScreenHandlerRegistryExtension.createOnly = true;
        if (this.factory == null) {
            ForgeContainerFactory<T> factory = this.forgeFactory;
            ret = ScreenHandlerRegistry.registerExtended(null, (windowId, inv, buf) -> factory.create(supplier.get(), windowId, inv, buf));
        } else {
            ContainerFactory<T> factory = this.factory;
            ret = ScreenHandlerRegistry.registerSimple(null, (syncId, inventory) -> factory.create(supplier.get(), syncId, inventory));
        }
        ScreenHandlerRegistryExtension.createOnly = false;
        EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
            ScreenFactory<T, S> screenFactory = this.screenFactory.get();
            ScreenRegistry.<T, S>register(ret, (type, inv, displayName) -> screenFactory.create(type, inv, displayName));
        });
        return ret;
    }

    @Override
    protected RegistryEntry<ScreenHandlerType<T>> createEntryWrapper(RegistryObject<ScreenHandlerType<T>> delegate) {
        return new ContainerEntry<>(getOwner(), delegate);
    }

    @Override
    public ContainerEntry<T> register() {
        return (ContainerEntry<T>) super.register();
    }
}
