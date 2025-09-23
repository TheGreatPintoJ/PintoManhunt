package me.pintoadmin.pintoManhunt;

import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.util.*;

public class ManhuntCommand implements CommandExecutor {
    private final PintoManhunt pintoManhunt;

    public ManhuntCommand(PintoManhunt plugin) {
        plugin.getCommand("manhunt").setExecutor(this);
        pintoManhunt = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage("Usage: /manhunt <create|remove|join|leave|status|start> [args]");
            return true;
        }
        if(args[0].equalsIgnoreCase("status") && args.length == 1){
            sender.sendMessage("Usage: /manhunt status <UUID>");
            return true;
        }
        if(args[0].equalsIgnoreCase("create") && args.length == 1){
            sender.sendMessage("Usage: /manhunt create <type>");
            return true;
        } else if(args[0].equalsIgnoreCase("create")) {
            String type;
            if(args.length == 1){
                type = "normal";
            } else {
                type = args[1].toLowerCase();
            }
            pintoManhunt.activeGames.add(new Manhunt(UUID.randomUUID().toString(), type, player));
            sender.sendMessage("Game created with UUID: " + pintoManhunt.activeGames.get(pintoManhunt.activeGames.size() - 1).getGameUuid());
            return true;
        }
        String subcommand = args[0].toLowerCase();
        Manhunt game;
        if(args.length >= 2){
            game = pintoManhunt.getGameByUuid(args[1]);
        } else {
            game = pintoManhunt.getGameByPlayer(player.getUniqueId());
        }

        if(game == null){
            sender.sendMessage("Game with UUID " + args[1] + " not found.");
            return true;
        }
        switch (subcommand) {
            case "remove":
                sender.sendMessage("Removing game with UUID " + args[1]);
                pintoManhunt.activeGames.remove(game);
                game.removeGame();
                return true;
            case "status":
                sender.sendMessage("Game UUID: " + game.getGameUuid());
                sender.sendMessage("Game State: " + game.getGameState());
                sender.sendMessage("Hunters: " + game.getHunters().size());
                sender.sendMessage("Speedrunners: " + game.getSpeedrunners().size());
                sender.sendMessage("Spectators: " + game.getSpectators().size());
                sender.sendMessage("Timer: " + game.getTimer() + " seconds");
                return true;
            case "start":
                if(!game.getHunters().isEmpty() && !game.getSpeedrunners().isEmpty()){
                    game.startGame();
                    return true;
                }
                break;
            case "join":
                if(args.length < 3){
                    sender.sendMessage("Usage: /manhunt join <UUID> <speedrunner|hunter|spectator>");
                    return true;
                }
                if(game.getSpeedrunners().contains(player) || game.getHunters().contains(player) || game.getSpectators().contains(player)){
                    sender.sendMessage("You are already in this game.");
                    return true;
                }
                String role = args[2].toLowerCase();
                switch (role) {
                    case "speedrunner":
                        game.addSpeedrunner(player);
                        sender.sendMessage("You have joined the game as a Speedrunner.");
                        break;
                    case "hunter":
                        game.addHunter(player);
                        sender.sendMessage("You have joined the game as a Hunter.");
                        break;
                    case "spectator":
                        game.addSpectator(player);
                        sender.sendMessage("You have joined the game as a Spectator.");
                        break;
                    default:
                        sender.sendMessage("Unknown role. Please choose 'speedrunner', 'hunter', or 'spectator'.");
                        break;
                }
                break;
            case "leave":
                if(args.length != 2){
                    sender.sendMessage("Usage: /manhunt leave <UUID>");
                    return true;
                }
                if(!game.getSpeedrunners().contains(player) && !game.getHunters().contains(player) && !game.getSpectators().contains(player)){
                    sender.sendMessage("You are not in this game.");
                    return true;
                }
                game.removeSpeedrunner(player);
                game.removeHunter(player);
                game.removeSpectator(player);
                sender.sendMessage("You have left the game.");
                break;
            default:
                sender.sendMessage("Unknown subcommand. Usage: /manhunt <create|remove|join|leave|status|start> [args]");
                break;
        }
        return true;
    }
}
