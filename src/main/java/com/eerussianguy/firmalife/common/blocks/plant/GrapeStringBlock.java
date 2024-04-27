package com.eerussianguy.firmalife.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.util.Helpers;

public class GrapeStringBlock extends DeviceBlock
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final VoxelShape SHAPE_Z = box(7, 10, 0, 9, 12, 16);
    public static final VoxelShape SHAPE_X = Helpers.rotateShape(Direction.WEST, 7, 10, 0, 9, 12, 16);

    public GrapeStringBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.NOOP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        final Direction.Axis axis = state.getValue(AXIS);
        if (facing.getAxis() == axis)
        {
            return facingState.getBlock() instanceof GrapeTrellisPostBlock ? state : Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final Direction dir = state.getValue(AXIS) == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        return level.getBlockState(pos.relative(dir)).getBlock() instanceof GrapeTrellisPostBlock && level.getBlockState(pos.relative(dir.getOpposite())).getBlock() instanceof GrapeTrellisPostBlock;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(AXIS) == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS));
    }
}
