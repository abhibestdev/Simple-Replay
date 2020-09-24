package me.abhi.replay;

import com.google.common.collect.Maps;
import lombok.Getter;
import me.abhi.core.CorePlugin;
import me.abhi.replay.bukkit.BukkitListener;
import me.abhi.replay.command.ReplayCommand;
import me.abhi.replay.location.ReplayLocation;
import me.abhi.replay.npc.NPC;
import me.abhi.replay.packet.PacketListener;
import me.abhi.replay.task.RecordTask;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReplayPlugin extends JavaPlugin {

    @Getter
    private static ReplayPlugin instance;
    public static boolean started;
    public static List<ReplayLocation> locationList = new ArrayList<>();
    public static Map<Long, Packet> packetMap = Maps.newHashMap();
    public static NPC npc;

    @Override
    public void onEnable() {
        instance = this;

        CorePlugin.getCommandFramework().registerCommands(new ReplayCommand());
        new PacketListener(this);
        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
        new RecordTask().runTaskTimerAsynchronously(this, 1L, 1L);
    }
}
