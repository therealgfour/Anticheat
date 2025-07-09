package dev.drvzs.anticheat.action.alert.impl;

import dev.drvzs.anticheat.Anticheat;
import dev.drvzs.anticheat.action.alert.AlertData;
import dev.drvzs.anticheat.action.alert.IAlert;
import dev.drvzs.anticheat.data.PlayerData;
import dev.drvzs.anticheat.data.settings.PlayerLookup;
import dev.drvzs.anticheat.packet.PacketManager;
import dev.drvzs.anticheat.utils.ChatUtils;
import dev.drvzs.anticheat.utils.DiscordWebhook;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Alert implements IAlert {

    @Override
    public void handle(PlayerData playerData, AlertData alertData, String... data) {
        alertData.violations += 1.0;

        StringBuilder stringBuilder = new StringBuilder();

        for (String s : data) {
            stringBuilder.append(s).append(", ");
        }

        String checkType = alertData.checkType;

        if (alertData.experimental) {
            checkType += "*";
        }

        String alertMessage = "&7[&bVCA&7] &f" + playerData.getUserName()
                + " &7failed &f" + alertData.checkName
                + " &7(&f" + checkType + "&7)"
                + " &7[x&b" + alertData.violations + "&7/&b" + alertData.punishmentVL + "&7]";

        TextComponent textComponent = new TextComponent(ChatUtils.colorize(alertMessage));

        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(stringBuilder.toString().trim()).create()));

        PlayerLookup lookup = new PlayerLookup(
                playerData.getUuid(),
                alertData.checkName,
                checkType
        );
        Anticheat.getInstance().getMongoDB().savePlayerLookup(lookup);

        DiscordWebhook webhook = new DiscordWebhook("webhook");
        webhook.setUsername("VCA");
        webhook.setTts(false);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle(playerData.getUserName() + " failed " + alertData.checkName + " (" + checkType + ")")
                .setColor(Color.CYAN)
                .addField("VL", "**x" + alertData.violations + "** / **" + alertData.punishmentVL + "**", true)
                .addField("Details", "```" + String.join(", ", data) + "```", false)
                .addField("Info",
                        PacketManager.getPlayerBrands().getOrDefault(playerData.getUuid(), "Unknown") +
                                " | " + playerData.getPlayer().getPing() + "ms" +
                                " | TPS: " + String.format("%.2f", MinecraftServer.getServer().recentTps[0]),
                        false)
                .setFooter(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()), null)
        );


        Bukkit.getScheduler().runTaskAsynchronously(Anticheat.getInstance(), () -> {
            try {
                webhook.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Anticheat.getInstance().getPlayerDataManager().getUserMap().values().stream()
                .filter(staffData -> staffData.getPlayer().hasPermission("anticheat.staff.alerts") &&
                        staffData.getSettings().isAlertsEnabled())
                .forEach(staffData -> staffData.getPlayer().spigot().sendMessage(textComponent));
    }
}
