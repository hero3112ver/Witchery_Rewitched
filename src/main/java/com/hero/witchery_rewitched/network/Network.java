package com.hero.witchery_rewitched.network;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.config.WitcheryRewitchedConfig;
import com.hero.witchery_rewitched.network.packets.CovenSyncPacket;
import com.hero.witchery_rewitched.network.packets.WitchOvenLocationPacket;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Objects;
import java.util.regex.Pattern;
@Mod.EventBusSubscriber(modid = WitcheryRewitched.MODID)
public class Network {
    public static final String VERSION = "wrb-net-0";
    private static final Pattern NET_VERSION_PATTERN = Pattern.compile("wrb-net-\\d+$");
    private static final Pattern MOD_VERSION_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");
    private static final int SYNC_FREQUENCY = WitcheryRewitchedConfig.Server.debug.get() ? 20 : 600;

    public static SimpleChannel channel;

    static {
        channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(WitcheryRewitched.MODID + ":" + "network"))
                .clientAcceptedVersions(s -> Objects.equals(s, VERSION))
                .serverAcceptedVersions(s -> Objects.equals(s, VERSION))
                .networkProtocolVersion(() -> VERSION)
                .simpleChannel();

        channel.messageBuilder(WitchOvenLocationPacket.class, 1)
                .decoder(WitchOvenLocationPacket::decode)
                .encoder(WitchOvenLocationPacket::encode)
                .consumer(WitchOvenLocationPacket::handle)
                .add();

        channel.messageBuilder(CovenSyncPacket.class, 2)
                .decoder(CovenSyncPacket::decode)
                .encoder(CovenSyncPacket::encode)
                .consumer(CovenSyncPacket::handle)
                .add();
    }

    private Network() {}

    public static void init() {}


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.player instanceof ServerPlayerEntity && event.player.tickCount % SYNC_FREQUENCY == 0){
            CovenSyncPacket packet = new CovenSyncPacket(event.player);
                Network.channel.sendTo(packet, ((ServerPlayerEntity) event.player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }


    void verifyNetworkVersion(PacketBuffer buffer) {
        // Throws an exception if versions do not match and provides a less cryptic message to the player
        // NOTE: This hangs without displaying a message on SSP, but that can't happen without messing with the written
        // network version
        String serverNetVersion = readNetworkVersion(buffer);
        String serverModVersion = readModVersion(buffer);

        WitcheryRewitched.LOGGER.debug("Read Silent Gear server version as {} ({})", serverModVersion, serverNetVersion);

        if (!Network.VERSION.equals(serverNetVersion)) {
            String msg = String.format("This server is running a different version of Silent Gear. Try updating Silent Gear on the client and/or server. Client version is %s (%s) and server version is %s (%s).",
                    Network.VERSION,
                    serverModVersion,
                    serverNetVersion);
            throw new MismatchedVersionsException(msg);
        }
    }

    private static String readNetworkVersion(PacketBuffer buffer) {
        String str = buffer.readUtf(16);
        if (!NET_VERSION_PATTERN.matcher(str).matches()) {
            // Server is running a version that doesn't encode the net version
            return "UNKNOWN (received: " + str + ")";
        }
        return str;
    }

    private static String readModVersion(PacketBuffer buffer) {
        String str = buffer.readUtf(16);
        if (!"NONE".equals(str) && !MOD_VERSION_PATTERN.matcher(str).matches()) {
            // Server is running a version that doesn't encode the mod version
            return "UNKNOWN (received: " + str + ")";
        }
        return str;
    }

    public class MismatchedVersionsException extends RuntimeException {
        public MismatchedVersionsException(String msg) {
            super(msg);
        }
    }

}
