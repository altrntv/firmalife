package com.eerussianguy.firmalife.common.capabilities.wine;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class WineCapability
{
    public static final Capability<IWine> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = FLHelpers.identifier("wine");
}
