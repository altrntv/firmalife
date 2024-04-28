package com.eerussianguy.firmalife.common.recipes.data;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifiers;

public class FLItemStackModifiers
{
    public static void init()
    {
        register("add_pie_pan", AddPiePanModifier.INSTANCE);
        register("copy_dynamic_food", CopyDynamicFoodModifier.INSTANCE);
        register("copy_bowl", CopyBowlModifier.INSTANCE);
        register("empty_pan", EmptyPanModifier.INSTANCE);
        register("label_wine", LabelWineModifier.INSTANCE);
    }

    private static void register(String name, ItemStackModifier.Serializer<?> serializer)
    {
        ItemStackModifiers.register(FLHelpers.identifier(name), serializer);
    }
}
