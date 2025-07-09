package dev.drvzs.anticheat.process.processors;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
import dev.drvzs.anticheat.data.PlayerData;
import dev.drvzs.anticheat.event.EventTimer;
import dev.drvzs.anticheat.process.Processor;
import dev.drvzs.anticheat.process.ProcessorInfo;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;

@Getter
@Setter
@ProcessorInfo(name = "Action")
public class ActionProcessor extends Processor {

    private EventTimer lastBlockDigTimer, serverPositionTimer;

    private Entity target;
    private boolean sprinting, sneaking, lastSprinting, lastSneaking;
    private boolean placing;
    private int hits, heldItemSlot, lastHeldItemSlot;

    public ActionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacket(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            final WrapperPlayClientEntityAction wrapper = new WrapperPlayClientEntityAction(event);
            switch (wrapper.getAction()) {
                case START_SPRINTING:
                    this.sprinting = true;
                    break;
                case STOP_SPRINTING:
                    this.sprinting = false;
                    break;
                case START_SNEAKING:
                    this.sneaking = true;
                    break;
                case STOP_SNEAKING:
                    this.sneaking = false;
                    break;
            }
        } else if (event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
            final WrapperPlayClientUseItem wrapper = new WrapperPlayClientUseItem(event);
            if (wrapper.getHand() == InteractionHand.MAIN_HAND) {
                ++this.hits;

                net.minecraft.server.v1_8_R3.Entity target = ((CraftWorld) getData().getPlayer().getWorld())
                        .getHandle().a(event.getUser().getEntityId());

                if (target != null) {
                    this.target = target.getBukkitEntity();
                } else {
                    this.target = null;
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            this.placing = true;
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING) {
            this.lastSneaking = this.sneaking;
            this.lastSprinting = this.sprinting;

            this.placing = false;

            this.hits = 0;
        } else if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            this.lastHeldItemSlot = this.heldItemSlot;
            this.heldItemSlot = new com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange(event).getSlot();
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            lastBlockDigTimer.reset();
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION) {
            this.serverPositionTimer.reset();
        }
    }

    @Override
    public void setupTimers(PlayerData playerData) {
        this.lastBlockDigTimer = new EventTimer(20, playerData);
        this.serverPositionTimer = new EventTimer(20, playerData);
    }
}
