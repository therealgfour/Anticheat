package dev.drvzs.anticheat.action.punish.impl;

import dev.drvzs.anticheat.action.alert.AlertData;
import dev.drvzs.anticheat.action.punish.IPunishment;
import dev.drvzs.anticheat.action.punish.PunishmentType;
import dev.drvzs.anticheat.data.PlayerData;

public class Punishment implements IPunishment {

    @Override
    public void handle(PlayerData playerData, AlertData alertData, String... data) {
        // TODO: hook with phoenix punishments
        if (alertData.getViolations() > alertData.getPunishmentVL())
            playerData.getPlayer().kickPlayer(PunishmentType.CONSOLE_BAN.getDesc());
    }
}