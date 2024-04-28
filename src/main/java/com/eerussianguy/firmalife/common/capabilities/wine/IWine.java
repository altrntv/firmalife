package com.eerussianguy.firmalife.common.capabilities.wine;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.calendar.Calendars;


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

    default void addTooltipInfo(List<Component> tooltip)
    {
        if (isSealed())
        {
            tooltip.add(Component.translatable("firmalife.wine.sealed").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GOLD));
            if (getLabelText() != null)
                tooltip.add(Component.literal(getLabelText()));
            tooltip.add(Component.translatable("firmalife.wine.age_time", Calendars.CLIENT.getTimeDelta(Calendars.CLIENT.getTicks() - getCreationDate())));
        }
        else
        {
            tooltip.add(Component.translatable("firmalife.wine.unsealed").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GOLD));
            if (getLabelText() != null)
                tooltip.add(Component.literal(getLabelText()));
            tooltip.add(Component.translatable("firmalife.wine.age_time_opened", Calendars.CLIENT.getTimeDelta(getOpenDate() - getCreationDate())));
        }
    }
}
