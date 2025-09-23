package me.pintoadmin.pintoManhunt;

import org.bukkit.*;
import org.bukkit.advancement.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.scheduler.*;

import java.util.*;

import static org.bukkit.Bukkit.getLogger;

public class Manhunt {
    private String gameUuid;
    private List<Player> hunters = new ArrayList<>();
    private List<Player> speedrunners = new ArrayList<>();
    private Map<Player, Location> speedrunnerLocations = new HashMap<>();
    private List<Player> allPlayers = new ArrayList<>();

    private List<Player> spectators = new ArrayList<>();

    private World world;
    private boolean START_pvp;
    private boolean START_daylight;
    private boolean START_weather;
    private World nether;
    private World end;

    private String gameState; // e.g., "waiting", "started", "ended"
    private String gameType; // TODO: Add game types

    private long timer; // in seconds

    Iterator<Advancement> ADVANCEMENTS = Bukkit.getServer().advancementIterator();

    public Manhunt(String gameUuid, String gameType, Player startingSpeedrunner) {
        this.gameUuid = gameUuid;
        this.gameType = gameType;
        this.speedrunnerLocations.put(startingSpeedrunner, startingSpeedrunner.getLocation());
        this.speedrunners.add(startingSpeedrunner);
        this.world = startingSpeedrunner.getWorld();
        this.hunters = new ArrayList<>();

        START_pvp = world.getPVP();
        START_daylight = world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
        START_weather = world.getGameRuleValue(GameRule.DO_WEATHER_CYCLE);

        checkWorlds();
        gameState = "waiting";
    }

    // Getters and Setters
    public String getGameUuid() {
        return gameUuid;
    }
    public List<Player> getHunters() {
        return hunters;
    }
    public List<Player> getSpeedrunners() {
        return speedrunners;
    }
    public Map<Player, Location> getSpeedrunnerLocations() {
        return speedrunnerLocations;
    }
    public List<Player> getSpectators() {
        return spectators;
    }
    public World getWorld() {
        return world;
    }
    public World getNether() {
        return nether;
    }
    public World getEnd() {
        return end;
    }
    public String getGameState() {
        return gameState;
    }
    public long getTimer() {
        return timer;
    }

    // Advanced Getters and Setters
    public List<Player> getAllPlayers() {
        allPlayers.clear();
        allPlayers.addAll(hunters);
        allPlayers.addAll(speedrunners);
        allPlayers.addAll(spectators);
        return allPlayers;
    }

    // Game functions
    public void addHunter(Player player) {
        if (!hunters.contains(player)) {
            hunters.add(player);
        }
    }
    public void removeHunter(Player player) {
        hunters.remove(player);
    }
    public void addSpeedrunner(Player player) {
        if (!speedrunners.contains(player)) {
            speedrunners.add(player);
        }
    }
    public void removeSpeedrunner(Player player) {
        speedrunners.remove(player);
        speedrunnerLocations.remove(player);
    }
    public void addSpectator(Player player) {
        if (!spectators.contains(player)) {
            spectators.add(player);
        }
    }
    public void removeSpectator(Player player) {
        spectators.remove(player);
    }
    public void updateSpeedrunnerLocation(Player player, Location location) {
        if (speedrunners.contains(player)) {
            speedrunnerLocations.put(player, location);
        }
    }
    public void updateSpeedrunnerLocations() {
        for (Player speedrunner : speedrunners) {
            speedrunnerLocations.put(speedrunner, speedrunner.getLocation());
        }
    }

    public void startGame() {
        this.gameState = "started";
        this.timer = 0; // reset timer

        getLogger().info("Game " + gameUuid + " started with " + hunters.size() + " hunters and " + speedrunners.size() + " speedrunners.");

        startTimer();
        world.setPVP(true);
        nether.setPVP(true);
        end.setPVP(true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);

        for(Player player : getHunters()){
            player.getInventory().addItem(new ItemStack(Material.COMPASS));
        }
        for(Player player : getSpeedrunners()){
            player.getAdvancementProgress(Bukkit.getAdvancement(NamespacedKey.fromString("minecraft:end/kill_dragon"))).revokeCriteria("impossible");
            revokeAllAdvancements(player);
        }
    }
    public void endGame(String winningTeam) {
        this.gameState = "ended";

        world.setPVP(false);
        nether.setPVP(false);
        end.setPVP(false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);

        if(winningTeam.equalsIgnoreCase("hunters")) {
            for (Player hunter : hunters) {
                hunter.sendMessage(ChatColor.GREEN + "Hunters win! Time: " + formatTime(timer));
                hunter.sendTitle(ChatColor.GREEN+"Hunters Win!", "Time: " + formatTime(timer), 10, 70, 20);
            }
            for (Player speedrunner : speedrunners) {
                speedrunner.sendMessage(ChatColor.RED + "Speedrunners lose! Time: " + formatTime(timer));
                speedrunner.sendTitle(ChatColor.RED +"Speedrunners Lose!", "Time: " + formatTime(timer), 10, 70, 20);
            }
        } else if(winningTeam.equalsIgnoreCase("speedrunners")) {
            for (Player speedrunner : speedrunners) {
                speedrunner.sendMessage(ChatColor.GREEN + "Speedrunners win! Time: " + formatTime(timer));
                speedrunner.sendTitle(ChatColor.GREEN+"Speedrunners Win!", "Time: " + formatTime(timer), 10, 70, 20);
            }
            for (Player hunter : hunters) {
                hunter.sendMessage(ChatColor.RED + "Hunters lose! Time: " + formatTime(timer));
                hunter.sendTitle(ChatColor.RED +"Hunters Lose!", "Time: " + formatTime(timer), 10, 70, 20);
            }
        }
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);

        for(Player player : getAllPlayers()){
            player.getInventory().clear();
            player.setCompassTarget(player.getWorld().getSpawnLocation());
            CompassMeta meta = (CompassMeta) new ItemStack(Material.COMPASS).getItemMeta();
            meta.setLodestone(player.getWorld().getSpawnLocation());
            meta.setLodestoneTracked(false);
            ItemStack compass = new ItemStack(Material.COMPASS);
            compass.setItemMeta(meta);
            player.teleport(player.getWorld().getSpawnLocation());
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                removeGame();
            }
        }.runTaskLater(PintoManhunt.getPlugin(PintoManhunt.class), 100L); // 5 seconds before removing the game
    }
    public void removeGame(){
        hunters.clear();
        speedrunners.clear();
        speedrunnerLocations.clear();
        spectators.clear();
        allPlayers.clear();
        gameState = "ended";
        PintoManhunt.getPlugin(PintoManhunt.class).activeGames.remove(this);

        world.setPVP(START_pvp);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, START_daylight);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, START_weather);
    }

    public void startTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if ("started".equals(gameState)) {
                    timer++;
                }
            }
        }.runTaskTimer(PintoManhunt.getPlugin(PintoManhunt.class), 0L, 20L); // runs every second
    }

    private void checkWorlds() {
        this.world = Bukkit.getWorld(world.getName());
        this.nether = Bukkit.getWorld(world.getName() + "_nether");
        this.end = Bukkit.getWorld(world.getName() + "_the_end");

        if (this.nether == null) {
            this.nether = Bukkit.createWorld(new WorldCreator(world.getName() + "_nether").environment(World.Environment.NETHER));
            Bukkit.broadcast(ChatColor.YELLOW + "Nether world created: " + nether.getName(), "pinto.manhunt.admin");
        }
        if (this.end == null) {
            this.end = Bukkit.createWorld(new WorldCreator(world.getName() + "_the_end").environment(World.Environment.THE_END));
            Bukkit.broadcast(ChatColor.YELLOW + "End world created: " + nether.getName(), "pinto.manhunt.admin");
        }
    }

    // Listeners or event handlers
    public void onPlayerDeath(PlayerDeathEvent event){
        Player deceased = event.getEntity();
        if (speedrunners.contains(deceased)) {
            speedrunners.remove(deceased);
            speedrunnerLocations.remove(deceased);
            if (speedrunners.isEmpty()) {
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        deceased.spigot().respawn();
                        endGame("hunters");
                    }
                }.runTaskLater(PintoManhunt.getPlugin(PintoManhunt.class), 5L); // 1/4 second delay before respawn and end of game
            }
        } else if(hunters.contains(deceased)){
            new BukkitRunnable(){
                @Override
                public void run() {
                    deceased.spigot().respawn();
                    deceased.getInventory().addItem(new ItemStack(Material.COMPASS));
                }
            }.runTaskLater(PintoManhunt.getPlugin(PintoManhunt.class), 5L); // 1/4 second delay before respawn
        }
    }
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event){
        Player player = event.getPlayer();
        if (speedrunners.contains(player) && event.getAdvancement().getKey().getKey().equals("end/kill_dragon")) {
            endGame("speedrunners");
        }
    }
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (hunters.contains(player) && event.getAction().toString().contains("RIGHT_CLICK")) {
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.COMPASS) {
                updateSpeedrunnerLocations();
                if (!speedrunnerLocations.isEmpty()) {
                    Player nearestSpeedrunner = null;
                    double nearestDistance = Double.MAX_VALUE;
                    for (Map.Entry<Player, Location> entry : speedrunnerLocations.entrySet()) {
                        Player speedrunner = entry.getKey();
                        Location location = entry.getValue();
                        double distance = player.getLocation().distance(new Location(player.getWorld(), location.getX(), location.getY(), location.getZ()));
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestSpeedrunner = speedrunner; // TODO: Select the speedrunner to point to
                        }
                    }
                    if (nearestSpeedrunner != null && nearestSpeedrunner.getWorld() == player.getWorld()) {
                        player.setCompassTarget(nearestSpeedrunner.getLocation());
                        CompassMeta meta = (CompassMeta) item.getItemMeta();
                        meta.setLodestone(nearestSpeedrunner.getLocation());
                        meta.setLodestoneTracked(false);
                        item.setItemMeta(meta);
                        player.sendMessage(ChatColor.GREEN + "Compass updated to point to " + nearestSpeedrunner.getName());
                    } else {
                        player.sendMessage(ChatColor.RED + "No speedrunner locations available in this dimension.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "No speedrunner locations available.");
                }
            }
        }
    }

    // Advanced methods
    private String formatTime(long timer) {
        long hours = timer / 3600;
        long minutes = (timer % 3600) / 60;
        long seconds = timer % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    public void revokeAllAdvancements(Player player) {
        while (ADVANCEMENTS.hasNext()) {
            AdvancementProgress progress = player.getAdvancementProgress(ADVANCEMENTS.next());
            for (String s : progress.getAwardedCriteria())
                progress.revokeCriteria(s);
        }
    }
}
