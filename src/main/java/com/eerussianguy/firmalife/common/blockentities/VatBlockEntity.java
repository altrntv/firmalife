package com.eerussianguy.firmalife.common.blockentities;

import java.util.ArrayList;
import java.util.List;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.VatBlock;
import com.eerussianguy.firmalife.common.recipes.FLRecipeTypes;
import com.eerussianguy.firmalife.common.recipes.VatRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class VatBlockEntity extends BoilingBlockEntity<VatBlockEntity.VatInventory>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, VatBlockEntity vat)
    {
        vat.checkForLastTickSync();
        vat.checkForCalendarUpdate();

        if (vat.needsRecipeUpdate)
        {
            vat.updateCachedRecipe();
        }

        final List<ItemStack> excess = vat.inventory.excess;
        if (!excess.isEmpty() && vat.inventory.getStackInSlot(0).isEmpty())
        {
            vat.inventory.setStackInSlot(0, excess.remove(0));
        }
        vat.handleJarring();
        vat.tickTemperature();
        vat.handleCooking();
    }

    public static final int CAPACITY = 10_000;

    @Nullable private VatRecipe cachedRecipe = null;
    private ItemStack jarOutput = ItemStack.EMPTY;
    @Nullable private ResourceLocation lastTexture = null;

    public VatBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.VAT.get(), pos, state, VatInventory::new, FLHelpers.blockEntityName("vat"));

        sidedInventory
            .on(new PartialItemHandler(inventory).insert(), Direction.Plane.HORIZONTAL);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        jarOutput = nbt.contains("jarOutput", Tag.TAG_COMPOUND) ? ItemStack.of(nbt.getCompound("jarOutput")) : ItemStack.EMPTY;
        lastTexture = nbt.contains("lastTexture", Tag.TAG_STRING) ? FLHelpers.res(nbt.getString("lastTexture")) : null;
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        if (!jarOutput.isEmpty())
        {
            nbt.put("jarOutput", jarOutput.serializeNBT());
        }
        if (lastTexture != null)
        {
            nbt.putString("lastTexture", lastTexture.toString());
        }
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        if (jarOutput.isEmpty())
        {
            lastTexture = null;
        }
    }

    public boolean hasOutput()
    {
        return !jarOutput.isEmpty();
    }

    public ItemStack getOutput()
    {
        return jarOutput;
    }

    public ItemStack takeOutput()
    {
        markForSync();
        return jarOutput.isEmpty() ? ItemStack.EMPTY : jarOutput.split(1);
    }

    public void setOutput(ItemStack stack, @Nullable ResourceLocation texture)
    {
        this.jarOutput = stack;
        this.lastTexture = texture;
    }

    @Nullable
    public ResourceLocation getJarTexture()
    {
        return lastTexture;
    }

    public void handleJarring()
    {
        final FluidStack fluid = inventory.getFluidInTank(0);
        final ItemStack stack = inventory.getStackInSlot(0);
        if (fluid.hasTag() && fluid.getTag().contains("fruit", Tag.TAG_COMPOUND) && Helpers.isItem(stack, TFCItems.EMPTY_JAR_WITH_LID.get()))
        {
            final int jars = stack.getCount();
            final int maxFill = fluid.getAmount() / 500;
            if (jars <= maxFill && maxFill > 0)
            {
                final ItemStack newStack = ItemStack.of(fluid.getTag().getCompound("fruit"));
                newStack.setCount(jars);
                inventory.setStackInSlot(0, newStack);
                inventory.drain(500 * jars, IFluidHandler.FluidAction.EXECUTE);
                markForSync();
            }
        }
    }

    public void updateCachedRecipe()
    {
        assert level != null;
        if (inventory.excess.isEmpty())
        {
            cachedRecipe = level.getRecipeManager().getRecipeFor(FLRecipeTypes.VAT.get(), inventory, level).orElse(null);
        }
        else
        {
            cachedRecipe = null;
        }
        needsRecipeUpdate = false;
    }

    public void handleCooking()
    {
        assert level != null;
        if (isBoiling())
        {
            assert cachedRecipe != null;
            if (boilingTicks < cachedRecipe.getDuration())
            {
                boilingTicks++;
                if (boilingTicks == 1) markForSync();
            }
            else
            {
                final VatRecipe recipe = cachedRecipe;
                cachedRecipe = null;
                recipe.assembleOutputs(this, inventory);
                boilingTicks = 0;
                updateCachedRecipe();
                markForSync();
                if (getBlockState().hasProperty(VatBlock.SEALED))
                {
                    level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(VatBlock.SEALED, false));
                }
            }
        }
        else if (boilingTicks > 0)
        {
            boilingTicks = 0;
            markForSync();
        }
    }

    @Override
    public boolean isBoiling()
    {
        assert level != null;
        if (hasOutput())
            return false;
        if (getBlockState().hasProperty(VatBlock.SEALED) && !getBlockState().getValue(VatBlock.SEALED))
        {
            return false;
        }
        if (level.isClientSide)
        {
            return boilingTicks > 0;
        }
        return cachedRecipe != null && temperature > cachedRecipe.getTemperature();
    }

    public static class VatInventory extends BoilingInventory
    {
        private final List<ItemStack> excess;
        private final VatBlockEntity vat;

        public VatInventory(InventoryBlockEntity<?> entity)
        {
            super(entity, 1, new InventoryFluidTank(CAPACITY, fluid -> Helpers.isFluid(fluid.getFluid(), TFCTags.Fluids.USABLE_IN_POT), (VatBlockEntity) entity));
            this.excess = new ArrayList<>();
            this.vat = (VatBlockEntity) entity;
        }

        public void insertItemWithOverflow(ItemStack stack)
        {
            final ItemStack remainder = inventory.insertItem(0, stack, false);
            if (!remainder.isEmpty())
            {
                excess.add(remainder);
            }
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return vat.hasOutput() ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag nbt = super.serializeNBT();
            FLHelpers.writeItemStackList(excess, nbt, "excess");
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            super.deserializeNBT(nbt);
            FLHelpers.readItemStackList(excess, nbt, "excess");
        }
    }
}
