package me.fayne.anticheat;

import com.github.retrooper.packetevents.PacketEvents;
import me.fayne.anticheat.action.alert.impl.Alert;
import me.fayne.anticheat.check.CheckManager;
import me.fayne.anticheat.command.impl.VCACommand;
import me.fayne.anticheat.data.PlayerDataManager;
import me.fayne.anticheat.database.MongoDB;
import me.fayne.anticheat.listener.PlayerListener;
import me.fayne.anticheat.packet.PacketManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class Anticheat extends JavaPlugin {

    @Getter
    private static Anticheat instance;
    private CheckManager checkManager;

    private final PlayerDataManager playerDataManager = new PlayerDataManager();
    private final ScheduledExecutorService checkService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService alertService = Executors.newSingleThreadScheduledExecutor();

    private Alert alert;
    private MongoDB mongoDB;

    @Override
    public void onEnable() {

        try {
            instance = this;

            saveDefaultConfig();

            PacketEvents.getAPI().init();
            PacketEvents.getAPI().getEventManager().registerListener(new PacketManager());

            new PlayerListener();

            this.alert = new Alert();

            this.checkManager = new CheckManager();
            this.checkManager.loadChecks();

            Bukkit.getScheduler().runTaskAsynchronously(this, this::initDatabase);

            // Reset all check violation over time
            this.checkService.scheduleAtFixedRate(() -> getPlayerDataManager().getUserMap().forEach((uuid, user) ->
                            user.getChecks().forEach(check -> check.getAlertData().setViolations(0))),
                    1L, 3L, TimeUnit.MINUTES);

            this.registerCommands();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDatabase() {
        String uri = this.getConfig().getString("DATABASE.uri");
        String name = this.getConfig().getString("DATABASE.name");
        this.mongoDB = new MongoDB(uri, name);
        this.mongoDB.connect();
    }

    private void registerCommands() {
        getCommand("vca").setExecutor(new VCACommand());
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        this.checkService.shutdown();
        this.mongoDB.close();
    }
}
