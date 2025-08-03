package me.lefton.anticheat.packet;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import me.lefton.anticheat.Anticheat;
import me.lefton.anticheat.data.PlayerData;
import me.lefton.anticheat.utils.ChatUtils;
import me.lefton.anticheat.utils.PacketUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketManager extends PacketListenerAbstract {
    @Getter
    private static final Map<UUID, String> playerBrands = new HashMap<>();

    // Clientbound
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {

        PacketUtil.toPacketReceive(event);

        Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        PlayerData playerData = Anticheat.getInstance().getPlayerDataManager().getUser(player);

        if (playerData == null) {
            return;
        }

        // Register clientbound packets for checks
        playerData.getChecks().forEach(check -> {
            if (check.getAlertData().isEnabled()) {
                check.onPacket(event);
            }
        });

        // run client processors
        playerData.getProcessorManager().getProcessors().forEach(processor -> processor.onPacket(event));

        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
            if (packet.getChannelName().equals("MC|Brand") || packet.getChannelName().equals("minecraft:brand")) {
                String brand = new String(Arrays.copyOfRange(packet.getData(), 1, packet.getData().length));
                brand = brand.replaceAll("[^a-zA-Z0-9_-]", "");
                brand = brand.replaceAll("Velocity", "");

                if (brand.contains("lunarclient"))
                    brand = "Lunar";

                String newBrand;
                try {
                    newBrand = brand.toUpperCase().charAt(0) + brand.substring(1);
                } catch (StringIndexOutOfBoundsException var17) {
                    newBrand = brand;
                }

                if (newBrand.length() > 30)
                    newBrand = "Unknown";

                playerBrands.put(playerData.getPlayer().getUniqueId(), newBrand);

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("anticheat.staff.alerts") &&
                            playerData.getSettings().isAlertsEnabled()) {
                        staff.sendMessage(ChatUtils.colorize(
                                "&7[&bVCA&7] &f" + playerData.getUserName() +
                                        " &7joined using &7[&e" + newBrand + "&7]"
                        ));
                    }
                }
            }
        }
    }


    // Serverbound
    @Override
    public void onPacketSend(PacketSendEvent event) {
        PacketUtil.toPacketSend(event);

        Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        PlayerData playerData = Anticheat.getInstance().getPlayerDataManager().getUser(player);

        if (playerData == null) {
            return;
        }

        // Register serverbound packets for checks
        playerData.getChecks().forEach(check -> {
            if (check.getAlertData().isEnabled()) {
                check.onPacket(event);
            }
        });

        // run server processors
        playerData.getProcessorManager().getProcessors().forEach(processor -> processor.onPacket(event));
    }
}
