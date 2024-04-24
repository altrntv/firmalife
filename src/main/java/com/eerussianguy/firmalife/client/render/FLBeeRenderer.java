package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.entities.FLBee;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Slime;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FLBeeRenderer extends BeeRenderer {
    public FLBeeRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.5f;
    }

    @Override
    protected void scale(Bee entity, PoseStack poseStack, float ticks)
    {
        poseStack.scale(0.2f, 0.2f, 0.2f);
    }
}
