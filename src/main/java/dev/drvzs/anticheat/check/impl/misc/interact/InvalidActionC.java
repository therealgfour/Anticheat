package dev.drvzs.anticheat.check.impl.misc.interact;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import dev.drvzs.anticheat.check.Check;
import dev.drvzs.anticheat.check.CheckData;
import dev.drvzs.anticheat.utils.PacketUtil;

@CheckData(name = "InvalidAction", type = "C", punishmentVL = 5, description = "Checks for block breaking without arm animation")
public class InvalidActionC extends Check {
    private boolean digging;
    private boolean hasSwung;

    @Override
    public void onPacket(PacketReceiveEvent event) {
        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_FLYING:
            case CLIENT_LOOK:
            case CLIENT_POSITION:
            case CLIENT_POSITION_LOOK: break;

            case CLIENT_ARM_ANIMATION:
                if (digging) {
                    hasSwung = true;
                }
                break;

            case CLIENT_BLOCK_DIG:
                WrapperPlayClientPlayerDigging digPacket = new WrapperPlayClientPlayerDigging(event);
                if (digPacket.getAction() == DiggingAction.START_DIGGING) {
                    digging = true;
                    hasSwung = false;
                } else if (digPacket.getAction() == DiggingAction.FINISHED_DIGGING) {
                    if (digging && !hasSwung) {
                        if (++buffer > 2) {
                            this.fail("no swing during block break. buffer=" + buffer);
                        }
                    } else {
                        buffer = Math.max(0, buffer - 1);
                    }
                    digging = false;
                    hasSwung = false;
                }
                break;
        }
    }
}