package me.pintoadmin.pintoManhunt.listeners;

import me.pintoadmin.pintoManhunt.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;

import static org.bukkit.Bukkit.getLogger;

public class PlayerDeath implements Listener {
    private final PintoManhunt pintoManhunt;
    public PlayerDeath(PintoManhunt plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        pintoManhunt = plugin;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Manhunt game = pintoManhunt.getGameByPlayer(player.getUniqueId());
        if(game != null && game.getGameState().equalsIgnoreCase("started")){
            game.onPlayerDeath(event);
        }
    }
}
