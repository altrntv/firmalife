package com.eerussianguy.firmalife.mixin;

import com.eerussianguy.firmalife.common.blocks.CheeseWheelBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.common.blocks.devices.BarrelRackBlock;

@Mixin(BarrelRackBlock.class)
public class BarrelRackMixin
{
    @Inject(method = "canBeReplaced", at = @At("HEAD"), remap = false, cancellable = true)
    private void inject$canBeReplaced(BlockState state, BlockPlaceContext context, CallbackInfoReturnable<Boolean> cir)
    {
        if (context.getItemInHand().getItem() instanceof BlockItem bi && bi.getBlock() instanceof CheeseWheelBlock)
        {
            cir.setReturnValue(true);
        }
    }
}
