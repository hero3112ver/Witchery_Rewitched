package com.hero.witchery_rewitched.api.util;

import com.hero.witchery_rewitched.init.ModItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

public class SlotUtils {

    /**
     * Copied from SilentLib under MIT licensing all credit goes to SilentChaos512.
     * https://github.com/SilentChaos512/SilentLib
     */
    public static class FuelSlot extends Slot {

        public FuelSlot(IInventory blockInv, int index, int x, int y) {
            super(blockInv, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return ForgeHooks.getBurnTime(pStack) > 0;
        }
    }

    public static class PotSlot extends Slot {

        public PotSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
            super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return pStack.getItem() == ModItems.COOKED_CLAY_POT.get();
        }
    }

    public static class OutputSlot extends Slot {

        public OutputSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
            super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return false;
        }
    }
}
