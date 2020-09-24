package me.abhi.replay.command;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.abhi.core.util.command.Command;
import me.abhi.core.util.command.CommandArgs;
import me.abhi.replay.ReplayPlugin;
import me.abhi.replay.npc.NPC;
import me.abhi.replay.task.ReplayTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

public class ReplayCommand {


    @Command(name = "replay", inGameOnly = true)
    public void replay(CommandArgs args) {
        if (ReplayPlugin.started) {
            ReplayPlugin.started = false;
            args.getSender().sendMessage(ChatColor.RED + "The replay has ended.");

            GameProfile gameProfile = ((CraftPlayer) args.getPlayer()).getProfile();
            String skinSig = "";
            String skinVal = "";

            PropertyMap propertyMap = gameProfile.getProperties();
            for (Property property : propertyMap.get("textures")) {
                skinSig = property.getSignature();
                skinVal = property.getValue();
            }
            ReplayPlugin.npc = new NPC(args.getPlayer().getUniqueId(), args.getPlayer().getName(), skinVal, skinSig, args.getPlayer().getLocation());
            ReplayPlugin.npc.addPlayer(args.getPlayer());
            new ReplayTask().runTaskTimer(ReplayPlugin.getInstance(), 1L, 1L);
        } else {
            ReplayPlugin.packetMap.clear();
            ReplayPlugin.locationList.clear();
            ReplayPlugin.started = true;
            args.getSender().sendMessage(ChatColor.GREEN + "The replay has been started.");
        }
    }
}
