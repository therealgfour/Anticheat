package me.lefton.anticheat.process;

import me.lefton.anticheat.data.PlayerData;
import me.lefton.anticheat.event.Event;
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
