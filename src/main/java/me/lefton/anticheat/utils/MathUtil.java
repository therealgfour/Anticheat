package me.lefton.anticheat.utils;

import com.google.common.util.concurrent.AtomicDouble;
import me.lefton.anticheat.utils.location.PlayerLocation;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

public class MathUtil {
    public static double getStandardDeviation(Collection<? extends Number> values) {
        double average = getAverage(values);

        AtomicDouble variance = new AtomicDouble(0D);

        values.forEach(delay -> variance.getAndAdd(Math.pow(delay.doubleValue() - average, 2D)));

        return Math.sqrt(variance.get() / values.size());
    }

    public static double getAverage(Collection<? extends Number> values) {
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .orElse(0D);
    }

    public static float getBaseSpeed_2(Player player) {
        return 0.23f + (getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public static int getPotionEffectLevel(Player player, PotionEffectType pet) {
        for (PotionEffect pe : player.getActivePotionEffects()) {
            if (pe.getType().getName().equalsIgnoreCase(pet.getName())) {
                return pe.getAmplifier() + 1;
            }
        }
        return 0;
    }

    public static float getMoveAngle(PlayerLocation from, PlayerLocation to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        float moveAngle = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90F); // have to subtract by 90 because minecraft does it

        return Math.abs(angle(moveAngle - to.getYaw()));
    }

    public static float angle(float value) {
        value %= 360F;

        if (value >= 180.0F)
            value -= 360.0F;

        if (value < -180.0F)
            value += 360.0F;

        return value;
    }
}
