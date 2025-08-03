package me.lefton.anticheat.action.punish.impl;

import me.lefton.anticheat.action.alert.AlertData;
import me.lefton.anticheat.action.punish.IPunishment;
import me.lefton.anticheat.action.punish.PunishmentType;
import me.lefton.anticheat.data.PlayerData;

public class Punishment implements IPunishment {

    @Override
    public void handle(PlayerData playerData, AlertData alertData, String... data) {
        // TODO: hook with phoenix punishments
        if (alertData.getViolations() > alertData.getPunishmentVL())
            playerData.getPlayer().kickPlayer(PunishmentType.CONSOLE_BAN.getDesc());
    }
}