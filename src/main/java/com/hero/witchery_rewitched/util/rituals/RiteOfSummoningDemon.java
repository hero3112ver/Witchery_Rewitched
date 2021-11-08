package com.hero.witchery_rewitched.util.rituals;

import com.hero.witchery_rewitched.block.glyph.GlyphBlock;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class RiteOfSummoningDemon extends AbstractRitual{
    public RiteOfSummoningDemon(BlockPos pos, World world, boolean stone) {
        super(
                pos,
                world,
                null,
                null,
                Collections.singletonList(new Pair<>(3, (GlyphBlock)ModBlocks.RITUAL_GLYPH.get())),
                stone ? Collections.singletonList(EntityType.VILLAGER) : new ArrayList<>(),
                3000,
                0,
                stone
        );
    }


    public RiteOfSummoningDemon(){this(null, null, false);}


    @Override
    public AbstractRitual createRite(BlockPos pos, World world, UUID caster, boolean stone) {
        return super.createRite(pos, world, caster, stone);
    }


    @Override
    public void start(ArrayList<ItemStack> items) {
        super.start(items);
        // TODO: Can't really spawn a demon without having a demon entity now can we?
        active = false;
    }
}
