package me.abhi.replay.bukkit;

import me.abhi.replay.ReplayPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BukkitListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (ReplayPlugin.started) {
            Player player = event.getPlayer();
        }
    }
}
