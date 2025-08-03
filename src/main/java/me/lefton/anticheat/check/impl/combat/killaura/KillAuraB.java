package me.lefton.anticheat.check.impl.combat.killaura;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import me.lefton.anticheat.check.Check;
import me.lefton.anticheat.check.CheckData;
import me.lefton.anticheat.utils.PacketUtil;

// Reference -> https://github.com/infiniteSM/Medusa/blob/master/Impl/src/main/java/com/gladurbad/medusa/check/impl/combat/killaura/KillAuraE.java
@CheckData(name = "KillAura", punishmentVL = 5, description = "checks for no swing aura", type = "B")
public class KillAuraB extends Check {
    @Override
    public void onPacket(PacketReceiveEvent event) {
        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_FLYING:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK:
            case CLIENT_POSITION: {
                booleanBuffer = false;
                break;
            }

            case CLIENT_ARM_ANIMATION: {
                booleanBuffer = true;
                break;
            }

            case CLIENT_USE_ENTITY: {
                WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
                if (wrapper.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                    if (isCancellable()) break;
                    if (++buffer > 10 && !booleanBuffer) {
                        this.fail("no swing aura");
                        buffer /= 2;
                    }
                } else {
                    buffer -= buffer > 0 ? 0.25 : 0;
                }
            }
        }
    }
}
