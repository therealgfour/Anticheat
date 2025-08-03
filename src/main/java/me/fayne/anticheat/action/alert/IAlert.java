package me.fayne.anticheat.action.alert;

import me.fayne.anticheat.data.PlayerData;

public interface IAlert {
    void handle(PlayerData playerData, AlertData alertData, String... data);
}
