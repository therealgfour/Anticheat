package me.fayne.anticheat.event;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import me.fayne.anticheat.data.PlayerData;

public interface IEvent {

    void onPacket(PacketReceiveEvent event);

    void onPacket(PacketSendEvent event);

    void setupTimers(PlayerData playerData);
}