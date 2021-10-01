package com.hero.witchery_rewitched.item;

import com.hero.witchery_rewitched.api.capabilities.altar.AltarLocationCapability;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class Debug extends Item {
    public Debug(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        if(!context.getLevel().isClientSide) {
            context.getLevel().getChunkAt(pos).getCapability(AltarLocationCapability.INSTANCE).ifPresent(source ->
                    source.getAltars().forEach(altar ->
                            context.getPlayer().displayClientMessage(new StringTextComponent(
                                    "Altar in range " + Math.sqrt(altar.distSqr(pos))
                            ), false)
                    )
            );
        }
        /*
        if(context.getPlayer() != null && context.getPlayer().isShiftKeyDown()){
            LazyOptional<IPoppetWorlds> cap = context.getPlayer().getCapability(PoppetWorldCapability.INSTANCE);
            context.getPlayer().displayClientMessage(new StringTextComponent(cap.isPresent() +""), false);
        }
        else {
            final int[] covenSize = new int[1];
            context.getPlayer().getCapability(WitcheryDataCapability.INSTANCE).ifPresent(source -> covenSize[0] = source.getCovenSize());
            context.getPlayer().displayClientMessage(new StringTextComponent("Player coven size: " + covenSize[0] + (context.getLevel()).isClientSide), false);
        }*/
        return super.useOn(context);
    }
}
