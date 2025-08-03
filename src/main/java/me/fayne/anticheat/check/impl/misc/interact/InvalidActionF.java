package me.fayne.anticheat.check.impl.misc.interact;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import me.fayne.anticheat.check.Check;
import me.fayne.anticheat.check.CheckData;
import me.fayne.anticheat.utils.PacketUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CheckData(name = "InvalidAction", punishmentVL = 5, description = "checks for weird y movement", type = "F")
public class InvalidActionF extends Check {
    @Override
    public void onPacket(PacketReceiveEvent event) {
        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_FLYING:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK:
            case CLIENT_POSITION: {
                if (isCancellable() || getPlayerData().getLastTeleportTimer().hasNotPassed(20) ||
                        wasOnSlime(getPlayerData().getPlayer()) ||
                        getPlayerData().getVehicleTicks() > 0 || getPlayerData().getLastBlockPlaceTimer().hasNotPassed(3)
                        || getPlayerData().getLastBlockPlaceCancelTimer().hasNotPassed(3)
                        || getPlayerData().getTick() < 60 || getPlayerData().getVehicleTicks() > 0 || getPlayerData().getProcessorManager().getMovementProcessor().getVelocityTimer().hasNotPassed(10)) {
                    return;
                }

                if (getPlayerData().getProcessorManager().getMovementProcessor().getDeltaY() > 0.0 && getPlayerData().getProcessorManager().getMovementProcessor().getLastDeltaY() < 0.0) {
                    if (++buffer > 3.5) {
                        this.fail("wrong y movement");
                        buffer = 3.5;
                    }
                } else {
                    buffer -= Math.min(buffer, 0.125);
                }
                break;
            }
        }
    }

    private boolean wasOnSlime(Player player) {
        return player.getLocation().getBlock().getType() == Material.SLIME_BLOCK;
    }
}
