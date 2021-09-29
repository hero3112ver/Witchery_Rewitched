package com.hero.witchery_rewitched.block.glyph;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.api.rituals.AbstractRitual;
import com.hero.witchery_rewitched.block.plants.grassper.GrassperTileEntity;
import com.hero.witchery_rewitched.config.WitcheryRewitchedConfig;
import com.hero.witchery_rewitched.crafting.recipe.ModRecipes;
import com.hero.witchery_rewitched.crafting.recipe.RitualRecipe;
import com.hero.witchery_rewitched.init.ModItems;
import com.hero.witchery_rewitched.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class GoldGlyphTileEntity extends TileEntity implements  ITickableTileEntity{
    public final static int GATHER_TIME = WitcheryRewitchedConfig.Server.debug.get() ? 1 : 20;
    private boolean gathering = false;
    private UUID caster = null;
    private final List<List<Item>> gatherQueue = new ArrayList<>();
    private final List<RitualRecipe> ritualQueue = new ArrayList<>();
    private final List<AbstractRitual> activeRituals = new ArrayList<>();


    public ArrayList<ItemStack> items = new ArrayList<>();

    public GoldGlyphTileEntity(){
        super(ModTileEntities.GOLD_GLYPH.get());
    }

    public void startGather(PlayerEntity player){
        gathering = true;
        caster = player != null ? player.getUUID() : null;
    }

    @Override
    public void tick() {
        if(level == null || level.isClientSide)
            return;

        /*
            The flow around here gets kinda funky so let me explain this up front
            The gatherQueue and ritualQueue are together at the hip, add to one add to the other
            they correspond to each other.
            You can't start a new ritual if you already have one active
            You can start multiple rituals at one time
         */

        // If you have an active ritual, you can't start initializing new rituals
        if(activeRituals.size() > 0 && gathering) gathering = false;

        // Stop initializing new rituals find the possible rituals with those item combinations
        if(gathering && gatherQueue.size() == 0) {
            if(!findRitual()) caster = null;
            gathering = false;
        }

        if(gatherQueue.size() > 0 && level.getGameTime() % GATHER_TIME == 0) {
            if(gather()){
                if(gatherQueue.get(0).size() == 0) {

                    gatherQueue.remove(0);
                    RitualRecipe rec = ritualQueue.remove(0);

                    // Create a new ritual of the current ritual in queue and get er goin
                    AbstractRitual ritual = rec.getRitual()
                            .createRite(worldPosition, level, caster, rec.getIngredientItems().contains(ModItems.CHARGED_ATTUNED_STONE.get()));
                    // If a ritual succeeds its start checks, gun it and start running it
                    if(ritual.checkStartConditions(items)) {
                        ritual.start(items);
                        activeRituals.add(ritual);
                    }
                    // If a ritual fails it's start checks dump it's items
                    else spitItems(rec, new ArrayList<>());

                    items.clear();
                    caster = null;
                }
            }
            // if you fail to gather the items for a ritual just dump it
            else{
                spitItems(ritualQueue.get(0), gatherQueue.remove(0));
                items.clear();
                caster = null;
            }
        };

        // Tick active rituals and remove dead rituals
        if(activeRituals.size() > 0){
            for(int i = 0; i < activeRituals.size(); i++){
                if(!activeRituals.get(i).active){
                    activeRituals.remove(i);
                }
            }
            for (AbstractRitual activeRitual : activeRituals) {
                activeRitual.tick();
            }
        }


    }

    private boolean findRitual(){
        AtomicBoolean flag = new AtomicBoolean(false);

        // This right here just grabs all the items in the radius in grasspers and dropped on the ground
        VoxelShape area = VoxelShapes.box(worldPosition.getX() - 3, worldPosition.getY() - 3, worldPosition.getZ() -3, worldPosition.getX() + 3, worldPosition.getY() + 3, worldPosition.getZ() +3);
        List<ItemStack> itemEntities = level.getEntities(EntityType.ITEM, area.bounds(), (item) ->  true).stream().map(ItemEntity::getItem).collect(Collectors.toList());
        List<ItemStack> itemStacks = new ArrayList<>();
        for(int x = -3;  x < 4; x++){
            for(int z = -3; z < 4; z++){
                BlockPos pos = getBlockPos().offset(x, 0, z);
                TileEntity te = level.getBlockEntity(pos);
                if( te instanceof GrassperTileEntity && ((GrassperTileEntity)te).getItem(0) != ItemStack.EMPTY){
                    itemStacks.add(((GrassperTileEntity)te).getItem(0));
                }
            }
        }
        List<ItemStack> items = new ArrayList<>();
        items.addAll(itemEntities);
        items.addAll(itemStacks);
        level.getRecipeManager().getAllRecipesFor(ModRecipes.Types.RITUAL).forEach(recipe -> {
            if (recipe.matches(new IInventory() {
                @Override
                public void clearContent() {

                }

                @Override
                public int getContainerSize() {
                    return items.size();
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public ItemStack getItem(int index) {
                    return items.get(index);
                }
                @Override
                public ItemStack removeItem(int index, int count) {
                    return null;
                }
                @Override
                public ItemStack removeItemNoUpdate(int index) {
                    return null;
                }
                @Override
                public void setItem(int index, ItemStack stack) {
                }
                @Override
                public void setChanged() {

                }
                @Override
                public boolean stillValid(PlayerEntity player) {
                    return false;
                }
            }, level)) {
                List<Item> gatherList = new ArrayList<>(recipe.getIngredientItems());
                gatherQueue.add(gatherList);
                ritualQueue.add(recipe);
                flag.set(true);
            }
        });
        return flag.get();
    }


    // Honestly kinda spaghetti but this function first checks for entities in range and yoinks them, then checks grasspers on ground level around it for items in queue
    // then it returns true if it takes an item, false if it does not take an item
    private boolean gather(){
        VoxelShape area = VoxelShapes.box(worldPosition.getX() - 3, worldPosition.getY() - 3, worldPosition.getZ() -3, worldPosition.getX() + 3, worldPosition.getY() + 3, worldPosition.getZ() +3);
        assert level != null;
        ItemEntity entity = level.getEntities(EntityType.ITEM, area.bounds(), (item) -> gatherQueue.size() > 0 && gatherQueue.get(0).contains(item.getItem().getItem())).stream().findFirst().orElse(null);
        if(entity != null ){
            items.add(entity.getItem().copy());
            gatherQueue.get(0).remove(entity.getItem().getItem());
            ((ServerWorld)level).playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, .5f, .3f);
            ((ServerWorld)level).sendParticles(ParticleTypes.POOF, entity.getX(), entity.getY() +.3, entity.getZ(), 5,0,0,0, .01);
            entity.getItem().setCount(entity.getItem().getCount()-1);
            return true;
        }
        for(int x = -3; x < 4; x++){
            for(int z = -3; z < 4; z++){
                BlockPos pos = getBlockPos().offset(x, 0, z);
                TileEntity te = level.getBlockEntity(pos);
                if( te instanceof GrassperTileEntity){
                    GrassperTileEntity gte = (GrassperTileEntity) te;
                    if(gatherQueue.size() > 0 && gatherQueue.get(0).contains(gte.getItem(0).getItem())){
                        gatherQueue.get(0).remove(gte.getItem(0).getItem());
                        gte.removeItem(0, 1);

                        ((ServerWorld)level).playSound(null, te.getBlockPos().getX(), te.getBlockPos().getY(), te.getBlockPos().getZ(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, .5f, .3f);
                        ((ServerWorld)level).sendParticles(ParticleTypes.POOF, te.getBlockPos().getX() + .5, te.getBlockPos().getY() +.6, te.getBlockPos().getZ()+ .5, 5,0,0,0, .01);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Drops items in world based on the current recipe sans the items in the given list
    private void spitItems(RitualRecipe recipe, List<Item> list){
        List<ItemStack> rec = recipe.getIngredientItems().stream().map(ItemStack::new).collect(Collectors.toList());
        for(int i = 0; i < rec.size(); i++){
            if(rec.get(i) != ItemStack.EMPTY && !list.contains(rec.get(i).getItem())){
                InventoryHelper.dropItemStack(level, getBlockPos().getX() + .5,getBlockPos().getY() + .5, getBlockPos().getZ() + .5, rec.get(i));
                rec.set(i, ItemStack.EMPTY);
                ((ServerWorld)level).playSound(null, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, .3f, .3f);
            }
        }
    }

    // TODO: I need to load and save active rituals eventually
    @Override
    public void load(@Nonnull BlockState state,@Nonnull  CompoundNBT nbt) {
        super.load(state, nbt);
    }

    @Override
    @Nonnull
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        super.save(compound);
        return compound;
    }


}
