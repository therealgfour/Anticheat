package me.lefton.anticheat.process;

import me.lefton.anticheat.data.PlayerData;
import me.lefton.anticheat.process.processors.ActionProcessor;
import me.lefton.anticheat.process.processors.CombatProcessor;
import me.lefton.anticheat.process.processors.MovementProcessor;
import me.lefton.anticheat.process.processors.PotionProcessor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProcessorManager {
    private final List<Processor> processors = new ArrayList<>();

    private final MovementProcessor movementProcessor;
    private final ActionProcessor actionProcessor;
    private final PotionProcessor potionProcessor;
    private final CombatProcessor combatProcessor;

    public ProcessorManager(PlayerData playerData) {
        this.processors.add(this.movementProcessor = new MovementProcessor(playerData));
        this.processors.add(this.potionProcessor = new PotionProcessor(playerData));
        this.processors.add(this.actionProcessor = new ActionProcessor(playerData));
        this.processors.add(this.combatProcessor = new CombatProcessor(playerData));
    }
}