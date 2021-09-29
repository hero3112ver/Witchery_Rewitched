package com.hero.witchery_rewitched.block.witch_oven;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.config.WitcheryRewitchedConfig;
import com.hero.witchery_rewitched.crafting.recipe.ModRecipes;
import com.hero.witchery_rewitched.crafting.recipe.OvenCookingRecipe;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModItems;
import com.hero.witchery_rewitched.init.ModTileEntities;
import com.hero.witchery_rewitched.block.INamedContainerExtraData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class WitchOvenTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerExtraData, ISidedInventory {
    public static float WORKTIME = WitcheryRewitchedConfig.Server.debug.get() ? 2*20 : 300;
    private int progress = 0;
    private int fuelTicks = 0;
    private int totalFuelTicks = 0;
    private final int CHANCE_OUTPUT = 2;

    private static  final int[] INPUT_SLOTS = new int[]{1, 0, 2};   // 0: INPUT, 1: FUEL, 2: POT
    private static final int[] OUTPUT_SLOTS = new int[]{3, 4};     // 3: OUTPUT 4: FUME OUTPUT
    public static final int INVENTORY_SIZE = 5;

    protected NonNullList<ItemStack> items;
    private final LazyOptional<? extends IItemHandler>[] handlers;

    private final IIntArray fields = new IIntArray() {
        @Override
        public int get(int index) {
            switch(index){
                case 0:
                    return progress;
                case 2:
                    return fuelTicks;
                case 3:
                    return totalFuelTicks;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch(index){
                case 0:
                    progress = value;
                    break;
                case 2:
                    fuelTicks = value;
                    break;
                case 3:
                    totalFuelTicks = value;
                    break;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public WitchOvenTileEntity() {
        super(ModTileEntities.WITCH_OVEN.get());
        handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
        items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    }

    public void encodeExtraData(PacketBuffer buffer){
        buffer.writeBlockPos(worldPosition);
        buffer.writeByte(fields.getCount());
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        switch(side){
            case DOWN:
                return OUTPUT_SLOTS;
            default:
                return INPUT_SLOTS;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return Arrays.stream(OUTPUT_SLOTS).anyMatch(i -> i == index);
    }




    @Override
    public void tick() {
        if(fuelTicks > 0)
            fuelTicks--;
        if(this.level == null || this.level.isClientSide)
            return;
        OvenCookingRecipe recipe = getRecipe();
        if(recipe != null && getItem(2).sameItem(ModItems.COOKED_CLAY_POT.get().getDefaultInstance())) {
            if(fuelTicks > 0)
                doWork(recipe);
            else
                this.consumeFuel();
        }
        else {
            stopWork();
        }
    }

    private void consumeFuel(){
        ItemStack item = getItem(1);
        int burnTime = ForgeHooks.getBurnTime(item);
        if(burnTime>0){
            removeItem(1, 1);
            fuelTicks += burnTime;
            totalFuelTicks = burnTime;
        }
    }

    @Nullable
    public OvenCookingRecipe getRecipe() {
        RecipeManager manager = level.getRecipeManager();
        List<OvenCookingRecipe> recipes = manager.getAllRecipesFor(ModRecipes.Types.WITCH_OVEN);
        for(OvenCookingRecipe recipe : recipes){
            if(recipe.matches(this, level))
                return recipe;
        }
        return null;
    }

    public int getInputSlotCount() {
        return 2;
    }

    private ItemStack getWorkOutPut(@Nullable OvenCookingRecipe recipe){
        if(recipe != null) {
            return recipe.getResultItem().copy();
        }
        return ItemStack.EMPTY;
    }



    private ItemStack getPossibleOutPut(@Nullable OvenCookingRecipe recipe){
        if(recipe != null){
            ItemStack result = recipe.getPossibleResult();
            return result != null ? result.copy() : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }


    private void doWork(OvenCookingRecipe recipe) {
        assert this.level != null;

        ItemStack current = getItem(3);
        ItemStack currentPossible = getItem(4);
        ItemStack output = getWorkOutPut(recipe);
        ItemStack possibleOutput = getPossibleOutPut(recipe);

        if(!current.isEmpty()){
            int newCount  = current.getCount() + 1;

            if(!ItemStack.isSame(current, output ) || newCount > output.getMaxStackSize()){
                stopWork();
                return;
            }
        }

        if(!currentPossible.isEmpty()){
            int posNewCount = currentPossible.getCount() + 1;
            if(!ItemStack.isSame(currentPossible, possibleOutput) || posNewCount > possibleOutput.getMaxStackSize()) {
                stopWork();
                return;
            }
        }

        if(progress < WORKTIME){
            progress += 1 + checkForFunnelsWork();
        }

        if(progress >= WORKTIME){
            finishWork(current, currentPossible, output, possibleOutput);
        }
    }

    private float checkForFunnelsWork(){
        float total = 0;
        Block up1 = level.getBlockState(new BlockPos(worldPosition.getX(),worldPosition.getY()+1, worldPosition.getZ())).getBlock();
        Block up2 = level.getBlockState(new BlockPos(worldPosition.getX(),worldPosition.getY()+1, worldPosition.getZ())).getBlock();

        if(up1 == ModBlocks.FUME_FUNNEL.get() || up1 == ModBlocks.FILTERED_FUME_FUNNEL.get()) total += .1;
        if(up2 == ModBlocks.FUME_FUNNEL.get() || up1 == ModBlocks.FILTERED_FUME_FUNNEL.get()) total += .1;
        return total;
    }

    private float checkForFunnelsFume(){
        float total = 0;
        Block up1 = level.getBlockState(new BlockPos(worldPosition.getX(),worldPosition.getY()+1, worldPosition.getZ())).getBlock();
        Block up2 = level.getBlockState(new BlockPos(worldPosition.getX(),worldPosition.getY()+1, worldPosition.getZ())).getBlock();

        if(up1 == ModBlocks.FUME_FUNNEL.get()) total += 2.5;
        else if(up1 == ModBlocks.FILTERED_FUME_FUNNEL.get()) total += 3;
        if(up2 == ModBlocks.FUME_FUNNEL.get()) total += 2.5;
        else if(up2 == ModBlocks.FILTERED_FUME_FUNNEL.get()) total += 3;
        return total;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if(index == 0 && !(stack.getItem() == ModItems.COOKED_CLAY_POT.get())){
            return true;
        }
        else if( ForgeHooks.getBurnTime(stack) > 0 && index == 1){
            return true;
        }
        else if(index ==  2 && stack.getItem() == ModItems.COOKED_CLAY_POT.get()){
            return true;
        }
        else if(Arrays.stream(OUTPUT_SLOTS).anyMatch(i -> i == index)){
            return false;
        }
        return false;
    }



    private void finishWork(ItemStack current, ItemStack currentPossible, ItemStack output, ItemStack possibleOutput){
        if(!current.isEmpty()){
            current.grow(output.getCount());
        }
        else {
            setItem(3, output);
        }
        float chance = CHANCE_OUTPUT + checkForFunnelsFume();
        int rand = this.level.getRandom().nextInt(10);
        if(rand <= (chance)) {
            if (!currentPossible.isEmpty()) {
                currentPossible.grow(possibleOutput.getCount());
            }
            else {
                setItem(4, possibleOutput);
            }
            this.removeItem(2, 1);
        }
        progress = 0;
        this.removeItem(0, 1);
    }

    private void stopWork() {
        progress = 0;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.witchery_rewitched.witch_oven");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory player, PlayerEntity playerEntity) {
        return WitchOvenContainer.createWitchOven(id, player, this, fields);
    }

    // Basic Shit I think after this point

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        ItemStackHelper.loadAllItems(nbt, this.items);
        progress = nbt.getInt("progress");
        fuelTicks = nbt.getInt("fuelticks");
        totalFuelTicks = nbt.getInt("totalfuelticks");
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        ItemStackHelper.saveAllItems(compound, this.items);

        compound.putInt("progress", progress);
        compound.putInt("fuelticks", fuelTicks);
        compound.putInt("totalfuelticks",totalFuelTicks);
        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        ItemStackHelper.loadAllItems(packet.getTag(), this.items);
        CompoundNBT tags = packet.getTag();
        progress = tags.getInt("progress");
        fuelTicks = tags.getInt("fuelticks");
        totalFuelTicks = tags.getInt("totalfuelticks");
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tags= super.getUpdateTag();
        tags.putInt("progress", progress);
        tags.putInt("fuelticks", fuelTicks);
        tags.putInt("totalfuelticks",totalFuelTicks);
        return tags;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (!this.remove && side != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == Direction.UP) {
                return this.handlers[0].cast();
            }
            else if (side == Direction.DOWN) {
                return this.handlers[1].cast();
            }
            else {
                return this.handlers[2].cast();
            }
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return getItem(0).isEmpty() && getItem(1).isEmpty() && getItem(2).isEmpty() && getItem(3).isEmpty() && getItem(4).isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < this.items.size() ? this.items.get(index) : ItemStack.EMPTY;
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
    public void setRemoved() {
        super.setRemoved();

        for (LazyOptional<? extends IItemHandler> handler : this.handlers) {
            handler.invalidate();
        }
    }
}
