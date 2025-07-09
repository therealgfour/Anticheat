package dev.drvzs.anticheat.data;

import dev.drvzs.anticheat.Anticheat;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlayerDataManager {
    private final Map<UUID, PlayerData> userMap = new ConcurrentHashMap<>();

    public void addUser(Player player) {
        PlayerData playerData = new PlayerData(player);
        this.userMap.put(player.getUniqueId(), playerData);

        playerData.loadSettings();

        Anticheat.getInstance().getCheckManager().loadToPlayer(playerData);
    }

    public void removeUser(Player player) {
        PlayerData playerData = userMap.get(player.getUniqueId());
        if (playerData != null) {
            playerData.saveSettings();
            this.userMap.remove(player.getUniqueId());
        }
    }

    public CompletableFuture<Void> loadSettings(PlayerData playerData) {
        return Anticheat.getInstance().getMongoDB()
                .getPlayerSettings(playerData.getUuid())
                .thenAccept(settings -> {
                    if (settings != null) {
                        playerData.getSettings().setAlertsEnabled(settings.isAlertsEnabled());
                    }
                });
    }

    public CompletableFuture<Void> saveSettings(PlayerData playerData) {
        return Anticheat.getInstance().getMongoDB()
                .savePlayerSettings(playerData.getPlayer().getUniqueId(), playerData.getSettings());
    }

    public PlayerData getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public PlayerData getUser(UUID uuid) {
        return this.userMap.get(uuid);
    }
}
