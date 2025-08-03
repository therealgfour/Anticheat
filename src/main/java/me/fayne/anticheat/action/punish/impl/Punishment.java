package me.fayne.anticheat.action.punish.impl;

import me.fayne.anticheat.action.alert.AlertData;
import me.fayne.anticheat.action.punish.IPunishment;
import me.fayne.anticheat.action.punish.PunishmentType;
import me.fayne.anticheat.data.PlayerData;

public class Punishment implements IPunishment {

    @Override
    public void handle(PlayerData playerData, AlertData alertData, String... data) {
        // TODO: hook with phoenix punishments
        if (alertData.getViolations() > alertData.getPunishmentVL())
            playerData.getPlayer().kickPlayer(PunishmentType.CONSOLE_BAN.getDesc());
    }
}