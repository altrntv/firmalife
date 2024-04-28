package com.eerussianguy.firmalife.common.blockentities;

import java.util.Collections;
import java.util.List;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLFruit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.util.Helpers;

public class StompingBarrelBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int MAX_GRAPES = 16;

    private int stomps = 0;

    public StompingBarrelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.STOMPING_BARREL.get(), pos, state, defaultInventory(1), FLHelpers.blockEntityName("stomping_barrel"));
    }

    public void stomp(Entity entity)
    {
        assert level != null;
        if (entity instanceof LivingEntity)
        {
            final ItemStack current = inventory.getStackInSlot(0);
            if (current.isEmpty() || !Helpers.isItem(current, FLTags.Items.GRAPES))
                return;
            stomps += 1;
            Helpers.playSound(level, worldPosition, SoundEvents.SLIME_HURT);

            if (stomps > 16)
            {
                final List<FoodTrait> traits = current.getCapability(FoodCapability.CAPABILITY).map(IFood::getTraits).orElse(Collections.emptyList());
                ItemStack newStack;
                if (Helpers.isItem(current, FLItems.FRUITS.get(FLFruit.RED_GRAPES).get()))
                {
                    newStack = new ItemStack(FLItems.FOODS.get(FLFood.SMASHED_RED_GRAPES).get(), current.getCount());
                }
                else
                {
                    newStack = new ItemStack(FLItems.FOODS.get(FLFood.SMASHED_WHITE_GRAPES).get(), current.getCount());
                }
                for (FoodTrait trait : traits)
                    FoodCapability.applyTrait(newStack, trait);
                inventory.setStackInSlot(0, newStack);
                Helpers.playSound(level, worldPosition, SoundEvents.SLIME_BLOCK_PLACE);
                stomps = 0;
                markForSync();
            }
        }
    }

    public int getStomps()
    {
        return stomps;
    }

    public ItemStack readStack()
    {
        return inventory.getStackInSlot(0);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        stomps = nbt.getInt("stomps");
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putInt("stomps", stomps);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        markForSync();
        stomps = 0;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, FLTags.Items.GRAPES) && stack.getCapability(FoodCapability.CAPABILITY).map(food -> !food.getTraits().contains(FLFoodTraits.DRIED)).orElse(false);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return MAX_GRAPES;
    }
}
