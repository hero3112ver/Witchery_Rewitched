package com.hero.witchery_rewitched.block.witch_oven;

import com.hero.witchery_rewitched.util.util.SlotUtils;
import com.hero.witchery_rewitched.init.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WitchOvenContainer extends Container {
    private final IInventory inventory;
    private final IIntArray fields;


    public WitchOvenContainer(ContainerType<?> type, int id, PlayerInventory inv, PacketBuffer data) {
        this(type, id, inv, (WitchOvenTileEntity)inv.player.level.getBlockEntity(data.readBlockPos()), new IntArray(4));
    }

    public WitchOvenContainer(@Nullable ContainerType<?> type, int id, PlayerInventory inv, IInventory blockInv, IIntArray fields) {
        super(type, id);
        this.fields = fields;
        this.inventory = blockInv;

        checkContainerSize(this.inventory, WitchOvenTileEntity.INVENTORY_SIZE);

        addSlot(new Slot(blockInv, 0, 48, 24)); // Item input
        addSlot(new SlotUtils.FuelSlot(blockInv, 1, 48, 60)); // Fuel input
        addSlot(new SlotUtils.PotSlot(blockInv, 2, 76, 60)); // clay jar input
        addSlot(new SlotUtils.OutputSlot(blockInv, 3, 108, 24));// Product output
        addSlot(new SlotUtils.OutputSlot(blockInv, 4, 108, 60));// Fume Output

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

    public static Container createWitchOven(int id, PlayerInventory inv, IInventory blockInv, IIntArray fields) {
        return new WitchOvenContainer(ModContainers.WITCH_OVEN.get(), id, inv, blockInv, fields);
    }




    public int getWorkProgress() {
        return fields.get(0);
    }

    public int getWorkTime() {
        return (int)WitchOvenTileEntity.WORKTIME;
    }

    public int getFuelTicks() {
        return fields.get(2);
    }

    public int getTotalFuelTicks() {
        return fields.get(3);
    }

    public int getProgressArrowScale() {
        int progress = getWorkProgress();
        int workTime = getWorkTime();
        return progress != 0 && workTime > 0 ? progress * 24 / workTime : 0;
    }

    public int getFuelScale() {
        int totalFuel = getTotalFuelTicks();
        int fuel = getFuelTicks();
        return fuel != 0 && totalFuel > 0 ? fuel * 14 / totalFuel : 0;
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return inventory.stillValid(playerIn);
    }


    //FIXME: This shit is broken
    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot != null && slot.hasItem()) {
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
