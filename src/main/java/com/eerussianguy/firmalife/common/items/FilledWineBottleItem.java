package com.eerussianguy.firmalife.common.items;

import java.util.List;
import com.eerussianguy.firmalife.common.capabilities.wine.WineCapability;
import com.eerussianguy.firmalife.common.capabilities.wine.WineHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

public class FilledWineBottleItem extends WineBottleItem
{
    public FilledWineBottleItem(Properties properties, ResourceLocation modelLocation)
    {
        super(properties, modelLocation);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess carried)
    {
        if (action == ClickAction.SECONDARY && Helpers.isItem(other, TFCTags.Items.KNIVES))
        {
            return stack.getCapability(WineCapability.CAPABILITY).map(wine -> {
                if (wine.isSealed())
                {
                    player.playSound(SoundEvents.BAMBOO_BREAK);
                    wine.setOpenDate(Calendars.get(player.level()).getTicks());
                    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(FLItems.CORK.get()));
                    other.hurtAndBreak(1, player, p -> {});
                    return true;
                }
                return false;
            }).orElse(false);
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag debug)
    {
        stack.getCapability(WineCapability.CAPABILITY).ifPresent(wine -> {
            if (wine.isSealed())
            {
                tooltip.add(Component.translatable("firmalife.wine.how_to_open").withStyle(ChatFormatting.GRAY));
            }
        });
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new WineHandler(stack);
    }
}
