package com.eerussianguy.firmalife.common.items;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public enum FLFood
{
    FROTHY_COCONUT,
    TOFU,
    SOY_MIXTURE,
    YAK_CURD,
    GOAT_CURD,
    MILK_CURD,
    OAT_SLICE,
    WHEAT_SLICE,
    BARLEY_SLICE,
    MAIZE_SLICE,
    RICE_SLICE,
    RYE_SLICE,
    OAT_FLATBREAD,
    WHEAT_FLATBREAD,
    BARLEY_FLATBREAD,
    MAIZE_FLATBREAD,
    RICE_FLATBREAD,
    RYE_FLATBREAD,
    OAT_DOUGH,
    WHEAT_DOUGH,
    BARLEY_DOUGH,
    MAIZE_DOUGH,
    RICE_DOUGH,
    RYE_DOUGH,
    TOAST,
    TOAST_WITH_JAM,
    TOAST_WITH_BUTTER,
    BACON,
    COOKED_BACON,
    GARLIC_BREAD,
    CHEDDAR,
    CHEVRE,
    RAJYA_METOK,
    GOUDA,
    FETA,
    SHOSHA,
    BUTTER,
    PIE_DOUGH,
    PUMPKIN_PIE_DOUGH,
    PIZZA_DOUGH,
    SHREDDED_CHEESE,
    PICKLED_EGG,
    COCOA_BEANS,
    ROASTED_COCOA_BEANS,
    COCOA_BUTTER,
    COCOA_POWDER,
    WHITE_CHOCOLATE_BLEND,
    DARK_CHOCOLATE_BLEND,
    MILK_CHOCOLATE_BLEND,
    WHITE_CHOCOLATE,
    DARK_CHOCOLATE,
    MILK_CHOCOLATE,
    CURED_MAIZE,
    NIXTAMAL,
    MASA_FLOUR,
    MASA,
    CORN_TORTILLA,
    TACO_SHELL,
    TOMATO_SAUCE,
    TOMATO_SAUCE_MIX,
    SALSA,
    VANILLA_ICE_CREAM,
    STRAWBERRY_ICE_CREAM,
    CHOCOLATE_ICE_CREAM,
    BANANA_SPLIT,
    COOKIE_DOUGH_ICE_CREAM,
    BURRITO,
    TACO,
    COOKED_PIZZA,
    COOKED_PIE,
    COOKIE_DOUGH,
    CHOCOLATE_CHIP_COOKIE_DOUGH,
    SUGAR_COOKIE,
    CHOCOLATE_CHIP_COOKIE,
    MAKI_ROLL,
    FUTO_MAKI_ROLL,
    HARDTACK,
    HARDTACK_DOUGH,
    RAW_LASAGNA,
    COOKED_LASAGNA,
    RAW_EGG_NOODLES,
    COOKED_PASTA,
    PASTA_WITH_TOMATO_SAUCE,
    RAW_RICE_NOODLES,
    COOKED_RICE_NOODLES,
    TORTILLA_CHIPS,
    NACHOS,
    SMASHED_RED_GRAPES,
    SMASHED_WHITE_GRAPES,
    SOYBEAN_PASTE,
    DEHYDRATED_SOYBEANS,
    ;

    private final boolean meat, fast;

    FLFood()
    {
        this(false, false);
    }

    FLFood(boolean meat, boolean fast)
    {
        this.meat = meat;
        this.fast = fast;
    }

    public FoodProperties getFoodProperties()
    {
        FoodProperties.Builder builder = new FoodProperties.Builder();
        if (meat) builder.meat();
        if (fast) builder.fast();
        return builder.nutrition(4).saturationMod(0.3f).build();
    }
}
