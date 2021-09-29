package com.hero.witchery_rewitched.block.plants.grassper;

import com.hero.witchery_rewitched.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GrassperTileEntity extends TileEntity implements IInventory {

    int INVENTORY_SIZE = 1;
    public NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private LazyOptional<IItemHandlerModifiable> itemHandler;

    public GrassperTileEntity() {
        super(ModTileEntities.GRASSPER.get());
    }

    public boolean interact(ItemStack handItem){
        if(getItem(0) == ItemStack.EMPTY && handItem != ItemStack.EMPTY){
            setItem(0, new ItemStack(handItem.copy().getItem(), 1));
            return true;
        }
        else if(getItem(0) != ItemStack.EMPTY){
            InventoryHelper.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), items.get(0));
            setItem(0, ItemStack.EMPTY);
        }
        return false;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.get(0) == ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItem(int pIndex) {
        return pIndex == 0 ? items.get(0) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        if(pIndex != 0)
            return ItemStack.EMPTY;
        ItemStack stack = items.get(0);
        setItem(0, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pIndex) {
        if(pIndex != 0)
            return ItemStack.EMPTY;
        ItemStack stack = items.get(0);
        setItem(0, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        setChanged();
        if(pIndex == 0)
            items.set(0, pStack);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sendUpdate();
    }

    @Override
    public boolean stillValid(PlayerEntity pPlayer) {
        return this.level != null &&
                this.level.getBlockEntity(this.worldPosition) == this &&
                pPlayer.distanceToSqr(this.worldPosition.getX() + .5, this.worldPosition.getY()+.5, this.worldPosition.getZ()) <= 64 ;
    }

    private void sendUpdate() {
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.load(state, nbt);
        ItemStackHelper.loadAllItems(nbt, this.items);
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        super.save(compound);
        ItemStackHelper.saveAllItems(compound, this.items);
        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        for(int i = 0; i < INVENTORY_SIZE; i++)
            this.items.set(i, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(packet.getTag(), this.items);
        CompoundNBT tags = packet.getTag();
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 0, getUpdateTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tags = super.getUpdateTag();
        ItemStackHelper.saveAllItems(tags, this.items);
        return tags;
    }


    @Override
    public void clearContent() {
        setItem(0, ItemStack.EMPTY);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.itemHandler == null)
                this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(this::newHandler);
            return this.itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private net.minecraftforge.items.IItemHandlerModifiable newHandler() {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof GrassperBlock)) {
            return new net.minecraftforge.items.wrapper.InvWrapper(this);
        }
        IInventory inv = this;
        return new net.minecraftforge.items.wrapper.InvWrapper(inv);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        if (itemHandler != null)
            itemHandler.invalidate();
    }
}
