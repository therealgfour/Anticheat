package dev.drvzs.anticheat.check.impl.movement.sprint;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import dev.drvzs.anticheat.check.Check;
import dev.drvzs.anticheat.check.CheckData;
import dev.drvzs.anticheat.utils.MathUtil;
import dev.drvzs.anticheat.utils.PacketUtil;

@CheckData(name = "OmniSprint", punishmentVL = 5, description = "checks for omni sprint")
public class OmniSprintA extends Check {
    @Override
    public void onPacket(PacketReceiveEvent event) {
        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_FLYING:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK:
            case CLIENT_POSITION: {
                if (isCancellable() || getPlayerData().getTick() < 250 || getPlayerData().getLastTeleportTimer().hasNotPassed(10) || getPlayerData().getPlayer().isDead() ||
                        getPlayerData().getProcessorManager().getActionProcessor().getServerPositionTimer().hasNotPassed(10)) {
                    return;
                }

                double moveAngle = MathUtil.getMoveAngle(getPlayerData().getLastLocation(), getPlayerData().getCurrentLocation());
                double deltaXZ = getPlayerData().getProcessorManager().getMovementProcessor().getDeltaXZ();
                if (getPlayerData().getCurrentLocation() == null) {
                    break;
                }

                if (moveAngle <= 90.0f || deltaXZ <= MathUtil.getBaseSpeed_2(getPlayerData().getPlayer()) || !getPlayerData().getProcessorManager().getActionProcessor().isSprinting()) {
                    this.buffer = 0.0;
                    break;
                }
                final double buffer2 = buffer + 1.0;
                buffer = buffer2;
                if (buffer2 > 9.0) {
                    this.fail("omni sprint");
                    getPlayerData().getProcessorManager().getActionProcessor().setSprinting(false);
                    break;
                }
                break;
            }
        }
    }
}
