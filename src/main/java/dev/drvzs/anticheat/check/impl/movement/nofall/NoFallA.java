package dev.drvzs.anticheat.check.impl.movement.nofall;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import dev.drvzs.anticheat.check.Check;
import dev.drvzs.anticheat.check.CheckData;
import dev.drvzs.anticheat.process.processors.MovementProcessor;
import dev.drvzs.anticheat.utils.PacketUtil;

@CheckData(name = "NoFall", description = "checks for ground spoof", punishmentVL = 5)
/**
 * This check is based on phase detection patterns I saw in other anticheats.
 * I thought I could turn it into a no fall check by doing some small other calculations, and it works really well
 */
public class NoFallA extends Check {

    @Override
    public void onPacket(PacketReceiveEvent event) {
        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_POSITION:
            case CLIENT_POSITION_LOOK: {
                if (isCancellable() ||
                        getPlayerData().getLastTeleportTimer().hasNotPassed(20) ||
                        getPlayerData().getTick() < 60 ||
                        getPlayerData().getProcessorManager().getMovementProcessor().getVelocityTimer().hasNotPassed(20)) {
                    return;
                }

                MovementProcessor movement = getPlayerData().getProcessorManager().getMovementProcessor();

                if (movement.getAirTicks() <= 6 && !movement.isOnGround()) {
                    if ((movement.getDeltaY() < -0.7 && movement.getLastDeltaY() < 0.42) || (movement.getDeltaY() > 0.7 && movement.getLastDeltaY() < 0.42)) {

                        if (++buffer > 5) {
                            this.fail("deltaY=" + movement.getDeltaY() + ", lastDeltaY=" + movement.getLastDeltaY() + ", airTicks=" + movement.getAirTicks());
                            buffer = 0;
                        }
                    } else {
                        buffer = Math.max(0, buffer - 0.5);
                    }
                }
                break;
            }
        }
    }
}