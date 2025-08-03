package me.lefton.anticheat.check;

import me.lefton.anticheat.Anticheat;
import me.lefton.anticheat.action.alert.AlertData;
import me.lefton.anticheat.data.PlayerData;
import me.lefton.anticheat.event.Event;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Getter
@Setter
public abstract class Check extends Event {

    @Setter
    private PlayerData playerData;
    private CheckData data;

    public double buffer;
    public boolean booleanBuffer;

    private AlertData alertData;

    public Check() {
        this.alertData = new AlertData();
        if (getClass().isAnnotationPresent(CheckData.class)) {
            this.data = getClass().getAnnotation(CheckData.class);


            alertData.punishmentVL = this.data.punishmentVL();
            alertData.checkName = this.data.name();
            alertData.checkType = this.data.type();
            alertData.enabled = this.data.enabled();
            alertData.experimental = this.data.experimental();
        }
    }

    public void fail(String... data) {
        Anticheat.getInstance().getAlert().handle(playerData, alertData, data);
        //new Punishment().handle(playerData, alertData, data);
    }

    public void debug(String msg) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(msg);
            }
        }
    }

    @Override
    public void setupTimers(PlayerData playerData) {

    }

    public boolean isCancellable() {
        return MinecraftServer.getServer().recentTps[0] <= 19.5 || this.playerData.getPlayer().getAllowFlight()
                || this.playerData.getPlayer().isFlying() || this.playerData.getPlayer().getGameMode() == GameMode.CREATIVE
                || this.playerData.getPlayer().getGameMode() == GameMode.SPECTATOR;
    }
}