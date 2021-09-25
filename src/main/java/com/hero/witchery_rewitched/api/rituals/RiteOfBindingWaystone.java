package com.hero.witchery_rewitched.api.rituals;

import com.hero.witchery_rewitched.block.glyph.GlyphBlock;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModItems;
import com.hero.witchery_rewitched.item.BoundWaystone;
import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class RiteOfBindingWaystone extends AbstractRitual{
    public RiteOfBindingWaystone(BlockPos pos, World world, UUID caster, boolean stone) {
        super(pos, world, caster, null,
                Collections.singletonList(new Pair<>(1, (GlyphBlock) ModBlocks.RITUAL_GLYPH.get())),
                new ArrayList<>(),
                stone ? 0 : 500,
                0,
                stone
        );
    }

    public RiteOfBindingWaystone(){
        this(null,null, null, false);
    }

    @Override
    public AbstractRitual createRite(BlockPos pos, World world, UUID caster, boolean stone) {
        return new RiteOfBindingWaystone(pos, world, caster, stone);
    }

    @Override
    public void start(ArrayList<ItemStack> items) {
        super.start(items);
        InventoryHelper.dropItemStack(world, pos.getX(),pos.getY(), pos.getZ(), BoundWaystone.updateStackWithPos(new ItemStack(ModItems.BOUND_WAYSTONE.get()), pos, world.dimension().location().toString()));
        active = false;
    }

    @Override
    public String getName() {
        return "Rite of Binding";
    }

    @Override
    public String getDescription() {
        return "Binds a waystone the location in the world of the circle.";
    }

    @Override
    public String getRequirements() {
        return "None.";
    }
}
