package com.hero.witchery_rewitched.util.rituals;

import com.hero.witchery_rewitched.block.glyph.GlyphBlock;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ServerWorldInfo;

import java.util.*;

public class RiteOfTotalEclipse extends AbstractRitual{
    public RiteOfTotalEclipse(BlockPos pos, World world, UUID caster, boolean stone) {
        super(pos, world, caster, null,
                Arrays.asList(new Pair<>(1, (GlyphBlock) ModBlocks.RITUAL_GLYPH.get())),
                new ArrayList<>(),
                stone ? 0 : 3000,
                0,
                stone
        );
    }

    public RiteOfTotalEclipse(){
        this( null, null, null, false);
    }

    @Override
    public  AbstractRitual createRite(BlockPos pos, World world, UUID caster, boolean stone){
        return new RiteOfTotalEclipse(pos, world, caster, stone);
    }

    @Override
    public boolean checkStartConditions(List<ItemStack> items) {
        if(world.getDayTime() <= 23000 && world.getDayTime() >= 13000){
            return false;
        }
        return super.checkStartConditions(items);
    }

    @Override
    public void start(ArrayList<ItemStack> items) {
        super.start(items);
        ((ServerWorldInfo)((world).getLevelData())).setDayTime(18000);
        LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(world);
        lightningboltentity.moveTo(Vector3d.atBottomCenterOf(pos));
        lightningboltentity.setVisualOnly(true);
        ((ServerWorld)world).playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, .5f,1);
        this.active = false;
    }

    @Override
    public String getName() {
        return "Rite of Total Eclipse";
    }


    @Override
    public String getDescription() {
        return "This rite blocks out the sun, causing night to fall upon the world that it is casted in.";
    }

    @Override
    public String getRequirements() {
        return "This ritual is activated during the day time";
    }
}
