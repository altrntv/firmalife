package com.eerussianguy.firmalife.common.blocks;

import java.util.ArrayList;
import java.util.List;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeAbility;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeCapability;
import com.eerussianguy.firmalife.common.capabilities.bee.IBee;
import com.eerussianguy.firmalife.common.misc.FLEffects;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;
import org.jetbrains.annotations.Nullable;

public class FLBeehiveBlock extends FourWayDeviceBlock implements HoeOverlayBlock
{
    public static boolean shouldAnger(Level level, BlockPos pos)
    {
        if (level.getGameTime() % 24000 > 12000)
        {
            return false;
        }
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        mutable.set(pos);
        for (int i = 0; i < 5; i++)
        {
            mutable.move(0, -1, 0);
            BlockState state = level.getBlockState(mutable);
            if (!state.isAir())
            {
                if (state.getBlock() instanceof FirepitBlock)
                {
                    if (state.getValue(FirepitBlock.LIT))
                    {
                        return false;
                    }
                }
                break; // we hit a solid block
            }

        }
        return level.getBlockEntity(pos, FLBlockEntities.BEEHIVE.get()).map(hive ->
            hive.getCapability(Capabilities.ITEM).map(inv -> {
                boolean anyBees = false;
                float calmChance = 0;
                for (int i = 0; i < FLBeehiveBlockEntity.FRAME_SLOTS; i++)
                {
                    final IBee bee = inv.getStackInSlot(i).getCapability(BeeCapability.CAPABILITY).resolve().orElse(null);
                    if (bee != null && bee.hasQueen())
                    {
                        anyBees = true;
                        calmChance += bee.getAbility(BeeAbility.CALMNESS);
                    }
                    calmChance /= 40f;
                }
                return anyBees && level.random.nextFloat() > calmChance;
        }).orElse(false)).orElse(false);
    }

    public static void attack(Player player)
    {
        player.addEffect(new MobEffectInstance(FLEffects.SWARM.get(), 100));
    }

    public static final BooleanProperty HONEY = FLStateProperties.HONEY;
    public static final BooleanProperty BEES = FLStateProperties.BEES;

    public FLBeehiveBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(HONEY, false).setValue(BEES, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (Helpers.isItem(held, TFCItems.EMPTY_JAR.get()) && !player.isShiftKeyDown())
        {
            level.getBlockEntity(pos, FLBlockEntities.BEEHIVE.get()).ifPresent(hive -> {
                if (hive.takeHoney(1) > 0)
                {
                    held.shrink(1);
                    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(FLItems.HONEY_JAR.get()));
                }
            });
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        else if (Helpers.isItem(held, FLItems.BEEHIVE_FRAME.get()))
        {
            final var res = FLHelpers.consumeInventory(level, pos, FLBlockEntities.BEEHIVE, (hive, inv) ->
                FLHelpers.insertOneAny(level, held, 0, FLBeehiveBlockEntity.FRAME_SLOTS - 1, inv, player)
            );
            if (res.consumesAction())
                return res;
        }
        else if (held.isEmpty() && player.isShiftKeyDown())
        {
            final var res = FLHelpers.consumeInventory(level, pos, FLBlockEntities.BEEHIVE, (hive, inv) ->
                FLHelpers.takeOneAny(level, 0, FLBeehiveBlockEntity.FRAME_SLOTS - 1, inv, player)
            );
            if (res.consumesAction())
            {
                if (shouldAnger(level, pos))
                    attack(player);
                return res;
            }
        }
        if (!player.isShiftKeyDown())
        {
            if (player instanceof ServerPlayer serverPlayer)
            {
                level.getBlockEntity(pos, FLBlockEntities.BEEHIVE.get()).ifPresent(nest -> Helpers.openScreen(serverPlayer, nest, pos));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        level.getBlockEntity(pos, FLBlockEntities.BEEHIVE.get()).ifPresent(FLBeehiveBlockEntity::tryPeriodicUpdate);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity entity, ItemStack tool)
    {
        if (shouldAnger(level, pos))
        {
            attack(player);
        }
        super.playerDestroy(level, player, pos, state, entity, tool);
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState blockState, List<Component> text, boolean debug)
    {
        level.getBlockEntity(pos, FLBlockEntities.BEEHIVE.get()).ifPresent(hive -> {
            if (hive.getHoney() > 0)
            {
                text.add(Component.translatable("firmalife.beehive.honey", String.valueOf(hive.getHoney())).withStyle(ChatFormatting.GOLD));
            }
            final float temp = Climate.getTemperature(level, pos);
            int ord = 0;
            final List<IBee> bees = new ArrayList<>();
            int noQueen = 0;
            for (IBee bee : hive.getCachedBees())
            {
                ord++;
                MutableComponent beeText = Component.translatable("firmalife.beehive.bee", ord);
                if (bee != null && bee.hasQueen())
                {
                    beeText.append(Component.translatable("firmalife.beehive.has_queen"));
                    final float minTemp = BeeAbility.getMinTemperature(bee.getAbility(BeeAbility.HARDINESS));
                    if (temp < minTemp)
                    {
                        beeText.append(Component.translatable("firmalife.beehive.bee_cold", minTemp, String.format("%.2f", temp)).withStyle(ChatFormatting.AQUA));
                    }
                    else
                    {
                        bees.add(bee);
                    }
                }
                else
                {
                    if (bee != null)
                        noQueen++;
                }
                text.add(beeText);
            }
            final int flowers = hive.getFlowers(bees, false);
            text.add(Component.translatable("firmalife.beehive.flowers", flowers));
            if (flowers < FLBeehiveBlockEntity.MIN_FLOWERS)
            {
                text.add(Component.translatable("firmalife.beehive.min_flowers"));
            }
            else
            {
                if (bees.size() < 4 && noQueen != 0)
                {
                    int breed = hive.getBreedTickChanceInverted(bees, flowers);
                    if (breed == 0) text.add(Component.translatable("firmalife.beehive.breed_chance_100"));
                    else text.add(Component.translatable("firmalife.beehive.breed_chance", breed));
                }
                if (!bees.isEmpty())
                {
                    int honey = hive.getHoneyTickChanceInverted(bees, flowers);
                    if (honey == 0) text.add(Component.translatable("firmalife.beehive.honey_chance_100"));
                    else text.add(Component.translatable("firmalife.beehive.honey_chance", honey));
                }

            }

        });

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(HONEY, BEES));
    }

}
