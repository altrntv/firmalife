package com.eerussianguy.firmalife.common.blocks.plant;

import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.util.Helpers;

public class GrapeStringWithPlantBlock extends GrapeStringBlock implements IGrape
{
    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.LIFECYCLE;
    public static final VoxelShape SHAPE = box(0, 4, 0, 16, 16, 16);

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
        if (facing == Direction.DOWN && !(facingState.getBlock() instanceof GrapeGroundPlantOnStringBlock))
        {
            return Helpers.copyProperty(FLBlocks.GRAPE_STRING.get().defaultBlockState(), state, AXIS);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return InteractionResult.PASS;
    }

    @Override
    public void advance(Level level, BlockPos pos, BlockState state)
    {
        final Direction dir = state.getValue(AXIS) == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        convertToPlant(level, pos.relative(dir));
        convertToPlant(level, pos.relative(dir.getOpposite()));
    }

    @Override
    public void advanceLifecycle(Level level, BlockPos pos, BlockState state, Lifecycle lifecycle)
    {
        updateLifecycle(level, pos, state, lifecycle);
        final Direction dir = state.getValue(AXIS) == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        updateLifecycle(level, pos.relative(dir), level.getBlockState(pos.relative(dir)), lifecycle);
        updateLifecycle(level, pos.relative(dir.getOpposite()), level.getBlockState(pos.relative(dir.getOpposite())), lifecycle);
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
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LIFECYCLE));
    }
}
