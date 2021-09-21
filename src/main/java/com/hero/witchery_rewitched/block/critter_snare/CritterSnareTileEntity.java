package com.hero.witchery_rewitched.block.critter_snare;

import com.hero.witchery_rewitched.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
public class CritterSnareTileEntity extends TileEntity {

    private CompoundNBT entityData = new CompoundNBT();
    public int CAPTURE_CD = 20 * 3;
    public long lastReleaseTime = -1;

    public CritterSnareTileEntity() {
        super(ModTileEntities.CRITTER_SNARE.get());
    }

    public void putEntity(Entity entity){
        entity.save(entityData);
    }

    public void releaseEntity(){

        Entity entity = null;
        String name = entityData.getString("id");
        if(name.contains("bat")){
            BatEntity bat = new BatEntity(EntityType.BAT, level);
            bat.load(entityData);
            entity = bat;
        }
        else if(name.contains("silver")){
            SilverfishEntity silverfishEntity = new SilverfishEntity(EntityType.SILVERFISH, level);
            silverfishEntity.load(entityData);
            entity = silverfishEntity;
        }
        else if(name.contains("slime")){
            SlimeEntity slimeEntity = new SlimeEntity(EntityType.SLIME, level);
            slimeEntity.load(entityData);
            entity = slimeEntity;
        }
        if(entity != null) {
            entity.setPos(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
            level.addFreshEntity(entity);
            entityData = new CompoundNBT();
            lastReleaseTime = level.getDayTime();
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
        pCompound.put("entityData", entityData);
        return super.save(pCompound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        entityData = (CompoundNBT) compoundNBT.get("entityData");
        super.load(state, compoundNBT);

    }
}
