package me.abhi.replay.task;

import me.abhi.replay.ReplayPlugin;
import me.abhi.replay.location.ReplayLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RecordTask extends BukkitRunnable {

    @Override
    public void run() {
        if (ReplayPlugin.started) {
            Player player = Bukkit.getPlayer("abhf");

            if (player == null) return;

     //       ReplayPlugin.locationList.add(new ReplayLocation(player.getLocation(), player.isSneaking()));
        }
    }
}
