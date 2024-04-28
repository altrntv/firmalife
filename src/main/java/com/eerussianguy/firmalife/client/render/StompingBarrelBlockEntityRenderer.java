package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.StompingBarrelBlockEntity;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLFruit;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.util.Helpers;

public class StompingBarrelBlockEntityRenderer implements BlockEntityRenderer<StompingBarrelBlockEntity>
{
    private static final ResourceLocation WHITE_SMASHED = FLHelpers.identifier("block/white_smashed_grapes");
    private static final ResourceLocation WHITE_UNSMASHED = FLHelpers.identifier("block/white_unsmashed_grapes");
    private static final ResourceLocation RED_SMASHED = FLHelpers.identifier("block/red_smashed_grapes");
    private static final ResourceLocation RED_UNSMASHED = FLHelpers.identifier("block/red_unsmashed_grapes");

    @Override
    public void render(StompingBarrelBlockEntity barrel, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        if (barrel.getLevel() == null)
            return;

        final ItemStack stack = barrel.readStack();
        if (stack.isEmpty())
            return;

        poseStack.pushPose();

        final ResourceLocation texture;
        if (Helpers.isItem(stack, FLItems.FOODS.get(FLFood.SMASHED_RED_GRAPES).get()))
        {
            texture = RED_SMASHED;
        }
        else if (Helpers.isItem(stack, FLItems.FOODS.get(FLFood.SMASHED_WHITE_GRAPES).get()))
        {
            texture = WHITE_SMASHED;
        }
        else if (Helpers.isItem(stack, FLItems.FRUITS.get(FLFruit.RED_GRAPES).get()))
        {
            texture = RED_UNSMASHED;
        }
        else
        {
            texture = WHITE_UNSMASHED;
        }

        final boolean smashed = texture == WHITE_SMASHED || texture == RED_SMASHED;
        float y = (1f / 16) + (7f / 16f * stack.getCount() / StompingBarrelBlockEntity.MAX_GRAPES);
        if (smashed)
        {
            y *= 0.5f;
        }
        else
        {
            y *= Mth.lerp(barrel.getStomps() / 16f, 1f, 0.5f);
        }
        RenderHelpers.renderTexturedFace(poseStack, buffer, 0xffffff, 2f / 16, 2f / 16, 14f / 16, 14f / 16, y, overlay, light, texture, false);

        poseStack.popPose();
    }
}
