package com.eerussianguy.firmalife.common.capabilities.wine;

import net.dries007.tfc.util.climate.KoppenClimateClassification;

public enum WineType
{
    RED,
    WHITE,
    ROSE,
    SPARKLING,
    DESSERT
    ;

    public static final WineType[] VALUES = values();
    public static final KoppenClimateClassification[] KOPPEN_VALUES = KoppenClimateClassification.values();
}
