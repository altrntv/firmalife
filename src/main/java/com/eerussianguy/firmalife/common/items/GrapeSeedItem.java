package com.eerussianguy.firmalife.common.items;

import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.plant.GrapeStringBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class GrapeSeedItem extends Item
{
    private final Supplier<? extends Block> crop;

    public GrapeSeedItem(Properties properties, Supplier<? extends Block> crop)
    {
        super(properties);
        this.crop = crop;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState stateAt = level.getBlockState(pos);
        final Player player = context.getPlayer();
        if (placePlant(context, level, pos, stateAt, player))
            return InteractionResult.SUCCESS;
        final BlockPos relPos = pos.relative(context.getClickedFace());
        final BlockState relState = level.getBlockState(relPos);
        if (placePlant(context, level, relPos, relState, player))
            return InteractionResult.SUCCESS;
        return InteractionResult.PASS;
    }

    private boolean placePlant(UseOnContext context, Level level, BlockPos pos, BlockState stateAt, @Nullable Player player)
    {
        if (Helpers.isBlock(stateAt, FLBlocks.GRAPE_STRING.get()))
        {
            final BlockState placeState = crop.get().defaultBlockState();
            if (Helpers.isBlock(level.getBlockState(pos.below()), TFCTags.Blocks.BUSH_PLANTABLE_ON))
            {
                level.setBlockAndUpdate(pos, Helpers.copyProperty(placeState, stateAt, GrapeStringBlock.AXIS));
                if (player == null || !player.isCreative())
                    context.getItemInHand().shrink(1);
                return true;
            }
        }
        return false;
    }
}
