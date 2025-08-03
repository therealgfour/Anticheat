package me.fayne.anticheat.action.punish;

import me.fayne.anticheat.action.alert.AlertData;
import me.fayne.anticheat.data.PlayerData;

public interface IPunishment {
    void handle(PlayerData playerData, AlertData alertData, String... data);
}
