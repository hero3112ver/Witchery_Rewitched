package com.hero.witchery_rewitched.network.packets;

import com.hero.witchery_rewitched.util.capabilities.witchery_data.WitcheryDataCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CovenSyncPacket {
    private int covenSize;
    public CovenSyncPacket(){}

    public CovenSyncPacket(int covenSize){this.covenSize = covenSize;}

    public CovenSyncPacket(PlayerEntity player){
        player.getCapability(WitcheryDataCapability.INSTANCE).ifPresent(source ->
                this.covenSize = source.getCovenSize()
        );
    }

    public static CovenSyncPacket decode(PacketBuffer buff){
        return new CovenSyncPacket(buff.readByte());
    }

    public void encode(PacketBuffer buff){
        buff.writeByte(this.covenSize);
    }

    public static void handle(CovenSyncPacket packet, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            CovenSyncInfo.covenSize = packet.covenSize;
        });
        context.get().setPacketHandled(true);
    }
}
