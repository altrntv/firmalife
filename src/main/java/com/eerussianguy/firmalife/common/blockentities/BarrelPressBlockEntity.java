package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;

public class BarrelPressBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static void tick(Level level, BlockPos pos, BlockState state, BarrelPressBlockEntity press)
    {
        final boolean doneWorking = level.getGameTime() - press.lastPushed > TIME;
        if (!press.didAction && doneWorking)
        {
            press.didAction = true;
            press.squish();
        }
        if (!doneWorking && level.isClientSide && level.getGameTime() % 2 == 0)
        {
            final RandomSource rand = level.random;
            final ItemStack item = press.inventory.getStackInSlot(rand.nextInt(SLOTS));
            if (!item.isEmpty())
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, item), pos.getX() + rand.nextFloat(), pos.getY() + 0.2f, pos.getZ() + rand.nextFloat(), rand.nextFloat(), 0.125f + 0.5f * rand.nextFloat(), rand.nextFloat());
        }
    }

    public static final int SLOTS = 6;
    public static final long TIME = 40L;

    private boolean didAction = false;
    private long lastPushed = 0L;

    public BarrelPressBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.BARREL_PRESS.get(), pos, state, defaultInventory(SLOTS), FLHelpers.blockEntityName("barrel_press"));
    }

    public InteractionResult push()
    {
        assert level != null;
        if (level.getGameTime() - lastPushed > TIME)
        {
            didAction = false;
            lastPushed = level.getGameTime();
            markForSync();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void squish()
    {

    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 16;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return super.isItemValid(slot, stack);
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putLong("pushed", this.lastPushed);
        tag.putBoolean("didAction", didAction);
    }

    @Override
    public void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        this.lastPushed = tag.getLong("pushed");
        this.didAction = tag.getBoolean("didAction");
    }

    public float sinceWeLastTouched(float partialTick)
    {
        assert level != null;
        return (level.getGameTime() - lastPushed) + partialTick;
    }
}
