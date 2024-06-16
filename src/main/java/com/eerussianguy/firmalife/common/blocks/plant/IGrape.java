package com.eerussianguy.firmalife.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;

public interface IGrape
{
    default void advance(Level level, BlockPos pos, BlockState state) {}

    default void advanceLifecycle(Level level, BlockPos pos, BlockState state, Lifecycle lifecycle)
    {
        updateLifecycle(level, pos, state, lifecycle);
    }

    default void updateLifecycle(Level level, BlockPos pos, BlockState state, Lifecycle lifecycle)
    {
        if (state.hasProperty(TFCBlockStateProperties.LIFECYCLE) && state.getValue(TFCBlockStateProperties.LIFECYCLE).advanceTowards(lifecycle) != state.getValue(TFCBlockStateProperties.LIFECYCLE))
        {
            level.setBlockAndUpdate(pos, state.setValue(TFCBlockStateProperties.LIFECYCLE, lifecycle));
        }
    }
}
