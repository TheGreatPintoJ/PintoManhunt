package me.pintoadmin.pintoManhunt.listeners;

import me.pintoadmin.pintoManhunt.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class PlayerInteract implements Listener {
    private final PintoManhunt pintoManhunt;
    public PlayerInteract(PintoManhunt plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        pintoManhunt = plugin;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Manhunt game = pintoManhunt.getGameByPlayer(event.getPlayer().getUniqueId());
        if(game != null){
            game.onPlayerInteract(event);
        }
    }
}
