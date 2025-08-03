package me.fayne.anticheat.check.impl.combat.autoclicker;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import me.fayne.anticheat.check.Check;
import me.fayne.anticheat.check.CheckData;
import me.fayne.anticheat.utils.MathUtil;
import me.fayne.anticheat.utils.PacketUtil;

import java.util.ArrayList;
import java.util.List;

@CheckData(name = "AutoClicker", punishmentVL = 5, description = "checks if player is clicking consistently")
public class AutoClickerA extends Check {

    private int teleportTicks;
    private final List<Integer> delays = new ArrayList<>();

    @Override
    public void onPacket(PacketReceiveEvent event) {
        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_FLYING:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK:
            case CLIENT_POSITION: {
                if (isCancellable() || getPlayerData().getTick() < 60
                        || getPlayerData().getLastBlockPlaceTimer().hasNotPassed(20)
                        || getPlayerData().getProcessorManager().getActionProcessor().getLastBlockDigTimer().hasNotPassed(20)
                        || getPlayerData().getLastBlockPlaceCancelTimer().hasNotPassed(20)) {
                    teleportTicks = 20;
                    return;
                }
                teleportTicks++;

                break;
            }

            case CLIENT_ARM_ANIMATION: {
                if (teleportTicks < 10) {
                    delays.add(teleportTicks);

                    if (delays.size() == 75) {
                        double std = MathUtil.getStandardDeviation(delays);

                        if (std < 0.3) {
                            if (buffer++ > 2) {
                                this.fail("Clicking too consistently, std=" + std);
                            }
                        } else {
                            buffer -= Math.min(buffer, 0.125);
                        }

                        delays.clear();
                    }
                }
                teleportTicks = 0;
                break;
            }
        }
    }
}