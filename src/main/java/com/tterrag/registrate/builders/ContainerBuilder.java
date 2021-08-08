package com.tterrag.registrate.builders;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.fabric.EnvExecutor;
import com.tterrag.registrate.fabric.RegistryObject;
import com.tterrag.registrate.fabric.ScreenHandlerRegistryExtension;
import com.tterrag.registrate.util.entry.ContainerEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

public class ContainerBuilder<T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>,  P> extends AbstractBuilder<MenuType<?>, MenuType<T>, P, ContainerBuilder<T, S, P>> {
    
    public interface ContainerFactory<T extends AbstractContainerMenu> {
        
        T create(MenuType<T> type, int windowId, Inventory inv);
    }

    public interface ForgeContainerFactory<T extends AbstractContainerMenu> {

        T create(MenuType<T> type, int windowId, Inventory inv, @Nullable FriendlyByteBuf buffer);
    }
    
    // same as ScreenRegistry.Factory
    public interface ScreenFactory<C extends AbstractContainerMenu, T extends Screen & MenuAccess<C>> {
        
        T create(C container, Inventory inv, Component displayName);
    }
    
    private final ContainerFactory<T> factory;
    private final ForgeContainerFactory<T> forgeFactory;
    private final NonNullSupplier<ScreenFactory<T, S>> screenFactory;

    public ContainerBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, S>> screenFactory) {
        super(owner, parent, name, callback, MenuType.class);
        this.factory = factory;
        this.forgeFactory = null;
        this.screenFactory = screenFactory;
    }

    public ContainerBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ForgeContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, S>> screenFactory) {
        super(owner, parent, name, callback, MenuType.class);
        this.factory = null;
        this.forgeFactory = factory;
        this.screenFactory = screenFactory;
    }

    @Override
    protected @NonnullType MenuType<T> createEntry() {
        NonNullSupplier<MenuType<T>> supplier = this.asSupplier();
        MenuType<T> ret;
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
    protected RegistryEntry<MenuType<T>> createEntryWrapper(RegistryObject<MenuType<T>> delegate) {
        return new ContainerEntry<>(getOwner(), delegate);
    }

    @Override
    public ContainerEntry<T> register() {
        return (ContainerEntry<T>) super.register();
    }
}
