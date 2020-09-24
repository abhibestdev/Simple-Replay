package me.abhi.replay.packet;

import com.comphenix.tinyprotocol.TinyProtocol;
import io.netty.channel.Channel;
import me.abhi.replay.ReplayPlugin;
import me.abhi.replay.location.ReplayLocation;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

public class PacketListener {

    public PacketListener(ReplayPlugin plugin) {
        new TinyProtocol(plugin) {

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
                if (ReplayPlugin.started) {
                    ReplayPlugin.locationList.add(new ReplayLocation(sender.getLocation(), sender.isSneaking(), (Packet) packet, sender.getItemInHand()));
                }
                return super.onPacketInAsync(sender, channel, packet);
            }

            @Override
            public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
                //          if (ReplayPlugin.started) {
                ///            ReplayPlugin.packetMap.put(System.currentTimeMillis(), (Packet) packet);
                //     }
                return super.onPacketOutAsync(receiver, channel, packet);
            }
        };
    }
}
