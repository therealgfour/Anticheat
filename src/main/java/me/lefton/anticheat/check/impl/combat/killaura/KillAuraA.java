// dummy check
/*package dev.drvzs.anticheat.check.impl.combat;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

@CheckData(name = "KillAura", punishmentVL = 5, description = "checks for attacking multiple entities")
public class KillAuraA extends Check {

    private int targets;
    private int lastEntity;

    @Override
    public void onPacket(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            if (wrapper.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                int currentTarget = wrapper.getEntityId();
                if (currentTarget != lastEntity) {
                    debug("increasing the targets");
                    ++this.targets;
                }

                if (this.targets > 1) {
                    this.fail("attacking multiple entities. attacks=" + this.targets);
                    this.targets = 0;
                }

                this.lastEntity = currentTarget;
            } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING) {
                debug("resetting the targets");
                this.targets = 0;
            }
        }
    }
}*/
