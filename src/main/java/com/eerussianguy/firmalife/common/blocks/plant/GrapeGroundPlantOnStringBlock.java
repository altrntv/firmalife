package com.eerussianguy.firmalife.common.blocks.plant;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;

public class GrapeGroundPlantOnStringBlock extends GrapeStringBlock
{
    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_2;

    private final Supplier<? extends Block> topBlock;

    public GrapeGroundPlantOnStringBlock(ExtendedProperties properties, Supplier<? extends Block> topBlock)
    {
        super(properties);
        this.topBlock = topBlock;
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (state.getValue(STAGE) == 2)
        {
            final BlockPos abovePos = pos.above();
            final BlockState above = level.getBlockState(abovePos);
            if (above.getBlock() instanceof GrapeStringBlock && above.getValue(AXIS) == state.getValue(AXIS))
            {
                final BlockState toPlace = Helpers.copyProperty(topBlock.get().defaultBlockState(), above, AXIS);
                level.setBlockAndUpdate(abovePos, toPlace);
            }
        }
        else
        {
            level.setBlockAndUpdate(pos, state.cycle(STAGE));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(STAGE));
    }
}
