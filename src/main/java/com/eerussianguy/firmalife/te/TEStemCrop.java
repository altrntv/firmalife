package com.eerussianguy.firmalife.te;

import net.dries007.tfc.objects.te.TEBase;
import net.dries007.tfc.objects.te.TECropBase;
import net.dries007.tfc.objects.te.TETickCounter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class TEStemCrop extends TECropBase
{
    //new Random is bad here, but it breaks if I pass in the block Random
    //and I still don't know why after a day of checking so that's what it is
    private EnumFacing fruitDirection = EnumFacing.Plane.HORIZONTAL.random(new Random());

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setInteger("fruitDirection",fruitDirection.getIndex());
        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        fruitDirection = EnumFacing.byIndex(tag.getInteger("fruitDirection"));
        super.readFromNBT(tag);
    }

    public EnumFacing getFruitDirection()
    {
        return fruitDirection;
    }
}
