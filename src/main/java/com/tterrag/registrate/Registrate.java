package com.tterrag.registrate;

import com.tterrag.registrate.providers.RegistrateDataProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.Nullable;

public class Registrate extends AbstractRegistrate<Registrate> implements DataGeneratorEntrypoint {
    
    /**
     * Create a new {@link Registrate} and register event listeners for registration and data generation. Used in lieu of adding side-effects to constructor, so that alternate initialization
     * strategies can be done in subclasses.
     * 
     * @param modid
     *            The mod ID for which objects will be registered
     * @return The {@link Registrate} instance
     */
    public static Registrate create(String modid) {
        return new Registrate(modid);
    }

    protected Registrate(String modid) {
        super(modid);
    }

    /**
     * This should only be used for datagen
     */
    public Registrate() {
        super("modid");
    }

    @Nullable
    private RegistrateDataProvider provider;

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(provider = new RegistrateDataProvider(this, fabricDataGenerator.getModId(), fabricDataGenerator));
    }
}
