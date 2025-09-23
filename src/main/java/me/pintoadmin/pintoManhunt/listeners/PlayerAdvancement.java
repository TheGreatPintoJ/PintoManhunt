package me.pintoadmin.pintoManhunt.listeners;

import me.pintoadmin.pintoManhunt.*;
import org.bukkit.event.*;

public class PlayerAdvancement implements Listener {
    private final PintoManhunt pintoManhunt;
    public PlayerAdvancement(PintoManhunt plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        pintoManhunt = plugin;
    }

    @EventHandler
    public void onPlayerAdvancement(org.bukkit.event.player.PlayerAdvancementDoneEvent event) {
        Manhunt game = pintoManhunt.getGameByPlayer(event.getPlayer().getUniqueId());
        if(game != null){
            game.onPlayerAdvancement(event);
        }
    }
}
