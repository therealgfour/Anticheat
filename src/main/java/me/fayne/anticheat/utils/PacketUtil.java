package me.fayne.anticheat.utils;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import lombok.Getter;

@Getter
public class PacketUtil {

    public enum Packets {

        //Server packets
        SERVER_POSITION,
        SERVER_KEEPALIVE,
        SERVER_TRANSACTION,
        SERVER_VELOCITY,
        SERVER_RESPAWN,
        SERVER_OPEN_WINDOW,
        SERVER_ENTITY_METADATA,
        SERVER_ABILITES,
        SERVER_ITEM_SLOT,
        SERVER_ENTITY,
        SERVER_ENTITY_TELEPORT,
        SERVER_ENTITY_HEAD_ROTATION,
        SERVER_NAMED_ENTITY,
        SERVER_ENTITY_DESTORY,
        SERVER_REL_LOOK,
        SERVER_REL_POSITION,
        SERVER_REL_POSITION_LOOK,
        SERVER_PLAYER_SPAWN,
        SERVER_LIVING_SPAWN,
        SERVER_ATTACH,
        SERVER_DESTORY,
        SERVER_BLOCK_CHANGE,
        SERVER_EXPLODE,

        //Client packets
        CLIENT_TRANSACTION,
        CLIENT_KEEPALIVE,
        CLIENT_POSITION,
        CLIENT_FLYING,
        CLIENT_POSITION_LOOK,
        CLIENT_LOOK,
        CLIENT_BLOCK_PLACE,
        CLIENT_BLOCK_DIG,
        CLIENT_ARM_ANIMATION,
        CLIENT_USE_ENTITY,
        CLIENT_ENTITY_ACTION,
        CLIENT_COMMAND,
        CLIENT_CREATIVE_INVENTORY,
        CLIENT_CLOSE_WINDOW,
        CLIENT_HELD_ITEM_SLOT,
        CLIENT_CUSTOM_PAYLOAD,
        CLIENT_WINDOW_CLICK,
        CLIENT_TAB_COMPLETE,
        CLIENT_RESOUCE_PACK,
        CLIENT_SETTINGS,
        CLIENT_ABILITES,
        CLIENT_STEER,
        ENTITY_RIDE_MOVE,
        SERVER_BED,
        SERVER_REL_ENTITY_MOVE,
        SERVER_REL_TELEPORT,
        SERVER_EFFECT,
        SERVER_EFFECT_REMOVE,

        //Other
        NOT_DEFINDED
    }


    /**
     * NOTE: all packets have not been set
     * So you will have to manually add them if you would like to use them this way.
     */


    public static Packets toPacketReceive(PacketReceiveEvent event) {

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING) {
            return Packets.CLIENT_FLYING;
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION) {
            return Packets.CLIENT_POSITION;
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            return Packets.CLIENT_POSITION_LOOK;
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION) {
            return Packets.CLIENT_LOOK;
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            return Packets.CLIENT_BLOCK_DIG;
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            return Packets.CLIENT_BLOCK_PLACE;
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ABILITIES) {
            return Packets.CLIENT_ABILITES;
        }

        if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            return Packets.CLIENT_KEEPALIVE;
        }

        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            return Packets.CLIENT_TRANSACTION;
        }

        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            return Packets.CLIENT_USE_ENTITY;
        }

        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            return Packets.CLIENT_ARM_ANIMATION;
        }

        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            return Packets.CLIENT_ENTITY_ACTION;
        }


        if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            return Packets.CLIENT_CREATIVE_INVENTORY;
        }


        return Packets.NOT_DEFINDED;
    }

    public static Packets toPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            return Packets.SERVER_REL_ENTITY_MOVE;
        }

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            return Packets.SERVER_REL_POSITION_LOOK;
        }

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_ROTATION) {
            return Packets.SERVER_REL_LOOK;
        }

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            return Packets.SERVER_ENTITY_TELEPORT;
        }

        if (event.getPacketType() == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK) {
            return Packets.SERVER_POSITION;
        }

        if (event.getPacketType() == PacketType.Play.Server.ATTACH_ENTITY) {
            return Packets.SERVER_ATTACH;
        }

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
            return Packets.SERVER_VELOCITY;
        }

        if (event.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) {
            return Packets.SERVER_KEEPALIVE;
        }

        if (event.getPacketType() == PacketType.Play.Server.WINDOW_CONFIRMATION) {
            return Packets.SERVER_TRANSACTION;
        }

        if (event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            return Packets.SERVER_PLAYER_SPAWN;
        }

        if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            return Packets.SERVER_LIVING_SPAWN;
        }

        if (event.getPacketType() == PacketType.Play.Server.HELD_ITEM_CHANGE) {
            return Packets.SERVER_ITEM_SLOT;
        }

        return Packets.NOT_DEFINDED;
    }
}