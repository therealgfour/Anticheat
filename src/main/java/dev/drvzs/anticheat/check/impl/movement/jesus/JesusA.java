package dev.drvzs.anticheat.check.impl.movement.jesus;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import dev.drvzs.anticheat.check.Check;
import dev.drvzs.anticheat.check.CheckData;
import dev.drvzs.anticheat.process.processors.MovementProcessor;
import dev.drvzs.anticheat.utils.PacketUtil;

@CheckData(name = "Jesus", punishmentVL = 5, description = "checks for speeding")
public class JesusA extends Check {
    @Override
    public void onPacket(PacketReceiveEvent event) {
        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_FLYING:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK:
            case CLIENT_POSITION: {
                if (isCancellable() || getPlayerData().getLastTeleportTimer().hasNotPassed(20) || getPlayerData().getVehicleTicks() > 0) {
                    buffer = 0;
                    return;
                }

                MovementProcessor movement = getPlayerData().getProcessorManager().getMovementProcessor();
                if (movement.isInLiquid() || movement.isWasInLiquid()) { // crazy english "isWasInLiquid" :P but guess what i won't rename it
                    double deltaXZ = movement.getDeltaXZ();
                    double max = 0.36;
                    if (movement.getVelocityTimer().hasNotPassed(20)) max += movement.getVelH();

                    if (deltaXZ == 342 || deltaXZ > 342)
                        return; // When the water force tries to push you back and you resist it to move forward, constant motion changes occur and hit these values, which can cause false flags

                    if (deltaXZ > max) {
                        if (buffer++ > 4) this.fail("speeding in water. deltaXZ=" + deltaXZ + ", maxXZ=" + max);
                    } else buffer = Math.max(0, buffer - 0.04);
                }
                break;
            }
        }
    }
}
