package dev.drvzs.anticheat.process.processors;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import dev.drvzs.anticheat.data.PlayerData;
import dev.drvzs.anticheat.process.Processor;
import dev.drvzs.anticheat.process.ProcessorInfo;
import dev.drvzs.anticheat.utils.PacketUtil;
import lombok.Getter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
@ProcessorInfo(name = "Potion")
public class PotionProcessor extends Processor {

    private boolean hasSpeed, hasJump;
    private double speedAmplifier, jumpAmplifier;
    private int speedTicks, jumpTicks;

    public PotionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacket(PacketReceiveEvent event) {
        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_FLYING:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK:
            case CLIENT_POSITION: {
                if ((this.hasSpeed == getData().getPlayer().hasPotionEffect(PotionEffectType.SPEED))) {
                    this.speedAmplifier = this.getPotionEffectLevel(getData(), PotionEffectType.SPEED);
                    this.speedTicks += (this.speedTicks < 20 ? 1 : 0);
                } else {
                    this.speedTicks -= (this.speedTicks > 0 ? 1 : 0);
                }

                if ((this.hasJump == getData().getPlayer().hasPotionEffect(PotionEffectType.JUMP))) {
                    this.jumpAmplifier = this.getPotionEffectLevel(getData(), PotionEffectType.JUMP);
                    this.jumpTicks += (this.jumpTicks < 20 ? 1 : 0);
                } else {
                    this.jumpTicks -= (this.jumpTicks > 0 ? 1 : 0);
                }
            }
        }
    }

    private int getPotionEffectLevel(PlayerData data, PotionEffectType potionEffectType) {
        PotionEffect potionEffect = data.getPlayer().getActivePotionEffects()
                .stream().filter(effect -> effect.getType().equals(potionEffectType)).findAny().orElse(null);
        return (potionEffect != null ? potionEffect.getAmplifier() + 1 : 0);
    }
}
