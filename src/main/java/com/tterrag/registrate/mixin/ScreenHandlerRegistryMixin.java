package com.tterrag.registrate.mixin;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.SimpleClientHandlerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import com.tterrag.registrate.fabric.ScreenHandlerRegistryExtension;

@Mixin(ScreenHandlerRegistry.class)
public class ScreenHandlerRegistryMixin {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/Registry;register(Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/Identifier;Ljava/lang/Object;)Ljava/lang/Object;"), method = "registerSimple", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void onRegisterSimple(Identifier id, SimpleClientHandlerFactory<?> factory, CallbackInfoReturnable<ScreenHandlerType<?>> cir, ScreenHandlerType<?> type) {
		if (ScreenHandlerRegistryExtension.createOnly) {
			cir.setReturnValue(type);
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/Registry;register(Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/Identifier;Ljava/lang/Object;)Ljava/lang/Object;"), method = "registerExtended", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void onRegisterExtended(Identifier id, ExtendedClientHandlerFactory<?> factory, CallbackInfoReturnable<ScreenHandlerType<?>> cir, ScreenHandlerType<?> type) {
		if (ScreenHandlerRegistryExtension.createOnly) {
			cir.setReturnValue(type);
		}
	}
}
