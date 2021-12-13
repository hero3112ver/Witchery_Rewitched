package com.hero.witchery_rewitched.util.rituals;

import com.hero.witchery_rewitched.util.util.DimPos;
import com.hero.witchery_rewitched.util.util.TeleportUtils;
import com.hero.witchery_rewitched.util.util.WorldUtils;
import com.hero.witchery_rewitched.block.glyph.GlyphBlock;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModItems;
import com.hero.witchery_rewitched.item.BoundWaystone;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RiteOfTransposition extends AbstractRitual {
    public RiteOfTransposition(BlockPos pos, World world, UUID caster) {
        super(
                pos,
                world,
                caster,
                null,
                Collections.singletonList(new Pair<>(1, (GlyphBlock) ModBlocks.OTHERWHERE_GLYPH.get())),
                new ArrayList<>(),
                Collections.singletonList(ModItems.BOUND_WAYSTONE.get()),
                new ArrayList<>(),
                0,
                0,
                false
        );
    }
    public RiteOfTransposition(){
        this( null, null, null);
    }

    @Override
    public AbstractRitual createRite(BlockPos pos, World world, UUID caster, boolean stone){
        return new RiteOfTransposition(pos, world, caster);
    }

    @Override
    public String checkStartConditions(List<ItemStack> items) {
        for(ItemStack item : items) {
            if (item.getItem() == ModItems.BOUND_WAYSTONE.get()) {
                if(BoundWaystone.getDimension(item) == null)
                    return "ritual.witchery_rewitched.invalid_waystone";
            }
        }
        return super.checkStartConditions(items);
    }

    public void start(ArrayList<ItemStack> items) {
        super.start(items);
        for(ItemStack item : items){
            if(item.getItem() == ModItems.BOUND_WAYSTONE.get()){
                BlockPos wayPos = BoundWaystone.getPos(item);
                if(world.getServer() != null && wayPos != null) {
                    DimPos pos1 = DimPos.of(wayPos, RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BoundWaystone.getDimension(item))));
                    if(caster != null) {
                        VoxelShape shape = VoxelShapes.box(this.pos.getX()-6, this.pos.getY()-.5, this.pos.getZ() - 6,this.pos.getX()+6, this.pos.getY() + 3, this.pos.getZ() +6);
                        List<Entity> entites = world.getEntities((Entity) null, shape.bounds(), (entity) -> Double.compare(Math.sqrt(WorldUtils.distanceSq(entity.blockPosition(), this.pos.offset(.5,0,.5))), 3.5) < 1 );
                        for(Entity entity : entites){
                            ((ServerWorld)world).sendParticles(ParticleTypes.PORTAL,  entity.blockPosition().getX(), entity.getEyeHeight()/2 + entity.getY(), entity.blockPosition().getZ(), 500,0f,0f, 0f, 3);
                            world.playSound(null, wayPos.getX(), wayPos.getY(), wayPos.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1,1);
                            if(entity instanceof PlayerEntity)
                                TeleportUtils.teleport((PlayerEntity) entity, pos1, null);
                            else
                                TeleportUtils.teleportEntity(entity, pos1, null);
                            ((ServerWorld)world).sendParticles(ParticleTypes.REVERSE_PORTAL,  wayPos.getX(), entity.getEyeHeight()/2 + wayPos.getY(), wayPos.getZ(), 500,0f,0f, 0f, .5);
                            ((ServerWorld)world).playSound(null, pos1.getX(), pos1.getY(), pos1.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, .5f,1);
                        }
                    }
                }
            }
        }
        active = false;
    }

    @Override
    public String getName() {
        return "Rite of Transposition";
    }

    @Override
    public String getDescription() {
        return "Transports the user to the specified location bound to the waystone.";
    }

    @Override
    public String getRequirements() {
        return "None";
    }
}
