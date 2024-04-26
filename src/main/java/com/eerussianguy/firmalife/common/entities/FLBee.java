package com.eerussianguy.firmalife.common.entities;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class FLBee extends Bee
{

    @Nullable
    private BlockPos spawnPos;
    public FLBee(EntityType<? extends Bee> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    protected void registerGoals()
    {
        super.registerGoals();
        EntityHelpers.removeGoalOfPriority(this.goalSelector, 1);
        EntityHelpers.removeGoalOfPriority(this.goalSelector, 5);
        EntityHelpers.removeGoalOfPriority(this.goalSelector, 8);
        this.goalSelector.addGoal(8, new FLBeeWanderGoal());
    }

    public void addAdditionalSaveData(CompoundTag pCompound)
    {
        super.addAdditionalSaveData(pCompound);
        assert this.getSpawnPos() != null;
        pCompound.put("spawnPos", NbtUtils.writeBlockPos(this.getSpawnPos()));
    }

    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        this.spawnPos = null;
        this.spawnPos = NbtUtils.readBlockPos(pCompound.getCompound("spawnPos"));
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

    private boolean closerThan(BlockPos pPos, int pDistance)
    {
        return pPos.closerThan(this.blockPosition(), (double)pDistance);
    }

    public class FLBeeWanderGoal extends Goal
    {
        private static final int WANDER_THRESHOLD = 6;

        FLBeeWanderGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse()
        {
            return FLBee.this.navigation.isDone() && FLBee.this.random.nextInt(10) == 0;
        }
        public boolean canContinueToUse()
        {
            return FLBee.this.navigation.isInProgress();
        }

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
            } else
            {
                vec3 = FLBee.this.getViewVector(0.0F);
            }

            int i = 8;
            Vec3 vec32 = HoverRandomPos.getPos(FLBee.this, 8, 7, vec3.x, vec3.z, ((float)Math.PI / 2F), 3, 1);
            return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(FLBee.this, 8, 4, -2, vec3.x, vec3.z, (double)((float)Math.PI / 2F));
        }
    }

    @Override
    public void tick()
    {
        if (tickCount == 10 && spawnPos == null)
        {
            spawnPos = this.blockPosition();
        }
        super.tick();
        // goodnight bees
        if (level().isNight() || level().isRaining())
        {
            this.discard();
        }
    }

}
