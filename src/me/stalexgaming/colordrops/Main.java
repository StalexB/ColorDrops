package me.stalexgaming.colordrops;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.stalexgaming.colordrops.commands.Commands;
import me.stalexgaming.colordrops.listeners.Listeners;
import me.stalexgaming.colordrops.managers.ArenaManager;
import me.stalexgaming.colordrops.managers.GameManager;
import me.stalexgaming.colordrops.utils.LocationUtil;
import me.stalexgaming.colordrops.utils.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class Main extends JavaPlugin {

    private static Main instance;

    public File locations = null;

    public int minimumPlayers;

    public List<Location> nexus = new ArrayList<>();
    public List<Location> blockspawnAreas = new ArrayList<>();
    public List<Location> redSpawnArea = new ArrayList<>();
    public List<Location> blueSpawnArea = new ArrayList<>();

    GameManager gameManager = GameManager.getInstance();
    ScoreboardUtil scoreboardUtil = ScoreboardUtil.getInstance();
    ArenaManager arenaManager = ArenaManager.getInstance();
    LocationUtil locationUtil = LocationUtil.getInstance();

    public void onEnable() {
        instance = this;

        locations = new File(this.getDataFolder(), "locations.yml");

        registerCommands();
        registerListeners();
        initializeBlocks();

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        minimumPlayers = getConfig().getInt("minimum-players");

        gameManager.countdown();
        scoreboardUtil.setScoreboards();
    }

    public WorldEditPlugin getWorldEditInstance() {
        Plugin p = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (p instanceof WorldEditPlugin) {
            return (WorldEditPlugin) p;
        }
        return null;
    }

    public void onDisable() {
        instance = null;
    }

    public static Main getInstance() {
        return instance;
    }

    private void registerCommands() {
        this.getCommand("colordrops").setExecutor(new Commands(this));
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new Listeners(this), this);
    }

    private void initializeBlocks() {
        if (arenaManager.isArenaReady()) {
            FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);

            for (String s : locationsFile.getStringList("arena.nexus")) {
                String[] data = s.split(":");
                for (String string : data) {
                    String[] location = string.split(" ");
                    Location minimum = locationUtil.deserializeLoc(location[0]);
                    Location maximum = locationUtil.deserializeLoc(location[1]);
                    for (double x = minimum.getX(); x <= maximum.getX(); x++) {
                        for (double y = minimum.getY(); y <= maximum.getY(); y++) {
                            for (double z = minimum.getZ(); z <= maximum.getZ(); z++) {
                                Location loc = new Location(minimum.getWorld(), x, y, z);
                                if (!nexus.contains(loc)) {
                                    nexus.add(loc);
                                }
                            }
                        }
                    }
                }
            }

            for(int i = 1; i < 9; i++){
                String[] data = locationsFile.getString("arena.blockspawnareas." + i).split(" ");

                Location minimum = locationUtil.deserializeLoc(data[0]);
                Location maximum = locationUtil.deserializeLoc(data[1]);
                for (double x = minimum.getX(); x <= maximum.getX(); x++) {
                    for (double y = minimum.getY(); y <= maximum.getY(); y++) {
                        for (double z = minimum.getZ(); z <= maximum.getZ(); z++) {
                            Location loc = new Location(minimum.getWorld(), x, y, z);
                            if (!blockspawnAreas.contains(loc)) {
                                blockspawnAreas.add(loc);
                            }
                        }
                    }
                }
            }

            String[] blue = locationsFile.getString("arena.spawns.blue").split(" ");

            Location minimum = locationUtil.deserializeLoc(blue[0]);
            Location maximum = locationUtil.deserializeLoc(blue[1]);
            for (double x = minimum.getX(); x <= maximum.getX(); x++) {
                for (double y = minimum.getY(); y <= maximum.getY(); y++) {
                    for (double z = minimum.getZ(); z <= maximum.getZ(); z++) {
                        Location loc = new Location(minimum.getWorld(), x, y, z);
                        if (!blueSpawnArea.contains(loc)) {
                            blueSpawnArea.add(loc);
                        }
                    }
                }
            }

            String[] red = locationsFile.getString("arena.spawns.red").split(" ");

            Location min = locationUtil.deserializeLoc(blue[0]);
            Location max = locationUtil.deserializeLoc(blue[1]);
            for (double x = min.getX(); x <= max.getX(); x++) {
                for (double y = min.getY(); y <= max.getY(); y++) {
                    for (double z = min.getZ(); z <= max.getZ(); z++) {
                        Location loc = new Location(min.getWorld(), x, y, z);
                        if (!redSpawnArea.contains(loc)) {
                            redSpawnArea.add(loc);
                        }
                    }
                }
            }
        }
    }

}
