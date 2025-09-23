package me.pintoadmin.pintoManhunt;

import me.pintoadmin.pintoManhunt.listeners.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class PintoManhunt extends JavaPlugin {
    public static PintoManhunt instance;
    public List<Manhunt> activeGames = new ArrayList<>();

    public PintoManhunt() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        new ManhuntCommand(this);
        new ManhuntCompleter(this);
        new PlayerDeath(this);
        new PlayerAdvancement(this);
        new PlayerInteract(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Manhunt getGameByPlayer(UUID playerUuid) {
        for (Manhunt game : activeGames) {
            for (Player player : game.getHunters()) {
                if (player.getUniqueId().equals(playerUuid)) {
                    return game;
                }
            }
            for (Player player : game.getSpeedrunners()) {
                if (player.getUniqueId().equals(playerUuid)) {
                    return game;
                }
            }
            for (Player player : game.getSpectators()) {
                if (player.getUniqueId().equals(playerUuid)) {
                    return game;
                }
            }
        }
        return null; // Player not found in any active game
    }
    public Manhunt getGameByUuid(String gameUuid) {
        for (Manhunt game : activeGames) {
            if (game.getGameUuid().equals(gameUuid)) {
                return game;
            }
        }
        return null; // Game not found
    }
}
