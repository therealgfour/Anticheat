package dev.drvzs.anticheat.action.alert;

import dev.drvzs.anticheat.data.PlayerData;

public interface IAlert {
    void handle(PlayerData playerData, AlertData alertData, String... data);
}
