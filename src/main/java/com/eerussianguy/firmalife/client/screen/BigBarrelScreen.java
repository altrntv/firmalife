package com.eerussianguy.firmalife.client.screen;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.BigBarrelBlockEntity;
import com.eerussianguy.firmalife.common.container.BigBarrelContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.screen.BlockEntityScreen;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Tooltips;


public class BigBarrelScreen extends BlockEntityScreen<BigBarrelBlockEntity, BigBarrelContainer>
{
    private static final ResourceLocation BACKGROUND = FLHelpers.identifier("textures/gui/big_barrel.png");

    public BigBarrelScreen(BigBarrelContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);
        imageHeight += 3 * 18;
        inventoryLabelY += 3 * 18 + 1;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        blockEntity.getCapability(Capabilities.FLUID).ifPresent(fluidHandler -> {
            final FluidStack fluidStack = fluidHandler.getFluidInTank(0);
            if (!fluidStack.isEmpty())
            {
                final TextureAtlasSprite sprite = RenderHelpers.getAndBindFluidSprite(fluidStack);
                final int fillHeight = (int) Math.ceil((float) 106 * fluidStack.getAmount() / (float) BigBarrelBlockEntity.CAPACITY);

                RenderHelpers.fillAreaWithSprite(graphics, sprite, leftPos + 8, topPos + 124 - fillHeight, 16, fillHeight, 16, 16);

                resetToBackgroundSprite();
            }
        });

        graphics.blit(texture, getGuiLeft() + 7, getGuiTop() + 17, 176, 0, 18, 107);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);
        final int relX = mouseX - getGuiLeft();
        final int relY = mouseY - getGuiTop();

        if (relX >= 7 && relY >= 17 && relX < 7 + 18 && relY < 17 + 107)
        {
            blockEntity.getCapability(Capabilities.FLUID).ifPresent(fluidHandler -> {
                FluidStack fluid = fluidHandler.getFluidInTank(0);
                if (!fluid.isEmpty())
                {
                    graphics.renderTooltip(font, Tooltips.fluidUnitsOf(fluid), mouseX, mouseY);
                }
            });
        }
    }
}
