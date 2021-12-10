package com.tterrag.registrate.providers;

import com.tterrag.registrate.AbstractRegistrate;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockStateDefinitionProvider;

public class RegistrateBlockstateProvider extends FabricBlockStateDefinitionProvider implements RegistrateProvider {

    private final AbstractRegistrate<?> parent;

    public RegistrateBlockstateProvider(AbstractRegistrate<?> parent, FabricDataGenerator gen) {
        super(gen);
        this.parent = parent;
    }

    @Override
    public EnvType getSide() {
        return EnvType.CLIENT;
    }

    @Override
    public String getName() {
        return "Blockstates";
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        parent.genData(ProviderType.BLOCKSTATE, this);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {

    }
}