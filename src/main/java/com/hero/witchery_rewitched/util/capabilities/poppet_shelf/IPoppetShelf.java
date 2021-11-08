package com.hero.witchery_rewitched.util.capabilities.poppet_shelf;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IPoppetShelf {
    List<Pair<GameProfile, BlockPos>> getShelves();
    void addShelfIfNotPresent(PlayerEntity player, BlockPos pos);
    void removeShelfIfPresent(BlockPos pos);
}
