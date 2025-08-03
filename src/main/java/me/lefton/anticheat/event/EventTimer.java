package me.lefton.anticheat.event;

import me.lefton.anticheat.data.PlayerData;
import lombok.Getter;

public class EventTimer {
    @Getter
    private int tick;
    private final int max;
    private final PlayerData data;

    public EventTimer(int max, PlayerData data) {
        this.tick = 0;
        this.max = max;
        this.data = data;
        this.reset();
    }

    public boolean hasNotPassed() {
        return (this.data.getTick() - this.tick) < this.max;
    }

    public boolean passed() {
        int maxTick = this.max + this.data.getTick();
        return (this.data.getTick() > maxTick && (this.data.getTick() - tick) > maxTick);
    }

    public boolean hasNotPassed(int ctick) {
        return (this.data.getTick() - this.tick) < ctick;
    }

    public boolean hasNotPassedNoPing(int ctick) {
        int maxTick = ctick;
        return (this.data.getTick() > maxTick && (this.data.getTick() - tick) < maxTick);
    }

    public boolean passed(int ctick) {
        int maxTick = ctick + this.data.getTick();
        return (this.data.getTick() > maxTick && (this.data.getTick() - tick) > maxTick);
    }

    public void reset() {
        this.tick = this.data.getTick();
    }

}
