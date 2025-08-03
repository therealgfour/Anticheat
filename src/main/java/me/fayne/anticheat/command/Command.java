package me.fayne.anticheat.command;

import me.fayne.anticheat.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Extend this class and annotate methods with @Param to create subcommands.
 */
public abstract class Command implements CommandExecutor {
    private static final String HEADER = "&8&m+----------------- &bVCA Commands &8&m-------------------+";
    private static final String FOOTER = "&8&m+-----------------------------------------------------+";
    private static final String COMMAND_FORMAT = "&7• &b/%s %s &8- &f%s";
    private static final String USAGE_FORMAT = "&bUsage: &f/%s %s %s";

    protected static final String NO_PERMISSION = "&cYou don't have permission to use this command.";
    protected static final String PLAYER_ONLY = "This command can only be used by players.";
    protected static final String PREFIX = "&7[&bVCA&7] ";


    private final Map<String, Method> commandMap = new HashMap<>();
    private final Map<Method, Param> annotationMap = new HashMap<>();
    private final String commandName;

    protected Command(String commandName) {
        this.commandName = commandName;
        this.registerCommands();
    }

    @Override
    public final boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("help") || subCommand.equals("?")) {
            sendHelp(sender);
            return true;
        }

        Method method = commandMap.get(subCommand);
        if (method == null) {
            method = commandMap.values().stream()
                    .filter(v -> {
                        Param param = annotationMap.get(v);
                        return Arrays.asList(param.aliases()).contains(subCommand);
                    })
                    .findFirst()
                    .orElse(null);

            if (method == null) {
                sendMessageWithPrefix(sender, "&cType &f/" + commandName + " help &cfor a list of commands.");
                return true;
            }
        }

        Param param = annotationMap.get(method);

        if (!param.permission().isEmpty() && !sender.hasPermission(param.permission())) {
            sendMessage(sender, NO_PERMISSION);
            return true;
        }

        if (args.length - 1 < param.minArgs()) {
            sendMessage(sender, buildUsageMessage(param));
            return true;
        }

        try {
            method.setAccessible(true);
            method.invoke(this, sender, Arrays.copyOfRange(args, 1, args.length));
        } catch (IllegalAccessException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            sendMessage(sender, ChatColor.RED + "An error occurred: " +
                    (cause != null ? cause.getMessage() : e.getMessage()));
            cause.printStackTrace();
        }

        return true;
    }

    private void registerCommands() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Param.class)) {
                Param param = method.getAnnotation(Param.class);
                commandMap.put(param.name().toLowerCase(), method);
                annotationMap.put(method, param);
            }
        }
    }

    protected void sendHelp(CommandSender sender) {
        List<String> help = new ArrayList<>();
        help.add(HEADER);
        help.add("");

        Map<String, List<Method>> commandGroups = new LinkedHashMap<>();
        commandGroups.put("General", new ArrayList<>());

        for (Method method : commandMap.values()) {
            Param param = annotationMap.get(method);
            if (param.permission().isEmpty() || sender.hasPermission(param.permission())) {
                String group = "General";
                if (param.permission().startsWith("anticheat.admin")) {
                    group = "Admin";
                } else if (param.permission().startsWith("anticheat.staff")) {
                    group = "Staff";
                }
                commandGroups.computeIfAbsent(group, k -> new ArrayList<>()).add(method);
            }
        }

        commandGroups.forEach((group, commands) -> {
            if (!commands.isEmpty()) {
                help.add("&b&l" + group + " Commands:");
                help.add("");

                commands.stream()
                        .distinct()
                        .sorted(Comparator.comparing(m -> annotationMap.get(m).name()))
                        .forEach(method -> {
                            Param param = annotationMap.get(method);
                            String usage = param.usage().isEmpty() ? "" : " &f" + param.usage();
                            String aliases = param.aliases().length > 0 ?
                                    " &7(&b" + String.join("&7, &b", param.aliases()) + "&7)" : "";

                            help.add(String.format(COMMAND_FORMAT,
                                    commandName,
                                    "&b" + param.name() + usage + aliases,
                                    param.description()
                            ));
                        });

                help.add("");
            }
        });

        help.add(FOOTER);
        help.forEach(line -> sendMessage(sender, line));
    }

    private String buildUsageMessage(Param param) {
        String usage = param.usage().isEmpty() ? "" : param.usage();
        return String.format(USAGE_FORMAT, commandName, param.name(), usage);
    }

    protected void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatUtils.colorize(message));
    }

    protected void sendMessageWithPrefix(CommandSender sender, String message) {
        sender.sendMessage(ChatUtils.colorize(PREFIX + message));
    }

    protected boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    protected Player getPlayer(CommandSender sender) {
        if (!isPlayer(sender)) {
            sender.sendMessage(PLAYER_ONLY);
            throw new IllegalStateException("This command can only be used by players");
        }
        return (Player) sender;
    }
}
