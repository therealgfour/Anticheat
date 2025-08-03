package me.lefton.anticheat.utils;

import org.bukkit.ChatColor;

public class ChatUtils {
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
