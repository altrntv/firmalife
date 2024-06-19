package com.eerussianguy.firmalife.common.capabilities.wine;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Tooltips;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.KoppenClimateClassification;


public interface IWine extends INBTSerializable<CompoundTag>
{
    long getCreationDate();

    void setCreationDate(long ticks);

    long getOpenDate();

    void setOpenDate(long ticks);

    @Nullable
    String getLabelText();

    void setLabelText(String text);

    default boolean isSealed()
    {
        return getOpenDate() == -1;
    }

    WineType getWineType();

    void setWineType(WineType type);

    @Nullable
    KoppenClimateClassification getClimate();

    void setClimate(KoppenClimateClassification koppen);

    List<FoodTrait> getTraits();

    void setTraits(List<FoodTrait> traits);

    FluidStack getContents();

    void setContents(FluidStack stack);

    default void addTooltipInfo(List<Component> tooltip)
    {
        if (getContents().isEmpty())
        {
            tooltip.add(Component.translatable("firmalife.wine.empty"));
            if (getLabelText() != null)
                tooltip.add(Component.literal(getLabelText()));
            return;
        }
        if (isSealed())
        {
            if (getLabelText() != null)
                tooltip.add(Component.literal(getLabelText()));
            tooltip.add(Component.translatable("firmalife.wine.age_time", Calendars.CLIENT.getTimeDelta(Calendars.CLIENT.getTicks() - getCreationDate())));
        }
        else
        {
            if (getLabelText() != null)
                tooltip.add(Component.literal(getLabelText()));
            tooltip.add(Component.translatable("firmalife.wine.age_time_opened", Calendars.CLIENT.getTimeDelta(getOpenDate() - getCreationDate())));
            tooltip.add(Tooltips.fluidUnits(getContents().getAmount()).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
        }
        if (getClimate() != null)
            tooltip.add(Helpers.translateEnum(getClimate()).withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC));
        for (FoodTrait trait : getTraits())
        {
            trait.addTooltipInfo(ItemStack.EMPTY, tooltip);
        }
    }
}
