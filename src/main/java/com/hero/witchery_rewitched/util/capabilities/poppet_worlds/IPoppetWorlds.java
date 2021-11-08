package com.hero.witchery_rewitched.util.capabilities.poppet_worlds;

import net.minecraft.world.World;

import java.util.List;

public interface IPoppetWorlds {
    List<String> getShelves();
    void addShelfIfNotPresent(World world);
    void removeShelfIfPresent(World world);
    void setShelves(List<String> shelves);
}
