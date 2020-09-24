package me.abhi.replay.npc;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.abhi.replay.ReplayPlugin;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/*
 * NMS code was given to me by svdragster
 */
public class NPC {
    private final MinecraftServer server;

    public EntityPlayer entityPlayer;
    private boolean showInTab;
    private Location location;
    private boolean invis;
    private ArrayList<Player> players;

    private Player toFollow;
    private Vector followRelatively;

    public NPC(UUID uuid, String name, String skinValue, String skinSignature, Location location) {
        server = ((CraftServer) Bukkit.getServer()).getServer();

        final WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(uuid, name);
        gameProfile.getProperties().clear();
        gameProfile.getProperties().put("textures", new Property("textures", skinValue, skinSignature));
        this.entityPlayer = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
        this.entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.entityPlayer.ping = ThreadLocalRandom.current().nextInt(0, 500);
        this.location = location;
        this.invis = false;
        this.players = new ArrayList<Player>();
        this.toFollow = null;
        this.followRelatively = new Vector(0, 0, 0);
    }

    public String getName() {
        return this.entityPlayer.getName();
    }

    public UUID getUUID() {
        return this.entityPlayer.getUniqueID();
    }

    public int getEntityID() {
        return this.entityPlayer.getId();
    }

    public boolean isInTab() {
        return this.showInTab;
    }

    public Location getLocation() {
        return this.location;
    }

    public int getPing() {
        return this.entityPlayer.ping;
    }

    public WorldSettings.EnumGamemode getGameMode() {
        return this.entityPlayer.playerInteractManager.getGameMode();
    }

    public boolean isInvisible() {
        return this.invis;
    }

    public Player getPlayerToFollow() {
        return this.toFollow;
    }

    public boolean followsPlayer() {
        return this.toFollow != null;
    }

    public Vector getFollowRelative() {
        return this.followRelatively;
    }

    public NPC updateRelativeLocation() {
        if (this.toFollow == null || this.followRelatively == null)
            return this;
        this.setLocation(this.toFollow.getLocation().clone().add(this.followRelatively));
        return this;
    }

    public NPC setToFollow(Player player, Vector relatively) {
        this.toFollow = player;
        this.followRelatively = relatively;
        return this;
    }

    public NPC setFollowRelative(Vector relatively) {
        this.followRelatively = relatively;
        return this;
    }

    public NPC setShowInTab(boolean tab) {
        this.showInTab = tab;
        return this;
    }

    public NPC setLocation(Location location) {
        this.entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.location = location;
        for (Player player : this.players) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityTeleport(this.entityPlayer));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(this.entityPlayer, (byte) (int) (location.getYaw() * 256.0F / 360.0F)));
        }
        return this;
    }

    public NPC setInvisible(boolean invis) {
        this.invis = invis;
        return this;
    }

    public NPC setPing(int ping) {
        this.entityPlayer.ping = ping;
        return this;
    }

    public NPC setGameMode(WorldSettings.EnumGamemode gamemode) {
        this.entityPlayer.playerInteractManager.setGameMode(gamemode);
        return this;
    }

    public NPC setArmorSet(ItemStack helm, ItemStack chest, ItemStack legs, ItemStack boots) {
        PacketPlayOutEntityEquipment helmPacket = new PacketPlayOutEntityEquipment(this.entityPlayer.getId(), 1, CraftItemStack.asNMSCopy(helm));
        PacketPlayOutEntityEquipment chestPacket = new PacketPlayOutEntityEquipment(this.entityPlayer.getId(), 2, CraftItemStack.asNMSCopy(chest));
        PacketPlayOutEntityEquipment legsPacket = new PacketPlayOutEntityEquipment(this.entityPlayer.getId(), 3, CraftItemStack.asNMSCopy(legs));
        PacketPlayOutEntityEquipment bootsPacket = new PacketPlayOutEntityEquipment(this.entityPlayer.getId(), 4, CraftItemStack.asNMSCopy(boots));

        for (Player player : this.players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(helmPacket);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(chestPacket);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(legsPacket);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(bootsPacket);
        }

        return this;
    }

    public boolean canSee(Player player) {
        return this.players.contains(player);
    }

    public void addPlayer(Player player) {
        if (this.players.contains(player))
            return;
        this.players.add(player);
        this.spawn(player);
    }

    public void removePlayer(Player player) {
        if (this.players.contains(player))
            return;
        this.players.remove(player);
        this.despawn(player);
    }

    public void remove() {
        for (Player lp : this.players)
            if (lp != null && lp.isOnline())
                this.despawn(lp);
        this.players = new ArrayList<Player>();
    }

    private void spawn(Player player) {
        if (player == null) return;

        final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        if (this.showInTab)
            connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, this.entityPlayer));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(this.entityPlayer));
        connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.UPDATE_GAME_MODE, this.entityPlayer));

        if (this.invis) {
            connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.UPDATE_LATENCY, this.entityPlayer));
            try {
                WrappedDataWatcher watcher = new WrappedDataWatcher();
                watcher.setObject(0, false ? (byte) 0 : 0x20);
                WrapperPlayServerEntityMetadata update = new WrapperPlayServerEntityMetadata();
                update.setEntityId(this.entityPlayer.getId());
                update.setEntityMetadata(watcher.getWatchableObjects());
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, update.getHandle());
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void despawn(Player player) {
        if (player == null) return;

        final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(this.entityPlayer.getId()));
        if (this.showInTab)
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ReplayPlugin.getInstance(), () -> connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, this.entityPlayer)), 5);
    }

    private EntityPlayer getEntityPlayer() {
        return this.entityPlayer;
    }
}