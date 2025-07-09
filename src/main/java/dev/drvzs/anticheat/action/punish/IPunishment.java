package dev.drvzs.anticheat.action.punish;

import dev.drvzs.anticheat.action.alert.AlertData;
import dev.drvzs.anticheat.data.PlayerData;

public interface IPunishment {
    void handle(PlayerData playerData, AlertData alertData, String... data);
}
