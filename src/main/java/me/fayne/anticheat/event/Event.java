package me.fayne.anticheat.event;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import me.fayne.anticheat.data.PlayerData;

public class Event implements IEvent {

    @Override
    public void onPacket(PacketSendEvent event) {
        //
    }

    @Override
    public void onPacket(PacketReceiveEvent event) {
        //
    }

    @Override
    public void setupTimers(PlayerData playerData) {
        //
    }
}