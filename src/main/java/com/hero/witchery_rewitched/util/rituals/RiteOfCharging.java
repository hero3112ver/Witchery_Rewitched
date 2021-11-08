package com.hero.witchery_rewitched.util.rituals;

import com.hero.witchery_rewitched.block.glyph.GlyphBlock;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class RiteOfCharging extends AbstractRitual{

    public RiteOfCharging(BlockPos pos, World world, UUID caster, boolean stone) {
        super(pos, world, caster, null,
                Arrays.asList(new Pair<>(1, (GlyphBlock) ModBlocks.RITUAL_GLYPH.get()),
                              new Pair<>(2, (GlyphBlock) ModBlocks.RITUAL_GLYPH.get())),
                new ArrayList<>(),
                2000,
                0,
                stone
        );
    }

    public RiteOfCharging(){
        this( null, null, null, false);
    }

    @Override
    public  AbstractRitual createRite(BlockPos pos, World world, UUID caster, boolean stone){
        return new RiteOfCharging(pos, world, caster, stone);
    }

    @Override
    public void start(ArrayList<ItemStack> items) {
        super.start(items);
        InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.CHARGED_ATTUNED_STONE.get()));
        active = false;
    }

    @Override
    public String getName() {
        return "Rite of Charging";
    }

    @Override
    public String getDescription() {
        return "Charges the attuned stone used in the rite.";
    }

    @Override
    public String getRequirements() {
        return "None.";
    }
}
