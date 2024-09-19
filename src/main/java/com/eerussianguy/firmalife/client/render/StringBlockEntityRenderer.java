package com.eerussianguy.firmalife.client.render;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.blockentities.StringBlockEntity;
import com.eerussianguy.firmalife.common.blocks.StringBlock;
import com.mojang.blaze3d.vertex.PoseStack;

public class StringBlockEntityRenderer implements BlockEntityRenderer<StringBlockEntity>
{
    @Override
    public void render(StringBlockEntity string, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        Level level = string.getLevel();
        if (level == null) return;
        BlockState state = level.getBlockState(string.getBlockPos());
        if (!(state.getBlock() instanceof StringBlock)) return;
        final ItemStack item = string.readStack();
        final int total = item.getCount();
        if (total <= 0) return;

        poseStack.pushPose();
        if (state.getValue(StringBlock.AXIS) == Direction.Axis.Z)
        {
            poseStack.translate(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(90f));
            poseStack.translate(-0.5f, -0.5f, -0.5f);
        }
        for (float i = 0; i < total; i++)
        {
            final float dx = 0.2f + (0.6f / total * i);
            poseStack.pushPose();

            poseStack.translate(dx, 0.38f, (i % 3 == 0 ? 0.52f : i % 3 == 1 ? 0.5f : 0.48f) + dx / 12 - .02);
            poseStack.scale(0.4f, 0.4f, 0.4f);
            Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffers, string.getLevel(), 0);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public int getViewDistance()
    {
        return 16;
    }
}
