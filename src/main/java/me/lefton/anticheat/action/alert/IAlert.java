package me.lefton.anticheat.action.alert;

import me.lefton.anticheat.data.PlayerData;

public interface IAlert {
    void handle(PlayerData playerData, AlertData alertData, String... data);
}
