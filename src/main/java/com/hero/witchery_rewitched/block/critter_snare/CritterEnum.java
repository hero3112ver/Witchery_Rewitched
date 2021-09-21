package com.hero.witchery_rewitched.block.critter_snare;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum CritterEnum implements IStringSerializable {
    NONE,
    SILVERFISH,
    SLIME,
    BAT;

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
    @Override
    public String toString() {
        return getSerializedName();
    }
}
