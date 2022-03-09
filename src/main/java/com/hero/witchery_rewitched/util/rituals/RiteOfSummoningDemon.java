package com.hero.witchery_rewitched.util.rituals;

import com.hero.witchery_rewitched.block.glyph.GlyphBlock;
import com.hero.witchery_rewitched.entity.demon.DemonEntity;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class RiteOfSummoningDemon extends AbstractRitual{
    public RiteOfSummoningDemon(BlockPos pos, World world, UUID caster, boolean stone) {
        super(
                pos,
                world,
                caster,
                null,
                Collections.singletonList(new Pair<>(3, (GlyphBlock)ModBlocks.RITUAL_GLYPH.get())),
                !stone ? Collections.singletonList(EntityType.VILLAGER) : new ArrayList<>(),
                Arrays.asList(ModItems.REFINED_EVIL.get(), Items.BLAZE_POWDER, Items.ENDER_PEARL),
                Arrays.asList(ModItems.REFINED_EVIL.get(), Items.BLAZE_ROD, Items.ENDER_PEARL, ModItems.ATTUNED_STONE.get()),
                3000,
                0,
                stone
        );
    }


    public RiteOfSummoningDemon(){this(null, null, null, false);}


    @Override
    public AbstractRitual createRite(BlockPos pos, World world, UUID caster, boolean stone) {
        return new RiteOfSummoningDemon(pos, world, caster, stone);
    }


    @Override
    public void start(ArrayList<ItemStack> items) {
        super.start(items);
        DemonEntity demon = new DemonEntity(this.world);
        demon.setPos(this.pos.getX(), this.pos.getY(), this.pos.getZ());
        world.addFreshEntity(demon);
        active = false;
    }

    @Override
    public String getName() {
        return "Rite of Summoning Demon";
    }

    @Override
    public String getDescription() {
        return "Calls to the depths, to summon forth a demon. Only those that are ready to face the consequences should begin this ritual.";
    }

    @Override
    public String getRequirements() {
        return "A villager close to the center of the circle as sacrifice.";
    }
}
