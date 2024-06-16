package com.eerussianguy.firmalife.common.blocks.plant;

import java.util.List;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blockentities.GrapePlantBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.ISlowEntities;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.Helpers;

public class GrapeTrellisPostWithPlantBlock extends GrapeTrellisPostBlock implements ISlowEntities
{
    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.LIFECYCLE;
    public static final VoxelShape SHAPE_WITH_PLANT = Shapes.or(SHAPE, box(0, 4, 0, 16, 16, 16));
    private final Supplier<? extends Item> grapeItem;

    public GrapeTrellisPostWithPlantBlock(ExtendedProperties properties, Supplier<? extends Item> grapeItem)
    {
        super(properties);
        this.grapeItem = grapeItem;
        registerDefaultState(getStateDefinition().any().setValue(AXIS, Direction.Axis.X).setValue(STRING_PLUS, false).setValue(STRING_MINUS, false).setValue(LIFECYCLE, Lifecycle.HEALTHY));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final InteractionResult ropeUse = super.use(state, level, pos, player, hand, result);
        if (!ropeUse.consumesAction() && state.getValue(LIFECYCLE) == Lifecycle.FRUITING)
        {
            level.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.PLAYERS, 1.0F, level.getRandom().nextFloat() + 0.7F + 0.3F);
            final ItemStack stack = grapeItem.get().getDefaultInstance();
            final GrapePlantBlockEntity grape = findPlant(level, pos, state);
            if (grape != null)
            {
                final List<FoodTrait> traits = grape.scanAndReport();
                for (FoodTrait trait : traits)
                {
                    FoodCapability.applyTrait(stack, trait);
                }
            }
            ItemHandlerHelper.giveItemToPlayer(player, stack);
            level.setBlockAndUpdate(pos, state.setValue(LIFECYCLE, Lifecycle.HEALTHY));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    private GrapePlantBlockEntity findPlant(Level level, BlockPos pos, BlockState state)
    {
        final Direction dir = state.getValue(AXIS) == Direction.Axis.X ? Direction.NORTH : Direction.WEST;
        if (level.getBlockEntity(pos.below().relative(dir)) instanceof GrapePlantBlockEntity grape)
            return grape;
        if (level.getBlockEntity(pos.below().relative(dir.getOpposite())) instanceof GrapePlantBlockEntity grape)
            return grape;
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE_WITH_PLANT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        if (state.getValue(AXIS).test(facing))
        {
            if (!(facingState.getBlock() instanceof GrapeStringWithPlantBlock))
            {
                return level.getBlockState(pos.relative(facing.getOpposite())).getBlock() instanceof GrapeStringWithPlantBlock ? state : Helpers.copyProperties(FLBlocks.GRAPE_TRELLIS_POST.get().defaultBlockState(), state);
            }
        }
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return Shapes.empty();
    }

    @Override
    public float slowEntityFactor(BlockState blockState)
    {
        return 0.5f;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LIFECYCLE));
    }
}
