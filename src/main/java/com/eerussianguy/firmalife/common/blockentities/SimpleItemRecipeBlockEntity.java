package com.eerussianguy.firmalife.common.blockentities;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.recipes.SimpleItemRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.calendar.Calendars;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleItemRecipeBlockEntity<T extends SimpleItemRecipe> extends InventoryBlockEntity<ItemStackHandler>
{
    private final Supplier<Integer> duration;
    protected long startTick;
    @Nullable protected T cachedRecipe;
    protected boolean needsRecipeUpdate = false;

    public SimpleItemRecipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Component defaultName, Supplier<Integer> duration)
    {
        super(type, pos, state, defaultInventory(1), defaultName);
        this.duration = duration;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        startTick = nbt.getLong("startTick");
        super.loadAdditional(nbt);
        needsRecipeUpdate = true;
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putLong("startTick", startTick);
        super.saveAdditional(nbt);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsRecipeUpdate = true;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    abstract void updateCache();

    @Nullable
    public T getCachedRecipe()
    {
        if (cachedRecipe == null && level != null && level.isClientSide)
        {
            updateCache();
        }
        return cachedRecipe;
    }

    public long getTicksLeft()
    {
        assert level != null;
        return getDuration() - (Calendars.get(level).getTicks() - startTick);
    }

    public void start()
    {
        assert level != null;
        updateCache();
        if (cachedRecipe != null)
        {
            startTick = Calendars.get(level).getTicks();
            if (!level.canSeeSky(worldPosition.above()))
            {
                startTick += getDuration(); // takes twice as long indoors
            }
            markForSync();
        }
    }

    public void finish()
    {
        assert level != null;
        final T recipe = this.cachedRecipe;
        if (recipe != null)
        {
            final int ct = readStack().getCount();
            final ItemStack out = recipe.assemble(new ItemStackInventory(readStack()), level.registryAccess());
            out.setCount(out.getCount() * ct);
            inventory.setStackInSlot(0, out);
            updateCache();
            markForSync();
        }
    }

    public void resetCounter()
    {
        startTick = Calendars.SERVER.getTicks();
        markForSync();
    }

    public ItemStack viewStack()
    {
        return inventory.getStackInSlot(0);
    }

    public ItemStack readStack()
    {
        return inventory.getStackInSlot(0).copy();
    }

    public int getDuration()
    {
        return duration.get();
    }
}
