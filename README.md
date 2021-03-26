# Registrate-Fabric
Fabric port of Registrate. Helper library to make creating and registering objects simpler.

Fabric API must be installed for it to work.

## Changes
- Because Fabric does not have registry events, `AbstractRegistrate` now has a `register` method that must be called once all builders have been registered. The registrate object should not be used anymore after this call.
- Datagen functionality has not been implemented yet. Some datagen methods are still available for compatibility with the Forge version, but they don't actually do anything.
- `EntityBuilder` now has three different types and `FluidBuilder` has a few new methods.
- All new classes have been put into the fabric package. These mostly replace Forge classes and functionality.
- Lombok usage has been removed.

## Links
- [Original Forge version repository](https://github.com/tterrag1098/Registrate)
- [Fabric API repository](https://github.com/FabricMC/fabric)
