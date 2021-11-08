package com.hero.witchery_rewitched.util.capabilities.altar;

import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IAltarLocations {
    List<BlockPos> getAltars();
    void addAltar(BlockPos pos);
    void deleteAltar(BlockPos pos);
}
