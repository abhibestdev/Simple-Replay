package me.abhi.replay.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class ReplayLocation {

    private Location location;
    private boolean sneak;
    private Packet packet;
    private ItemStack itemInHand;

}
