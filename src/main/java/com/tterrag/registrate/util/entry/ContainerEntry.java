package com.tterrag.registrate.util.entry;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.fabric.RegistryObject;

public class ContainerEntry<T extends ScreenHandler> extends RegistryEntry<ScreenHandlerType<T>> {

    public ContainerEntry(AbstractRegistrate<?> owner, RegistryObject<ScreenHandlerType<T>> delegate) {
        super(owner, delegate);
    }

    public T create(int windowId, PlayerInventory playerInv) {
        return get().create(windowId, playerInv);
    }

    public ScreenHandlerFactory asProvider() {
        return (window, playerinv, $) -> create(window, playerinv);
    }

    public void open(ServerPlayerEntity player, Text displayName) {
        open(player, displayName, asProvider());
    }

    public void open(ServerPlayerEntity player, Text displayName, Consumer<PacketByteBuf> extraData) {
        open(player, displayName, asProvider(), extraData);
    }

    public void open(ServerPlayerEntity player, Text displayName, ScreenHandlerFactory provider) {
    	player.openHandledScreen(new SimpleNamedScreenHandlerFactory(provider, displayName));
    }

    public void open(ServerPlayerEntity player, Text displayName, ScreenHandlerFactory provider, Consumer<PacketByteBuf> extraData) {
        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
			@Override
			public Text getDisplayName() {
				return displayName;
			}

			@Override
			public ScreenHandler createMenu(int arg0, PlayerInventory arg1, PlayerEntity arg2) {
				return provider.createMenu(arg0, arg1, arg2);
			}

			@Override
			public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
				extraData.accept(buf);
			}
        });
    }
}
