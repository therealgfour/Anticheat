package me.lefton.anticheat.check.impl.misc.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import me.lefton.anticheat.check.Check;
import me.lefton.anticheat.check.CheckData;
import me.lefton.anticheat.utils.PacketUtil;

@CheckData(
        name = "BadPackets",
        description = "impossible pitch check",
        punishmentVL = 3)
public class BadPacketsA extends Check {

    @Override
    public void onPacket(PacketReceiveEvent event) {

        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_LOOK:
            case CLIENT_POSITION:
            case CLIENT_POSITION_LOOK:
            case CLIENT_FLYING: {

                double pitch = Math.abs(getPlayerData().getCurrentLocation().getPitch());

                if (pitch > 90.0) {
                    this.fail("Impossible pitch rotation",
                            "pitch=" + pitch);
                }

                break;
            }
        }
    }
}