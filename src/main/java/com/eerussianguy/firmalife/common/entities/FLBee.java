package com.eerussianguy.firmalife.common.entities;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.util.calendar.Calendars;

public class FLBee extends Bee
{
    @Nullable
    private BlockPos spawnPos;

    private long daySpawned = -1;

    public FLBee(EntityType<? extends Bee> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        EntityHelpers.removeGoalOfPriority(this.goalSelector, 1);
        EntityHelpers.removeGoalOfPriority(this.goalSelector, 5);
        EntityHelpers.removeGoalOfPriority(this.goalSelector, 8);
        this.goalSelector.addGoal(8, new FLBeeWanderGoal());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound)
    {
        super.addAdditionalSaveData(pCompound);
        assert this.getSpawnPos() != null;
        pCompound.put("spawnPos", NbtUtils.writeBlockPos(this.getSpawnPos()));
        pCompound.putLong("daySpawned", this.daySpawned);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        this.spawnPos = null;
        setSpawnPos(NbtUtils.readBlockPos(pCompound.getCompound("spawnPos")));
        this.daySpawned = pCompound.getLong("daySpawned");
        super.readAdditionalSaveData(pCompound);
    }

    @Nullable
    public BlockPos getSpawnPos()
    {
        return spawnPos;
    }

    public void setSpawnPos(BlockPos pos)
    {
        spawnPos = pos;
    }

    private boolean closerThan(BlockPos pos, int distance)
    {
        return pos.closerThan(this.blockPosition(), distance);
    }

    public class FLBeeWanderGoal extends Goal
    {
        private static final int WANDER_THRESHOLD = 6;

        FLBeeWanderGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            return FLBee.this.navigation.isDone() && FLBee.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean canContinueToUse()
        {
            return FLBee.this.navigation.isInProgress();
        }

        @Override
        public void start()
        {
            Vec3 vec3 = this.findPos();
            if (vec3 != null)
            {
                FLBee.this.navigation.moveTo(FLBee.this.navigation.createPath(BlockPos.containing(vec3), 1), 1.0D);
            }

        }

        @Nullable
        private Vec3 findPos()
        {
            Vec3 vec3;
            assert spawnPos != null;
            if (!closerThan(spawnPos, WANDER_THRESHOLD))
            {
                Vec3 vec31 = Vec3.atCenterOf(FLBee.this.spawnPos);
                vec3 = vec31.subtract(FLBee.this.position()).normalize();
            }
            else
            {
                vec3 = FLBee.this.getViewVector(0.0F);
            }

            Vec3 vec32 = HoverRandomPos.getPos(FLBee.this, 8, 7, vec3.x, vec3.z, ((float) Math.PI / 2F), 3, 1);
            return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(FLBee.this, 8, 4, -2, vec3.x, vec3.z, (float) Math.PI / 2F);
        }
    }

    @Override
    public void tick()
    {
        if (tickCount <= 2)
        {
            if (spawnPos == null)
            {
                spawnPos = this.blockPosition();
            }
            if (daySpawned == -1)
            {
                daySpawned = Calendars.get(level()).getTotalCalendarDays();
            }
        }
        super.tick();
        // goodnight bees
        if (level().isNight() || level().isRaining())
        {
            this.discard();
        }
        if (tickCount % 400 == 0)
        {
            if (Calendars.get(this.level()).getTotalCalendarDays() > daySpawned && daySpawned >= 0)
            {
                this.discard();
            }
        }

    }

}
