package com.eerussianguy.firmalife.client.screen;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import com.eerussianguy.firmalife.common.container.BeehiveContainer;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.screen.BlockEntityScreen;

public class BeehiveScreen extends BlockEntityScreen<FLBeehiveBlockEntity, BeehiveContainer>
{
    private static final ResourceLocation TEXTURE = FLHelpers.identifier("textures/gui/beehive.png");

    public BeehiveScreen(BeehiveContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, TEXTURE);
    }

    @Override
    public void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        final float honeyLevel = blockEntity.getHoney() / (float) blockEntity.getMaxHoney();
        if (honeyLevel > 0)
        {
            final int pixels = 26 - Mth.ceil(honeyLevel * 26);
            graphics.blit(texture, leftPos + 43, topPos + 40 + pixels, 176, pixels, 72, 65 - pixels);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, int x, int y)
    {
        super.renderTooltip(graphics, x, y);
        if (RenderHelpers.isInside(x, y, leftPos + 43, topPos + 40, 72, 26))
        {
            graphics.renderTooltip(font, Component.translatable("firmalife.beehive.honey", blockEntity.getHoney()).withStyle(ChatFormatting.GOLD), x, y);
        }
    }
}
