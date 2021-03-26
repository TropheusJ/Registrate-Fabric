package com.tterrag.registrate.providers;

import net.fabricmc.api.EnvType;

import net.minecraft.data.DataProvider;

public interface RegistrateProvider extends DataProvider {
    
    EnvType getSide();
}
