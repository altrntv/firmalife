package com.eerussianguy.firmalife.common.capabilities.wine;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;
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

    default void addTooltipInfo(List<Component> tooltip)
    {
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
        }
        if (getClimate() != null)
            tooltip.add(Helpers.translateEnum(getClimate()).withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC));
    }
}
