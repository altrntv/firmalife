package com.eerussianguy.firmalife.common.recipes.data;

import com.eerussianguy.firmalife.common.capabilities.wine.WineCapability;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;

public enum LabelWineModifier implements ItemStackModifier.SingleInstance<LabelWineModifier>
{
    INSTANCE;

    @Override
    public LabelWineModifier instance()
    {
        return INSTANCE;
    }

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        if (input.hasCustomHoverName())
        {
            stack.getCapability(WineCapability.CAPABILITY).ifPresent(wine -> wine.setLabelText(input.getHoverName().getString()));
        }
        return stack;
    }
}
