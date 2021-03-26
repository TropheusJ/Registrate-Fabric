package com.tterrag.registrate.util;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import com.tterrag.registrate.mixin.SpawnEggItemAccessor;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

public class LazySpawnEggItem<T extends Entity> extends SpawnEggItem {

    private final NonNullSupplier<EntityType<T>> typeIn;

    public LazySpawnEggItem(final NonNullSupplier<EntityType<T>> type, int primaryColor, int secondaryColor, Settings properties) {
        super(null, primaryColor, secondaryColor, properties);
        this.typeIn = type;
        SpawnEggItemAccessor.getEggMap().remove(null);
    }

    public void injectType() {
    	SpawnEggItemAccessor.getEggMap().put(typeIn.get(), this);
    }

    public EntityType<?> getEntityType(@Nullable CompoundTag p_208076_1_) {
        if (p_208076_1_ != null && p_208076_1_.contains("EntityTag", 10)) {
            return super.getEntityType(p_208076_1_);
        }

        return this.typeIn.get();
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            ItemStack itemstack = context.getStack();
            BlockPos blockpos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockState blockstate = world.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (block == Blocks.SPAWNER) {
                BlockEntity tileentity = world.getBlockEntity(blockpos);
                if (tileentity instanceof MobSpawnerBlockEntity) {
                    MobSpawnerLogic abstractspawner = ((MobSpawnerBlockEntity) tileentity).getLogic();
                    EntityType<?> entitytype1 = this.getEntityType(itemstack.getTag());
                    abstractspawner.setEntityId(entitytype1);
                    tileentity.markDirty();
                    world.updateListeners(blockpos, blockstate, blockstate, 3);
                    itemstack.decrement(1);
                    return ActionResult.SUCCESS;
                }
            }

            BlockPos blockpos1;
            if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.offset(direction);
            }

            EntityType<?> entitytype = this.getEntityType(itemstack.getTag());
            if (entitytype.spawnFromItemStack((ServerWorld) world, itemstack, context.getPlayer(), blockpos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP) != null) {
                itemstack.decrement(1);
            }

            return ActionResult.SUCCESS;
        }
    }

    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getStackInHand(handIn);
        if (worldIn.isClient) {
            return new TypedActionResult<>(ActionResult.PASS, itemstack);
        } else {
            HitResult raytraceresult = raycast(worldIn, playerIn, RaycastContext.FluidHandling.SOURCE_ONLY);
            if (raytraceresult.getType() != HitResult.Type.BLOCK) {
                return new TypedActionResult<>(ActionResult.PASS, itemstack);
            } else {
                BlockHitResult blockraytraceresult = (BlockHitResult) raytraceresult;
                BlockPos blockpos = blockraytraceresult.getBlockPos();
                if (!(worldIn.getBlockState(blockpos).getBlock() instanceof FluidBlock)) {
                    return new TypedActionResult<>(ActionResult.PASS, itemstack);
                } else if (worldIn.canPlayerModifyAt(playerIn, blockpos) && playerIn.canPlaceOn(blockpos, blockraytraceresult.getSide(), itemstack)) {
                    EntityType<?> entitytype = this.getEntityType(itemstack.getTag());
                    if (entitytype.spawnFromItemStack((ServerWorld) worldIn, itemstack, playerIn, blockpos, SpawnReason.SPAWN_EGG, false, false) == null) {
                        return new TypedActionResult<>(ActionResult.PASS, itemstack);
                    } else {
                        if (!playerIn.abilities.creativeMode) {
                            itemstack.decrement(1);
                        }

                        playerIn.incrementStat(Stats.USED.getOrCreateStat(this));
                        return new TypedActionResult<>(ActionResult.SUCCESS, itemstack);
                    }
                } else {
                    return new TypedActionResult<>(ActionResult.FAIL, itemstack);
                }
            }
        }
    }
}
