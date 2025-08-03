package me.lefton.anticheat.action.punish;

import me.lefton.anticheat.action.alert.AlertData;
import me.lefton.anticheat.data.PlayerData;

public interface IPunishment {
    void handle(PlayerData playerData, AlertData alertData, String... data);
}
