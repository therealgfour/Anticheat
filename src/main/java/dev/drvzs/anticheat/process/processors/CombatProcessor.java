package dev.drvzs.anticheat.process.processors;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import dev.drvzs.anticheat.data.PlayerData;
import dev.drvzs.anticheat.process.Processor;
import dev.drvzs.anticheat.process.ProcessorInfo;
import lombok.Getter;
import org.bukkit.entity.Entity;

@Getter
@ProcessorInfo(name = "Combat")
public class CombatProcessor extends Processor {

    private Entity lastAttackedEntity, lastLastAttackedEntity;

    public CombatProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacket(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            if (wrapper.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                lastLastAttackedEntity = lastAttackedEntity;
                lastAttackedEntity = event.getPlayer();
            }
        }
    }
}
