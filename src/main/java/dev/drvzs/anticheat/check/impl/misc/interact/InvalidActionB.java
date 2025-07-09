package dev.drvzs.anticheat.check.impl.misc.interact;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import dev.drvzs.anticheat.check.Check;
import dev.drvzs.anticheat.check.CheckData;
import dev.drvzs.anticheat.utils.PacketUtil;

@CheckData(name = "InvalidAction", type = "B", description = "Checks for sprinting while using items", punishmentVL = 6)
public class InvalidActionB extends Check {

    private double buffer;

    @Override
    public void onPacket(PacketReceiveEvent event) {
        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_POSITION:
            case CLIENT_ABILITES: {
                if (getPlayerData().getLastTeleportTimer().hasNotPassed(20) || getPlayerData().getTick() < 60)
                    return;

                if (getPlayerData().getProcessorManager().getActionProcessor().isSprinting() && getPlayerData().getPlayer().isEating()) {
                    if (++buffer > 10) {
                        this.fail("buffer=" + buffer, "sprinting and eating at the same time");
                        buffer /= 2;
                    }
                } else {
                    buffer -= buffer > 0 ? 0.25 : 0;
                }
                break;
            }
        }
    }
}