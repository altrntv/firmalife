package com.eerussianguy.firmalife.common.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class PressRecipe extends StompingRecipe
{
    public static final IndirectHashCollection<Item, PressRecipe> PRESS_CACHE = IndirectHashCollection.createForRecipe(PressRecipe::getValidItems, FLRecipeTypes.PRESS);

    public PressRecipe(ResourceLocation id, Ingredient ingredient, ItemStackProvider result, ResourceLocation inputTexture, ResourceLocation outputTexture, SoundEvent sound)
    {
        super(id, ingredient, result, inputTexture, outputTexture, sound);
    }

    @Nullable
    public static PressRecipe getPressRecipe(Level level, ItemStackInventory wrapper)
    {
        for (PressRecipe recipe : PRESS_CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, level))
            {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public RecipeType<?> getType()
    {
        return FLRecipeTypes.PRESS.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.PRESS.get();
    }
}
