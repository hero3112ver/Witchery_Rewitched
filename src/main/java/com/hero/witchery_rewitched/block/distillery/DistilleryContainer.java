package com.hero.witchery_rewitched.block.distillery;

import com.hero.witchery_rewitched.api.util.SlotUtils;
import com.hero.witchery_rewitched.init.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DistilleryContainer extends Container {
    private final IInventory inventory;
    private final IIntArray fields;

    public DistilleryContainer(ContainerType<?> type, int id, PlayerInventory inv, PacketBuffer data){
        this(type, id, inv, (DistilleryTileEntity)inv.player.level.getBlockEntity(data.readBlockPos()), new IntArray(data.readByte()));
    }

    public DistilleryContainer(@Nullable ContainerType<?> type, int id, PlayerInventory inv, IInventory blockInv, IIntArray fields){
        super(type, id);
        this.fields = fields;
        this.inventory = blockInv;
        checkContainerSize(this.inventory, DistilleryTileEntity.INVENTORY_SIZE);

        addSlot(new Slot(blockInv, 0, 28,13));
        addSlot(new Slot(blockInv, 1, 28,34));
        addSlot(new SlotUtils.PotSlot(blockInv, 2, 28,56));
        addSlot(new SlotUtils.OutputSlot(blockInv, 3, 105,20));
        addSlot(new SlotUtils.OutputSlot(blockInv, 4, 105,44));
        addSlot(new SlotUtils.OutputSlot(blockInv, 5, 131,20));
        addSlot(new SlotUtils.OutputSlot(blockInv, 6, 131, 44));
        List<Slot> list = new ArrayList<>();
        // Backpack
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                list.add(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        // Hotbar
        for (int x = 0; x < 9; ++x) {
            list.add(new Slot(inv, x, 8 + x * 18, 84 + 58));
        }
        list.forEach(this::addSlot);

        addDataSlots(this.fields);
    }

    public static Container createDistillery(int id, PlayerInventory inv, IInventory blockInv, IIntArray fields){
        return new DistilleryContainer(ModContainers.DISTILLERY.get(), id, inv, blockInv, fields);
    }

    public int getWorkProgress(){
        return fields.get(0);
    }

    public int getProgressArrowScale(){
        int progress = getWorkProgress();
        int workTime = (int)DistilleryTileEntity.WORKTIME;
        int blit = progress != 0 && workTime > 0 ? progress * 53 / workTime  : 0;
        return blit;
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }

    // TODO: this will definitely not work
    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if(slot != null && slot.hasItem()) {
            ItemStack stack1 = slot.getItem();
            stack = stack1.copy();
            final int size = inventory.getContainerSize();
            final int startPlayer = size;
            final int endPlayer = size + 27;
            final int startHotbar = size + 27;
            final int endHotbar = size + 36;

            if (index >= 1 && index < size) {
                // Remove from output slot?
                if (!this.moveItemStackTo(stack1, startPlayer, endHotbar, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && inventory.canPlaceItem(0, stack1)) {
                // Move from player to input slot?
                if (!moveItemStackTo(stack1, 0, 3, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= startPlayer && index < endPlayer) {
                // Move player items to hotbar.
                if (!moveItemStackTo(stack1, startHotbar, endHotbar, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= startHotbar && index < endHotbar) {
                // Move player items from hotbar.
                if (!moveItemStackTo(stack1, startPlayer, endPlayer, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack1, startPlayer, endHotbar, false)) {
                return ItemStack.EMPTY;
            }

            if (stack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack1.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack1);
        }

        return stack;
    }
}
