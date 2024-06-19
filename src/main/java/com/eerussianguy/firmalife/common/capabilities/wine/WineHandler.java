package com.eerussianguy.firmalife.common.capabilities.wine;

import java.util.ArrayList;
import java.util.List;
import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.climate.KoppenClimateClassification;

public class WineHandler implements IWine, ICapabilitySerializable<CompoundTag>
{
    private final ItemStack stack;
    private long creationDate = -1L;
    private long openDate = -1L;
    private final LazyOptional<IWine> capability = LazyOptional.of(() -> this);
    private boolean initialized = false;
    @Nullable private String labelText = null;
    private WineType wineType = WineType.RED;
    @Nullable private KoppenClimateClassification climate = null;
    private List<FoodTrait> traits = new ArrayList<>();
    private FluidStack contents = FluidStack.EMPTY;

    public WineHandler(ItemStack stack)
    {
        this.stack = stack;
    }

    @Nullable
    @Override
    public String getLabelText()
    {
        return labelText;
    }

    @Override
    public void setLabelText(String labelText)
    {
        this.labelText = labelText;
        save();
    }

    @Override
    public WineType getWineType()
    {
        return wineType;
    }

    @Override
    public void setWineType(WineType type)
    {
        this.wineType = type;
        save();
    }

    @Override
    public @Nullable KoppenClimateClassification getClimate()
    {
        return climate;
    }

    @Override
    public void setClimate(@Nullable KoppenClimateClassification koppen)
    {
        this.climate = koppen;
        save();
    }

    @Override
    public long getCreationDate()
    {
        return creationDate;
    }

    @Override
    public void setCreationDate(long ticks)
    {
        this.creationDate = ticks;
        save();
    }

    @Override
    public long getOpenDate()
    {
        return openDate;
    }

    @Override
    public void setOpenDate(long ticks)
    {
        this.openDate = ticks;
        save();
    }

    @Override
    public List<FoodTrait> getTraits()
    {
        return traits;
    }

    @Override
    public void setTraits(List<FoodTrait> traits)
    {
        this.traits = traits;
        save();
    }

    @Override
    public FluidStack getContents()
    {
        return contents;
    }

    @Override
    public void setContents(FluidStack contents)
    {
        this.contents = contents;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) { }

    private void load()
    {
        if (!initialized)
        {
            initialized = true;

            final CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("wine", Tag.TAG_COMPOUND))
            {
                final CompoundTag wineTag = tag.getCompound("wine");
                creationDate = wineTag.getLong("creationDate");
                openDate = wineTag.getLong("openDate");
                wineType = WineType.VALUES[wineTag.getInt("wineType")];
                if (wineTag.contains("label", Tag.TAG_STRING))
                    labelText = wineTag.getString("label");
                if (wineTag.contains("climate", Tag.TAG_INT))
                    climate = WineType.KOPPEN_VALUES[wineTag.getInt("climate")];
                FLHelpers.readTraitList(traits, wineTag, "traits");
                contents = FluidStack.loadFluidStackFromNBT(wineTag.getCompound("contents"));
            }
        }
    }

    private void save()
    {
        final CompoundTag tag = stack.getOrCreateTag();
        final CompoundTag wineTag = new CompoundTag();
        wineTag.putLong("creationDate", creationDate);
        wineTag.putLong("openDate", openDate);
        wineTag.putInt("wineType", wineType.ordinal());
        if (labelText != null)
            wineTag.putString("label", labelText);
        if (climate != null)
            wineTag.putInt("climate", climate.ordinal());
        FLHelpers.writeTraitList(traits, wineTag, "traits");
        wineTag.put("contents", contents.writeToNBT(new CompoundTag()));
        tag.put("wine", wineTag);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == WineCapability.CAPABILITY)
        {
            load();
            return capability.cast();
        }
        return LazyOptional.empty();
    }
}
