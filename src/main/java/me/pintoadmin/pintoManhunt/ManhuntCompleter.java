package me.pintoadmin.pintoManhunt;

import org.bukkit.command.*;

import java.util.*;

public class ManhuntCompleter implements TabCompleter {
    private final PintoManhunt pintoManhunt;

    public ManhuntCompleter(PintoManhunt plugin) {
        this.pintoManhunt = plugin;
        plugin.getCommand("manhunt").setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        java.util.List<String> completions = new java.util.ArrayList<>();
        if (args.length == 1) {
            completions.add("create");
            completions.add("remove");
            completions.add("join");
            completions.add("leave");
            completions.add("start");
            completions.add("status");
        } else if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("leave") && !args[0].equalsIgnoreCase("create")) {
                for (Manhunt game : pintoManhunt.activeGames) {
                    completions.add(game.getGameUuid());
                }
            } else if(args[0].equalsIgnoreCase("create")) {
                completions.add("normal");
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("join")) {
                completions.add("hunter");
                completions.add("speedrunner");
                completions.add("spectator");
            }
        }
        return completions.stream().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).toList();
    }
}
