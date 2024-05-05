package com.eerussianguy.firmalife.client;

import java.util.List;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.capabilities.wine.WineCapability;
import com.eerussianguy.firmalife.common.util.Plantable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import com.eerussianguy.firmalife.common.capabilities.bee.BeeCapability;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.SelfTests;

public class FLClientForgeEvents
{
    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(FLClientForgeEvents::onSelfTest);
        bus.addListener(FLClientForgeEvents::onTooltip);
    }

    private static void onSelfTest(SelfTests.ClientSelfTestEvent event)
    {
        FLClientSelfTests.runClientSelfTests();
    }

    private static void onTooltip(ItemTooltipEvent event)
    {
        final ItemStack stack = event.getItemStack();
        final List<Component> text = event.getToolTip();
        if (!stack.isEmpty())
        {
            stack.getCapability(BeeCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(text));
            stack.getCapability(WineCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(text));
            final Plantable plantable = Plantable.get(stack);
            if (plantable != null)
            {
                plantable.addTooltipInfo(text);
            }
            if (Helpers.isItem(stack, FLTags.Items.BEEKEEPER_ARMOR))
            {
                text.add(Component.translatable("firmalife.tooltip.beekeeper_armor"));
            }
        }
    }
}
