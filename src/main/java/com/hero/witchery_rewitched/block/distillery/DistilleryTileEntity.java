package com.hero.witchery_rewitched.block.distillery;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.block.INamedContainerExtraData;
import com.hero.witchery_rewitched.block.altar.AltarTileEntity;
import com.hero.witchery_rewitched.config.WitcheryRewitchedConfig;
import com.hero.witchery_rewitched.crafting.recipe.DistilleryRecipe;
import com.hero.witchery_rewitched.crafting.recipe.ModRecipes;
import com.hero.witchery_rewitched.init.ModItems;
import com.hero.witchery_rewitched.init.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DistilleryTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerExtraData, ISidedInventory {
    public static final float WORKTIME = WitcheryRewitchedConfig.Server.debug.get() ?  2*20 : 150;
    private static final float UPDATE_TIME = WitcheryRewitchedConfig.Server.debug.get() ? 5 : 20;
    private BlockPos altar;
    private int progress = 0;

    private static final int[] INPUT_SLOTS = {0,1,2};
    private static final int[] OUTPUT_SLOTS = {3,4,5,6};
    public static final int INVENTORY_SIZE = 7;

    protected NonNullList<ItemStack> items;
    private final LazyOptional<? extends IItemHandler>[] handlers;

    private final IIntArray fields = new IIntArray() {
        @Override
        public int get(int index) {
            switch(index){
                case 0:
                    return progress;
                default:
                    return 0;
            }
        }
        @Override
        public void set(int index, int value) {
            switch(index){
                case 0:
                    progress = value;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public DistilleryTileEntity(){
        super(ModTileEntities.DISTILLERY.get());
        handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
        items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    }

    @Override
    public void encodeExtraData(PacketBuffer buffer) {
        buffer.writeBlockPos(worldPosition);
        buffer.writeByte(fields.getCount());
    }

    @Override
    public void tick() {

        if(this.level == null || this.level.isClientSide)
            return;
        Map<BlockPos, Block> tileEntityMap = null;
        if(altar != null && !(level.getBlockEntity(altar) instanceof AltarTileEntity))
            altar = null;

        if((altar == null) && level.getDayTime() % UPDATE_TIME == 0) {
            altar = AltarTileEntity.findFirstAltar(getBlockPos(), level);
        }

        if(altar != null){
            DistilleryRecipe  rec = getRecipe();
            if(progress == WORKTIME && rec != null) finishWork(rec);
            if(rec != null) {
                if(((AltarTileEntity)level.getBlockEntity(altar)).takePower(5))
                    progress++;
            }
            else stopWork();
        }
        else{
            stopWork();
        }
    }

    private void stopWork(){
        progress = 0;
    }

    private DistilleryRecipe getRecipe(){
        return level.getRecipeManager().getRecipeFor(ModRecipes.Types.DISTILLERY, this, level).orElse(null);
    }

    private void finishWork(DistilleryRecipe rec){
        setItem(0,new ItemStack(getItem(0).getItem(),getItem(0).getCount()-1));
        setItem(1,new ItemStack(getItem(1).getItem(),getItem(1).getCount()-1));
        setItem(2,new ItemStack(getItem(2).getItem(),getItem(2).getCount()- rec.fumeAmount));
        List<ItemStack> result = rec.getResults();
        for(int i =0; i < result.size();i++){
            tryInsert(result.get(i));
        }
        stopWork();
    }

    private void tryInsert(ItemStack stack){
        for(int slot : OUTPUT_SLOTS){
            if(getItem(slot).getItem() == stack.getItem() && stack.getCount() + getItem(slot).getCount() <= stack.getMaxStackSize()){
                setItem(slot, new ItemStack(getItem(slot).getItem(), stack.getCount() + getItem(slot).getCount()));
                return;
            }
            else if(getItem(slot).getItem() == Items.AIR) {
                setItem(slot, stack);
                return;
            }
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return OUTPUT_SLOTS;
        }
        return INPUT_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return Arrays.stream(INPUT_SLOTS).anyMatch(i -> (i == index && !(itemStackIn.getItem() == ModItems.COOKED_CLAY_POT.get())) || (index == 2 && itemStackIn.getItem() == ModItems.COOKED_CLAY_POT.get()));

    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return Arrays.stream(OUTPUT_SLOTS).anyMatch(i -> i == index);
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack stack : items)
            if(!stack.isEmpty())
                return true;
        return false;
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ItemStackHelper.removeItem(this.items, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStackHelper.takeItem(items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        items.set(index, stack);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.level != null &&
                this.level.getBlockEntity(this.worldPosition) == this &&
                player.distanceToSqr(this.worldPosition.getX() + .5, this.worldPosition.getY()+.5, this.worldPosition.getZ()) <= 64 ;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.witchery_rewitched.distillery");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory player, PlayerEntity playerEntity) {
        return DistilleryContainer.createDistillery(id, player, this, fields);
    }


    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        ItemStackHelper.loadAllItems(nbt, this.items);
        progress = nbt.getInt("progress");
        altar = NBTUtil.readBlockPos((CompoundNBT) nbt.get("pos"));
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        super.save(compound);
        ItemStackHelper.saveAllItems(compound, this.items);
        if(altar != null)
            compound.put("pos", NBTUtil.writeBlockPos(altar));
        compound.putInt("progress", progress);
        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        ItemStackHelper.loadAllItems(packet.getTag(), this.items);
        CompoundNBT tags = packet.getTag();
        CompoundNBT nbt= (CompoundNBT) tags.get("pos");
        if(nbt != null)
            altar = NBTUtil.readBlockPos(nbt);
        progress = nbt.getInt("progress");
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tags = super.getUpdateTag();
        tags.putInt("progress", progress);
        if(altar != null)
            tags.put("pos", NBTUtil.writeBlockPos(altar));
        return tags;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction d) {
        if (!this.remove && d != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (d == Direction.UP) {
                return this.handlers[0].cast();
            }
            else if (d == Direction.DOWN) {
                return this.handlers[1].cast();
            }
            else {
                return this.handlers[2].cast();
            }
        } else {
            return super.getCapability(cap, d);
        }
    }
}
