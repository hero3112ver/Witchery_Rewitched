package com.hero.witchery_rewitched.item;

import com.hero.witchery_rewitched.util.util.PlayerUtils;
import com.hero.witchery_rewitched.util.capabilities.poppet_shelf.PoppetShelfCapability;
import com.hero.witchery_rewitched.util.capabilities.poppet_worlds.PoppetWorldCapability;
import com.hero.witchery_rewitched.block.poppet_shelf.PoppetShelfTileEntity;
import com.hero.witchery_rewitched.init.ModItems;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class PoppetBase extends Item{

    public PoppetBase(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {

        tooltip.add(new StringTextComponent(getPlayerName(stack)));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    public boolean verifyTagAfterLoad(CompoundNBT nbt) {
        super.verifyTagAfterLoad(nbt);
        boolean flag;
        if (nbt.contains("PlayerID")) {
            UUID id = NBTUtil.loadUUID(nbt.get("PlayerID"));
            nbt.putUUID("PlayerID", id);
            flag = true;
        }
        else if(nbt.contains("PlayerName")){
            String playerName = nbt.getString("PlayerName");
            nbt.putString("PlayerName", playerName);
            flag = true;
        }
        else{
            flag = false;
        }
        return flag;
    }

    public static String getPlayerName(ItemStack stack){
        String name = "Unbound";
        if (stack.hasTag()) {
            CompoundNBT compoundnbt = stack.getTag();
            if (compoundnbt.contains("PlayerName")) {
                name = compoundnbt.getString("PlayerName");
            }
        }
        return name;
    }

    public static UUID getPlayerID(ItemStack stack){
        UUID id = null;
        if (stack.hasTag()) {
            CompoundNBT compoundnbt = stack.getTag();
            if (compoundnbt.contains("PlayerID")) {
                id = NBTUtil.loadUUID(compoundnbt.get("PlayerID"));
            }
        }
        return id;
    }

    private static boolean removeFromPlayerInventory(PlayerEntity player, Item item){
        ItemStack stack = PlayerUtils.getFirstValidStack(player, true, true, false, itemStack -> itemStack.getItem() == item);
        if(stack != ItemStack.EMPTY){
            player.inventory.setItem(player.inventory.findSlotMatchingItem(stack), ItemStack.EMPTY);
            return true;
        }
        return false;
    }

    //TODO: spaghetti code central
    private static boolean removePoppetFromShelf(MinecraftServer server, PlayerEntity player, Item item){
        if(player.level.isClientSide )
            return false;
        AtomicBoolean flag = new AtomicBoolean(false);
        player.getCapability(PoppetWorldCapability.INSTANCE).ifPresent((source) -> {
            List<String> shelves = source.getShelves();
            for(int i = 0; i < shelves.size(); i++){
                if(flag.get())
                    continue;
                World world = server.getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(shelves.get(i))));
                if(world != null) {
                    world.getCapability(PoppetShelfCapability.INSTANCE).ifPresent((source2) -> {
                        List<Pair<GameProfile, BlockPos>> worldShelves = source2.getShelves();
                        for(Pair<GameProfile, BlockPos> pair: worldShelves){
                            if(flag.get())
                                continue;

                            TileEntity te = world.getBlockEntity(pair.getSecond());
                            ItemStack stack = new ItemStack(item);
                            TaglockKit.giveStackWithPlayerInfo(stack, player.getUUID(), player.getName().getString());
                            if(te instanceof PoppetShelfTileEntity){
                                if(((PoppetShelfTileEntity)te).destroyPoppet(stack))
                                    flag.set(true);

                                if(!((PoppetShelfTileEntity)te).contains(player)){
                                    source.removeShelfIfPresent(world);
                                }
                            }
                        }
                    });
                }
            }
        });

        return flag.get();
    }


    public static void onDeath(LivingDeathEvent event){
        if(event.getEntity() instanceof PlayerEntity){
            PlayerEntity player = ((PlayerEntity)event.getEntity());
            MinecraftServer server = event.getEntity().getServer();

            if(event.getSource() == DamageSource.FALL){
                Item item = ModItems.EARTH_PROTECTION_POPPET.get();
                if(removeFromPlayerInventory(player, item) || removePoppetFromShelf(server, player, item)){
                    player.displayClientMessage(new StringTextComponent("Your earth protection poppet triggered!"), true);
                    event.setCanceled(true);
                    player.setHealth(10);
                }
            }
            else if(event.getSource() == DamageSource.ON_FIRE || event.getSource() == DamageSource.IN_FIRE || DamageSource.LAVA == event.getSource()){
                Item item = ModItems.FIRE_PROTECTION_POPPET.get();
                if(removeFromPlayerInventory(player, item) || removePoppetFromShelf(server, player, item)){
                    event.setCanceled(true);
                    player.displayClientMessage(new StringTextComponent("Your fire protection poppet triggered!"), true);
                    player.addEffect(new EffectInstance(Potions.FIRE_RESISTANCE.getEffects().get(0).getEffect(), 5*20));
                    player.setHealth(10);
                }
            }
            else if(event.getSource() == DamageSource.DROWN){
                Item item = ModItems.WATER_PROTECTION_POPPET.get();
                if(removeFromPlayerInventory(player, item) || removePoppetFromShelf(server, player, item)){
                    player.displayClientMessage(new StringTextComponent("Your water protection poppet triggered!"), true);
                    event.setCanceled(true);
                    player.addEffect(new EffectInstance(Potions.WATER_BREATHING.getEffects().get(0).getEffect(), 5*20));
                    player.setHealth(10);
                }
            }
            else if(event.getSource() == DamageSource.STARVE){
                Item item = ModItems.HUNGER_PROTECTION_POPPET.get();
                if(removeFromPlayerInventory(player, item) || removePoppetFromShelf(server, player, item)){
                    player.displayClientMessage(new StringTextComponent("Your hunger protection poppet triggered!"), true);
                    event.setCanceled(true);
                    player.getFoodData().setFoodLevel(10);
                    player.setHealth(10);
                }
            }
            if(!event.isCanceled()){
                Item item = ModItems.DEATH_PROTECTION_POPPET.get();
                if(removeFromPlayerInventory(player, item) || removePoppetFromShelf(server, player, item)){
                    player.displayClientMessage(new StringTextComponent("Your death protection poppet triggered!"), true);
                    event.setCanceled(true);
                    player.setHealth(10);
                }
            }
        }
    }

    public static void onItemDestroy(PlayerDestroyItemEvent event){
        if(event.getOriginal().getItem() instanceof ToolItem){
            PlayerEntity player = ((PlayerEntity)event.getEntity());
            MinecraftServer server = event.getEntity().getServer();
            Item item = ModItems.TOOL_PROTECTION_POPPET.get();
            if(removeFromPlayerInventory(player, item) || removePoppetFromShelf(server, player, item)){
                player.displayClientMessage(new StringTextComponent("Your tool protection poppet triggered!"), true);
                ItemStack newStack = event.getOriginal().copy();
                newStack.setDamageValue(0);
                if(event.getHand() != null){
                    player.setItemInHand(event.getHand(), newStack);
                }
                else{
                    PlayerUtils.giveItem(player, newStack);
                }
            }
        }
    }

    public static void onArmorBreak(LivingEquipmentChangeEvent event){
        if(event.getEntity() instanceof PlayerEntity){
            boolean broke = event.getFrom().getDamageValue() + 1 >= event.getFrom().getMaxDamage();
            boolean armorItem = event.getFrom().getItem() instanceof ArmorItem;
            EquipmentSlotType slot2 = event.getSlot();
            boolean plz = event.getTo().getCount() == 0 && event.getTo().getTag() != null;
            if(broke && armorItem && slot2 != null && slot2 != EquipmentSlotType.MAINHAND && slot2 != EquipmentSlotType.OFFHAND && event.getTo().isEmpty() && plz){
                Item item = ModItems.ARMOR_PROTECTION_POPPET.get();
                PlayerEntity player = (PlayerEntity)event.getEntity();
                MinecraftServer server = event.getEntity().getServer();
                if(removeFromPlayerInventory(player, item) || removePoppetFromShelf(server, player, item)){
                    player.displayClientMessage(new StringTextComponent("Your armor protection poppet triggered!"), true);
                    ItemStack newStack = event.getFrom().copy();
                    newStack.setDamageValue(0);

                    EquipmentSlotType slotType = event.getSlot();
                    if(slotType != null)
                        player.inventory.armor.set(slotType.getIndex(), newStack);
                    else{
                        PlayerUtils.giveItem(player, newStack);
                    }
                }
            }
        }
    }


}
