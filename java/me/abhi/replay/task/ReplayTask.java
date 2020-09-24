package me.abhi.replay.task;

import me.abhi.replay.ReplayPlugin;
import me.abhi.replay.location.ReplayLocation;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ReplayTask extends BukkitRunnable {

    private int i = 0;

    @Override
    public void run() {
        if (i < ReplayPlugin.locationList.size()) {
            ReplayLocation location = ReplayPlugin.locationList.get(i);

            ReplayPlugin.npc.setLocation(location.getLocation());
            DataWatcher dataWatcher = new DataWatcher(ReplayPlugin.npc.entityPlayer);
            PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = null;
            if (location.isSneak()) {
                dataWatcher.a(0, (byte) 0x02);
            } else {
                dataWatcher.a(0, (byte) 0);
            }
            DataWatcher skinWatcher = ReplayPlugin.npc.entityPlayer.getDataWatcher();
            skinWatcher.watch(10, (byte) 127);
            for (Player player : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(ReplayPlugin.npc.getEntityID(), skinWatcher, true));
            }
            packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(ReplayPlugin.npc.getEntityID(), dataWatcher, true);
            for (Player player : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityMetadata);
            }
            if (location.getPacket() instanceof PacketPlayInArmAnimation) {
                PacketPlayOutAnimation packetPlayOutAnimation = new PacketPlayOutAnimation(ReplayPlugin.npc.entityPlayer, 0);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutAnimation);
                }
            }
            PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(ReplayPlugin.npc.getEntityID(), 0, CraftItemStack.asNMSCopy(location.getItemInHand()));

            for (Player player : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment);
            }
            ReplayPlugin.npc.updateRelativeLocation();
            i++;
        } else {
            ReplayPlugin.npc.remove();
            this.cancel();
        }

    }
}
