package com.hero.witchery_rewitched.util.rituals;

import com.hero.witchery_rewitched.block.glyph.GlyphBlock;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmokingRecipe;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class RiteOfBroiling extends AbstractRitual{
    private final int DURATION_SECONDS = 7;
    private long startTime;

    public RiteOfBroiling(BlockPos pos, World world, UUID caster, boolean stone) {
        super(pos, world, caster, null,
                Collections.singletonList(new Pair<>(1, (GlyphBlock) ModBlocks.INFERNAL_GLYPH.get())),
                Arrays.asList(new EntityType<?>[]{EntityType.ITEM}),
                stone ? 0 : 1000,
                0,
                stone
        );
    }

    public RiteOfBroiling(){
        this(null, null, null, false);
    }

    @Override
    public void tick() {
        super.tick();
        if((world.getDayTime() - startTime)/20f >= DURATION_SECONDS){
            active = false;
        }
        if(active){
            if(world.getDayTime() % 5 == 0) {
                for (float i = 0; i < Math.PI * 2; i += Math.PI / 4) {
                    ((ServerWorld) world).sendParticles(ParticleTypes.FLAME, pos.getX() + .5 + 2.5 * Math.cos(i + world.getDayTime() % 32 * Math.PI/18), pos.getY() + .5, pos.getZ() + .5 + 2.5 * Math.sin(i + world.getDayTime() % 32 * Math.PI/18), 1, 0, 0, 0, 0);
                }
            }

            List<ItemEntity> itemEntities = world.getEntities(EntityType.ITEM, VoxelShapes.box(pos.getX()-3, pos.getY(), pos.getZ()-3, pos.getX()+3, pos.getY()+2, pos.getZ()+3).bounds(),
                    (itemEntity) -> itemEntity.verticalCollision);
            for(ItemEntity item: itemEntities){
                SmokingRecipe recipe = world.getRecipeManager().getRecipeFor(IRecipeType.SMOKING, new IInventory() {
                    @Override
                    public int getContainerSize() {
                        return 1;
                    }

                    @Override
                    public boolean isEmpty() {
                        return false;
                    }

                    @Override
                    public ItemStack getItem(int index) {
                        return item.getItem();
                    }

                    @Override
                    public ItemStack removeItem(int index, int count) {
                        return ItemStack.EMPTY;
                    }

                    @Override
                    public ItemStack removeItemNoUpdate(int index) {
                        return ItemStack.EMPTY;
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

                    @Override
                    public void clearContent() {

                    }
                }, world).orElse(null);
                if(recipe != null){
                    int num = world.getRandom().nextInt(item.getItem().getCount()/4 + 1);
                    if(item.getItem().getCount() == 1)
                        if(world.getRandom().nextInt(2) == 1)
                            num = 1;
                    InventoryHelper.dropItemStack(world, item.getX(), item.getY(), item.getZ(), new ItemStack(recipe.getResultItem().getItem(), item.getItem().getCount() - num));
                    InventoryHelper.dropItemStack(world, item.getX(), item.getY(), item.getZ(), new ItemStack(Items.CHARCOAL, num));
                    item.setItem(ItemStack.EMPTY);
                    world.playSound(null, pos, SoundEvents.GENERIC_BURN, SoundCategory.BLOCKS, 1, 1);
                    ((ServerWorld)world).sendParticles(ParticleTypes.LARGE_SMOKE, item.getX(), item.getY() + .3, item.getZ(), 5, 0, 0, 0, .02);
                }
            }
        }
    }

    @Override
    public  AbstractRitual createRite(BlockPos pos, World world, UUID caster, boolean stone){
        return new RiteOfBroiling(pos, world, caster, stone);
    }

    @Override
    public void start(ArrayList<ItemStack> items) {
        super.start(items);
        startTime = world.getDayTime();
    }

    @Override
    public String getName() {
        return "Rite of Broiling";
    }

    @Override
    public String getDescription() {
        return "Cooks any food placed in the circle while the rite is active for 10 seconds.";
    }

    @Override
    public String getRequirements() {
        return "None.";
    }
}
