package dev.drvzs.anticheat.data;

import dev.drvzs.anticheat.Anticheat;
import dev.drvzs.anticheat.check.Check;
import dev.drvzs.anticheat.data.settings.PlayerSettings;
import dev.drvzs.anticheat.event.EventTimer;
import dev.drvzs.anticheat.process.ProcessorManager;
import dev.drvzs.anticheat.utils.location.PlayerLocation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class PlayerData {

    private transient Player player;
    private final UUID uuid;
    private final String userName;
    private final List<Check> checks;
    private final PlayerSettings settings;

    private PlayerLocation currentLocation = new PlayerLocation(null, 0, 0, 0, 0, 0,
            false, System.currentTimeMillis());
    private PlayerLocation lastLocation = currentLocation, lastLastLocation = lastLocation;
    private Block blockPlaced;
    private int tick = 1;
    private int vehicleTicks;

    private EventTimer lastBlockPlaceCancelTimer = new EventTimer(20, this),
            lastTeleportTimer = new EventTimer(20, this),
            vehicleTimer = new EventTimer(40, this),
            lastBlockPlaceTimer = new EventTimer(20, this);

    private ProcessorManager processorManager;
    private boolean settingsLoaded = false;

    public PlayerData(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.userName = player.getName();
        this.checks = new ArrayList<>();
        this.processorManager = new ProcessorManager(this);
        this.settings = new PlayerSettings(uuid);
    }

    public void loadSettings() {
        Anticheat.getInstance().getPlayerDataManager().loadSettings(this)
                .thenRun(() -> settingsLoaded = true);
    }

    public CompletableFuture<Void> saveSettings() {
        if (settingsLoaded) {
            return Anticheat.getInstance().getPlayerDataManager().saveSettings(this);
        }
        return CompletableFuture.completedFuture(null);
    }
}
