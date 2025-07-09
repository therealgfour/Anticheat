package dev.drvzs.anticheat.check.impl.misc.interact;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import dev.drvzs.anticheat.check.Check;
import dev.drvzs.anticheat.check.CheckData;

@CheckData(name = "InvalidAction", description = "checks for attacking and blocking", type = "E", punishmentVL = 5)
public class InvalidActionE extends Check {
    @Override
    public void onPacket(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            if (wrapper.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                if (getPlayerData().getPlayer().isBlocking()) {
                    if (buffer++ > 10)
                        this.fail("attacking and blocking at the same time" + " buffer=" + buffer);
                } else {
                    buffer -= buffer > 0 ? 0.25 : 0;
                }
            }
        }
    }
}

