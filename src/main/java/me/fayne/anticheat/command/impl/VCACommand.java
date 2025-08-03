package me.fayne.anticheat.command.impl;

import me.fayne.anticheat.Anticheat;
import me.fayne.anticheat.command.Command;
import me.fayne.anticheat.command.Param;
import me.fayne.anticheat.data.PlayerData;
import me.fayne.anticheat.data.settings.PlayerLookup;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class VCACommand extends Command {

    public VCACommand() {
        super("vca");
    }

    @Param(name = "info", description = "Views info about the anticheat.", permission = "anticheat.admin.info", aliases = {"i"})
    public void onInfo(CommandSender sender, String[] args) {
        sendMessageWithPrefix(sender, "&fVersion: &b" + Anticheat.getInstance().getDescription().getVersion());
    }

    @Param(name = "alerts", description = "toggle alerts on/off", permission = "anticheat.admin.staff", aliases = {"a"})
    public void onAlerts(CommandSender sender, String[] args) {
        Player player = getPlayer(sender);

        PlayerData playerData = Anticheat.getInstance().getPlayerDataManager().getUser(player);
        if (playerData == null) {
            sendMessageWithPrefix(sender, "&cFailed to load your data. Please relog and try again.");
            return;
        }

        boolean alertState = !playerData.getSettings().isAlertsEnabled();
        playerData.getSettings().setAlertsEnabled(alertState);

        playerData.saveSettings().thenRun(() ->
                sendMessageWithPrefix(sender, "&bAlerts have been " + (alertState ? "&fenabled" : "&fdisabled") + "&b!")
        );
    }

    @Param(name = "lookup", description = "View player violation history", permission = "anticheat.admin.lookup", aliases = {"l"}, minArgs = 1)
    public void onLookup(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sendMessageWithPrefix(sender, "&cPlayer not found or offline.");
            return;
        }

        AtomicInteger page = new AtomicInteger(1);
        if (args.length > 1) {
            try {
                page.set(Math.max(1, Integer.parseInt(args[1])));
            } catch (NumberFormatException e) {
                sendMessageWithPrefix(sender, "&cInvalid page number.");
                return;
            }
        }

        Anticheat.getInstance().getMongoDB().getPlayerLookup(target.getUniqueId()).thenAccept(lookups -> {
            if (lookups.isEmpty()) {
                sendMessageWithPrefix(sender, "&cNo violation history found for &b" + target.getName() + "&c.");
                return;
            }

            lookups.sort((a, b) -> Long.compare(b.getLastTimestamp(), a.getLastTimestamp()));

            int perPage = 10;
            int totalPages = (int) Math.ceil((double) lookups.size() / perPage);
            page.set(Math.min(page.get(), totalPages));

            int start = (page.get() - 1) * perPage;
            int end = Math.min(start + perPage, lookups.size());

            sendMessage(sender, "");
            sendMessageWithPrefix(sender, "&bViolations for &f" + target.getName() + " &7(Page &f" + page + "&7/&f" + totalPages + "&7)");
            sendMessage(sender, "&7&m--------------------------------");

            for (int i = start; i < end; i++) {
                final PlayerLookup lookup = lookups.get(i);
                sendMessage(sender, String.format(
                        "&b%s &f%s &7x&f%d &8",
                        lookup.getCheckName(),
                        lookup.getCheckType(),
                        lookup.getCount()
                ));
            }
        });
    }

    @Param(name = "checks", description = "Displays the list of all the available checks", permission = "anticheat.admin.checks", aliases = {"c"})
    public void onChecksCommand(CommandSender sender, String[] args) {
        StringBuilder checksList = new StringBuilder("&bAvailable checks:\n");
        Anticheat.getInstance().getCheckManager().getCheckClasses().stream()
                .map(Class::getSimpleName)
                .forEach(check -> checksList.append("&7- &f").append(check).append("\n"));
        sendMessageWithPrefix(sender, checksList.toString().trim());
    }
}
