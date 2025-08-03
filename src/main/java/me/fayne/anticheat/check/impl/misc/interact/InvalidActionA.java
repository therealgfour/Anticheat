package me.fayne.anticheat.check.impl.misc.interact;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import me.fayne.anticheat.check.Check;
import me.fayne.anticheat.check.CheckData;
import me.fayne.anticheat.utils.PacketUtil;

@CheckData(name = "InvalidAction", description = "Checks for blocking and sprinting", punishmentVL = 6)
public class InvalidActionA extends Check {

    @Override
    public void onPacket(PacketReceiveEvent event) {
        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_POSITION:
            case CLIENT_POSITION_LOOK:
                if (getPlayerData().getLastTeleportTimer().hasNotPassed(20) || getPlayerData().getTick() < 60)
                    return;

                if (getPlayerData().getProcessorManager().getActionProcessor().isSprinting() &&
                        getPlayerData().getPlayer().isBlocking()) {

                    if (++buffer > 10) {
                        this.fail("buffer=" + buffer + " sprinting and blocking at the same time");
                        buffer /= 2;
                    }
                } else {
                    buffer -= buffer > 0 ? 0.25 : 0;
                }
                break;
        }
    }
}