package me.fayne.anticheat.process;

import me.fayne.anticheat.data.PlayerData;
import me.fayne.anticheat.event.Event;
import lombok.Getter;

@Getter
public class Processor extends Event {

    private final String name;


    private final PlayerData data;

    public Processor(final PlayerData data) {
        this.name = getClass().getAnnotation(ProcessorInfo.class).name();
        this.data = data;
        this.setupTimers(data);
    }
}
