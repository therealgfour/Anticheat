package me.lefton.anticheat.listener;

import me.lefton.anticheat.Anticheat;
import me.lefton.anticheat.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {

    public PlayerListener() {
        Bukkit.getPluginManager().registerEvents(this, Anticheat.getInstance());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent event) {
        Anticheat.getInstance().getPlayerDataManager().addUser(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(final PlayerQuitEvent event) {
        Anticheat.getInstance().getPlayerDataManager().removeUser(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = Anticheat.getInstance().getPlayerDataManager().getUserMap().get(player.getUniqueId());

        if (playerData != null) {
            playerData.setBlockPlaced(event.getBlockPlaced());

            if (event.getItemInHand().getType().isBlock()) {
                playerData.getLastBlockPlaceTimer().reset();

                if (event.isCancelled()) {
                    playerData.getLastBlockPlaceCancelTimer().reset();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent event) {
        PlayerData user = Anticheat.getInstance().getPlayerDataManager().getUserMap().get(event.getPlayer().getUniqueId());
        if (user == null) return;

        PlayerTeleportEvent.TeleportCause cause = event.getCause();

        if (cause != PlayerTeleportEvent.TeleportCause.UNKNOWN)
            user.getLastTeleportTimer().reset();
    }
}
