package com.eerussianguy.firmalife.common.blocks.plant;

import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.util.Helpers;

public class GrapeStringWithPlantBlock extends GrapeStringBlock
{
    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.LIFECYCLE;

    private final Supplier<? extends Block> postPlantBlock;

    public GrapeStringWithPlantBlock(ExtendedProperties properties, Supplier<? extends Block> postPlantBlock)
    {
        super(properties);
        this.postPlantBlock = postPlantBlock;
        registerDefaultState(getStateDefinition().any().setValue(LIFECYCLE, Lifecycle.HEALTHY));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        final BlockState superState = super.updateShape(state, facing, facingState, level, pos, facingPos);
        if (superState.isAir())
            return superState;
        if (facing == Direction.DOWN && !Helpers.isBlock(facingState, TFCTags.Blocks.BUSH_PLANTABLE_ON))
        {
            return Helpers.copyProperty(FLBlocks.GRAPE_STRING.get().defaultBlockState(), state, AXIS);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        level.setBlockAndUpdate(pos, state.cycle(LIFECYCLE));
        final Direction dir = state.getValue(AXIS) == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        convertToPlant(level, pos.relative(dir));
        convertToPlant(level, pos.relative(dir.getOpposite()));
        return InteractionResult.SUCCESS;
    }

    private void convertToPlant(Level level, BlockPos pos)
    {
        final BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof GrapeTrellisPostBlock && !(state.getBlock() instanceof GrapeTrellisPostWithPlantBlock))
        {
            level.setBlockAndUpdate(pos, Helpers.copyProperties(postPlantBlock.get().defaultBlockState(), state));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LIFECYCLE));
    }
}
