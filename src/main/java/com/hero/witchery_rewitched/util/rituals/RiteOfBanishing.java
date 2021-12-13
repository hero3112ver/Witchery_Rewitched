package com.hero.witchery_rewitched.util.rituals;

import com.hero.witchery_rewitched.block.glyph.GlyphBlock;
import com.hero.witchery_rewitched.entity.demon.DemonEntity;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModItems;
import com.hero.witchery_rewitched.util.util.WorldUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Abs;

import javax.annotation.Nullable;
import java.util.*;

public class RiteOfBanishing extends AbstractRitual {
    public RiteOfBanishing(BlockPos pos, World world, UUID caster, boolean stone) {
        super(pos, world, caster, null,
                Collections.singletonList(new Pair<>(1, (GlyphBlock) ModBlocks.RITUAL_GLYPH.get())),
                new ArrayList<>(),
                Arrays.asList(Items.BLAZE_POWDER, ModItems.WAYSTONE.get()),
                Arrays.asList(Items.BLAZE_POWDER, ModItems.WAYSTONE.get(), ModItems.CHARGED_ATTUNED_STONE.get()),
                stone ? 0 : 2000,
                0,
                stone);
    }

    public RiteOfBanishing(){
        this(null, null, null, false);
    }

    @Override
    public AbstractRitual createRite(BlockPos pos, World world, UUID caster, boolean stone) {
        return new RiteOfBanishing(pos, world, caster, stone);
    }

    @Override
    public String checkStartConditions(List<ItemStack> items) {
        return super.checkStartConditions(items);
    }

    @Override
    public void start(ArrayList<ItemStack> items) {
        super.start(items);

        VoxelShape shape = VoxelShapes.box(this.pos.getX()-6, this.pos.getY()-.5, this.pos.getZ() - 6,this.pos.getX()+6, this.pos.getY() + 3, this.pos.getZ() +6);
        List<Entity> entities = world.getEntities((DemonEntity) null, shape.bounds(), (entity) -> Double.compare(Math.sqrt(WorldUtils.distanceSq(entity.blockPosition(), this.pos.offset(.5,0,.5))), 3.5) < 1 );
        for(Entity demon: entities){
            demon.remove();
        }
        active = false;
    }

    @Override
    public String getName() {
        return "Rite of banishing";
    }

    @Override
    public String getDescription() {
        return "Banishes all demons within the circle back into the pit.";
    }

    @Override
    public String getRequirements() {
        return "None.";
    }
}
