package dev.drvzs.anticheat.check;

import dev.drvzs.anticheat.check.impl.combat.autoclicker.AutoClickerA;
import dev.drvzs.anticheat.check.impl.combat.killaura.KillAuraB;
import dev.drvzs.anticheat.check.impl.misc.badpackets.BadPacketsA;
import dev.drvzs.anticheat.check.impl.misc.bednuke.BedNukeA;
import dev.drvzs.anticheat.check.impl.misc.interact.*;
import dev.drvzs.anticheat.check.impl.movement.jesus.JesusA;
import dev.drvzs.anticheat.check.impl.movement.nofall.NoFallA;
import dev.drvzs.anticheat.check.impl.movement.sprint.OmniSprintA;
import dev.drvzs.anticheat.data.PlayerData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CheckManager {
    public final List<Class<? extends Check>> checkClasses = new ArrayList<>();

    public void loadChecks() {
        this.checkClasses.add(InvalidActionD.class);
        this.checkClasses.add(InvalidActionA.class);
        this.checkClasses.add(AutoClickerA.class);
        this.checkClasses.add(InvalidActionB.class);
        this.checkClasses.add(KillAuraB.class);
        this.checkClasses.add(OmniSprintA.class);
        //this.checkClasses.add(ISeeYouVolcan.class);
        this.checkClasses.add(JesusA.class);
        this.checkClasses.add(InvalidActionC.class);
        this.checkClasses.add(InvalidActionF.class);
        this.checkClasses.add(BedNukeA.class);
        this.checkClasses.add(InvalidActionE.class);
        //this.checkClasses.add(KillAuraA.class);
        this.checkClasses.add(BadPacketsA.class);
        this.checkClasses.add(NoFallA.class);
    }

    public void loadToPlayer(PlayerData playerData) {
        List<Check> playerChecks = new ArrayList<>();
        for (Class<? extends Check> clazz : checkClasses) {
            try {
                Check check = clazz.getDeclaredConstructor().newInstance();
                check.setPlayerData(playerData);
                check.setupTimers(playerData);
                playerChecks.add(check);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        playerData.getChecks().addAll(playerChecks);
    }
}