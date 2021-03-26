package com.tterrag.registrate.fabric;

import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class EnvExecutor {
	public static void runWhenOn(EnvType env, Supplier<Runnable> toRun) {
		if (FabricLoader.getInstance().getEnvironmentType() == env) {
			toRun.get().run();
		}
	}
}
