package com.hero.witchery_rewitched.util.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public final class PlayerUtils {
    /**
     * Copied from SilentLib under MIT licensing all credit goes to SilentChaos512.
     * https://github.com/SilentChaos512/SilentLib
     */

    private PlayerUtils() {}

    /**
     * Gives the player an item. If it can't be given directly, it spawns an EntityItem. Spawns 1
     * block above player's feet.
     *
     * @param player The player
     * @param stack  The item
     */
    public static void giveItem(PlayerEntity player, ItemStack stack) {
        ItemStack copy = stack.copy();
        if (!player.inventory.add(copy)) {
            ItemEntity entityItem = new ItemEntity(player.level, player.getX(), player.getY(0.5), player.getZ(), copy);
            entityItem.setNoPickUpDelay();
            entityItem.setOwner(player.getUUID());
            player.level.addFreshEntity(entityItem);
        }
    }

    /**
     * Removes the stack from the player's inventory, if it exists.
     *
     * @param player The player
     * @param stack  The item
     */
    public static void removeItem(PlayerEntity player, ItemStack stack) {
        List<NonNullList<ItemStack>> inventories = ImmutableList.of(player.inventory.items, player.inventory.offhand, player.inventory.armor);
        for (NonNullList<ItemStack> inv : inventories) {
            for (int i = 0; i < inv.size(); ++i) {
                if (stack == inv.get(i)) {
                    inv.set(i, ItemStack.EMPTY);
                    return;
                }
            }
        }
    }

    /**
     * Gets a tag compound from the player's persisted data NBT compound, or creates it if it does
     * not exist. This can be used to save additional data to a player.
     *
     * @param player         The player
     * @param subCompoundKey The key for the tag compound (ideally should contain mod ID)
     * @return The tag compound, creating it if it does not exist.
     */
    public static CompoundNBT getPersistedDataSubcompound(PlayerEntity player, String subCompoundKey) {
        CompoundNBT forgeData = player.getPersistentData();
        if (!forgeData.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
            forgeData.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
        }

        CompoundNBT persistedData = forgeData.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
        if (!persistedData.contains(subCompoundKey)) {
            persistedData.put(subCompoundKey, new CompoundNBT());
        }

        return persistedData.getCompound(subCompoundKey);
    }

    @Nonnull
    public static NonNullList<ItemStack> getNonEmptyStacks(PlayerEntity player) {
        return getNonEmptyStacks(player, true, true, true, s -> true);
    }

    @Nonnull
    public static NonNullList<ItemStack> getNonEmptyStacks(PlayerEntity player, Predicate<ItemStack> predicate) {
        return getNonEmptyStacks(player, true, true, true, predicate);
    }

    @Nonnull
    public static NonNullList<ItemStack> getNonEmptyStacks(PlayerEntity player, boolean includeMain, boolean includeOffHand, boolean includeArmor) {
        return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor, s -> true);
    }

    @Nonnull
    public static NonNullList<ItemStack> getNonEmptyStacks(PlayerEntity player, boolean includeMain, boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {
        NonNullList<ItemStack> list = NonNullList.create();

        if (includeMain)
            for (ItemStack stack : player.inventory.items)
                if (!stack.isEmpty() && predicate.test(stack))
                    list.add(stack);

        if (includeOffHand)
            for (ItemStack stack : player.inventory.offhand)
                if (!stack.isEmpty() && predicate.test(stack))
                    list.add(stack);

        if (includeArmor)
            for (ItemStack stack : player.inventory.armor)
                if (!stack.isEmpty() && predicate.test(stack))
                    list.add(stack);

        return list;
    }

    /**
     * Gets the first matching valid ItemStack in the players inventory.
     *
     * @param player         The player
     * @param includeMain    Search the players main inventory (hotbar and the 3 rows above that,
     *                       hotbar is first, I think).
     * @param includeOffHand Check the player's offhand slot as well.
     * @param includeArmor   Check the player's armor slots as well.
     * @param predicate      The condition to check for on the ItemStack.
     * @return The first ItemStack that matches the predicate, or ItemStack.EMPTY if there is no
     * match. Search order is offHand, armor, main.
     * @since 2.3.1
     */
    @Nonnull
    public static ItemStack getFirstValidStack(PlayerEntity player, boolean includeMain, boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {
        if (includeOffHand)
            for (ItemStack stack : player.inventory.offhand)
                if (!stack.isEmpty() && predicate.test(stack))
                    return stack;

        if (includeArmor)
            for (ItemStack stack : player.inventory.armor)
                if (!stack.isEmpty() && predicate.test(stack))
                    return stack;

        if (includeMain)
            for (ItemStack stack : player.inventory.items)
                if (!stack.isEmpty() && predicate.test(stack))
                    return stack;

        return ItemStack.EMPTY;
    }
}