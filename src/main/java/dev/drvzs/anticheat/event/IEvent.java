package dev.drvzs.anticheat.event;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import dev.drvzs.anticheat.data.PlayerData;

public interface IEvent {

    void onPacket(PacketReceiveEvent event);

    void onPacket(PacketSendEvent event);

    void setupTimers(PlayerData playerData);
}