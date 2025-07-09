package dev.drvzs.anticheat.check.impl.movement.sprint;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import dev.drvzs.anticheat.check.Check;
import dev.drvzs.anticheat.check.CheckData;
import dev.drvzs.anticheat.utils.MathUtil;
import dev.drvzs.anticheat.utils.PacketUtil;

@CheckData(name = "ISeeYouVolcan", punishmentVL = 5, description = "checks for weird sprint behaviour like cancelling sprint.", type = "B")
public class ISeeYouVolcan extends Check {
    @Override
    public void onPacket(PacketReceiveEvent event) {
        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_FLYING:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK:
            case CLIENT_POSITION: {
                if (isCancellable() ||
                        getPlayerData().getTick() < 200 ||
                        getPlayerData().getLastBlockPlaceCancelTimer().hasNotPassed(20)
                        || getPlayerData().getVehicleTicks() > 0 ||
                        getPlayerData().getLastTeleportTimer().hasNotPassed(10) ||
                        getPlayerData().getPlayer().isDead() ||
                        getPlayerData().getProcessorManager().getActionProcessor().getServerPositionTimer().hasNotPassed(20) ||
                        getPlayerData().getPlayer().isInsideVehicle() || getPlayerData().getProcessorManager().getActionProcessor().isSneaking() || getPlayerData().getPlayer().isBlocking()) {
                    return;
                }

                final double deltaXZ = getPlayerData().getProcessorManager().getMovementProcessor().getDeltaXZ();
                if (getPlayerData().getProcessorManager().getActionProcessor().isSprinting() || getPlayerData().getProcessorManager().getActionProcessor().isLastSprinting() || deltaXZ <= MathUtil.getBaseSpeed_2(getPlayerData().getPlayer())) {
                    buffer = Math.max(0, buffer - 0.5);
                    break;
                }
                buffer += 1.0;
                if (buffer > 12.0) {
                    this.fail("weird sprint behaviour. possibly cancelling sprint or sprinting in all directions.");
                    buffer = 8.0;
                }
                break;
            }
        }
    }
}
