package com.eerussianguy.firmalife.client.screen;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.BarrelPressBlockEntity;
import com.eerussianguy.firmalife.common.capabilities.wine.WineType;
import com.eerussianguy.firmalife.common.container.BarrelPressContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import net.dries007.tfc.client.screen.BlockEntityScreen;
import net.dries007.tfc.common.capabilities.Capabilities;

public class BarrelPressScreen extends BlockEntityScreen<BarrelPressBlockEntity, BarrelPressContainer>
{
    private static final ResourceLocation TEXTURE = FLHelpers.identifier("textures/gui/barrel_press.png");

    public BarrelPressScreen(BarrelPressContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, TEXTURE);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderLabels(graphics, mouseX, mouseY);
        if (blockEntity.getOutput() != null)
        {
            final boolean hasCork = blockEntity.getCapability(Capabilities.ITEM).map(inv -> !inv.getStackInSlot(BarrelPressBlockEntity.SLOT_CORK).isEmpty()).orElse(false);
            graphics.drawWordWrap(font, hasCork ? Component.translatable("firmalife.wine.has_output", blockEntity.getOutput().getServings(), FLHelpers.translateEnum(blockEntity.getOutput().getType())) : Component.translatable("firmalife.wine.need_cork"), 17, 40, 110, 0x404040);
            return;
        }
        final WineType type = blockEntity.getWineType();
        if (type != null)
        {
            graphics.drawString(font, Component.translatable("firmalife.wine.making", FLHelpers.translateEnum(type)), 17, 52, 0x404040, false);
        }
    }
}
