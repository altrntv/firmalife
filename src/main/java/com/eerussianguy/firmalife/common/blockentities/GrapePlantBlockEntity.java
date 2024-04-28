package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.blocks.plant.GrapeGroundPlantOnStringBlock;
import com.eerussianguy.firmalife.common.blocks.plant.IGrape;
import com.eerussianguy.firmalife.common.util.FLClimateRanges;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;

public class GrapePlantBlockEntity extends TFCBlockEntity implements ICalendarTickable
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, GrapePlantBlockEntity plant)
    {
        plant.checkForCalendarUpdate();
    }

    public static final int UPDATE_INTERVAL = ICalendar.TICKS_IN_DAY;

    private long lastUpdateTick; // The last tick this crop was ticked via the block entity's tick() method. A delta of > 1 is used to detect time skips
    private long lastGrowthTick; // The last tick the crop block was ticked via ICropBlock#growthTick()
    private float growth = 0f;

    public GrapePlantBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.GRAPE_PLANT.get(), pos, state);
        lastGrowthTick = Calendars.SERVER.getTicks();
        lastUpdateTick = Integer.MIN_VALUE;
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        tryUpdate();
    }

    public void tryUpdate()
    {
        final long now = Calendars.SERVER.getTicks();
        //handle update interval
        if (now > (lastGrowthTick + UPDATE_INTERVAL))
        {
            while (lastGrowthTick < now)
            {
                updateTick();
                lastGrowthTick += UPDATE_INTERVAL;
            }
            markForSync();
        }
    }

    public void updateTick()
    {
        assert level != null;

        if (FLClimateRanges.GRAPES.get().checkTemperature(Climate.getTemperature(level, worldPosition), false) == ClimateRange.Result.VALID)
        {
            growth += 0.125f + (level.random.nextFloat() * 0.05f);
            if (growth >= 1f)
            {
                advanceAt(worldPosition);
                advanceAt(worldPosition.above());
                growth = 0f;
                markForSync();
            }
            lifecycleAt(worldPosition, Lifecycle.HEALTHY);
            lifecycleAt(worldPosition.above(), Lifecycle.HEALTHY);
        }
        else
        {

            lifecycleAt(worldPosition, Lifecycle.DORMANT);
            lifecycleAt(worldPosition.above(), Lifecycle.DORMANT);
        }
    }

    private void advanceAt(BlockPos pos)
    {
        assert level != null;
        final BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof IGrape grape)
        {
            grape.advance(level, pos, state);
        }
    }

    private void lifecycleAt(BlockPos pos, Lifecycle lifecycle)
    {
        assert level != null;
        final BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof IGrape grape)
        {
            grape.advanceLifecycle(level, pos, state, lifecycle);
        }
    }

    public BlockPos getBrainBlock()
    {
        assert level != null;
        BlockState state = level.getBlockState(worldPosition);
        final Direction dir = state.getValue(GrapeGroundPlantOnStringBlock.AXIS) == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        cursor.set(worldPosition);
        while (state.getBlock() instanceof GrapeGroundPlantOnStringBlock)
        {
            cursor.move(dir, 2);
            state = level.getBlockState(cursor);
        }
        cursor.move(dir, -2);
        return cursor.immutable();
    }

    public boolean isBrain()
    {
        return getBrainBlock().equals(worldPosition);
    }

    public float getGrowth()
    {
        return growth;
    }

    public long getLastGrowthTick()
    {
        return lastGrowthTick;
    }

    public void setLastGrowthTick(long lastGrowthTick)
    {
        this.lastGrowthTick = lastGrowthTick;
        markForSync();
    }

    @Override
    @Deprecated
    public long getLastCalendarUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    @Deprecated
    public void setLastCalendarUpdateTick(long tick)
    {
        lastUpdateTick = tick;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        lastUpdateTick = nbt.getLong("tick");
        lastGrowthTick = nbt.getLong("lastGrowthTick");
        growth = nbt.getFloat("growth");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putLong("tick", lastUpdateTick);
        nbt.putLong("lastGrowthTick", lastGrowthTick);
        nbt.putFloat("growth", growth);
        super.saveAdditional(nbt);
    }
}
