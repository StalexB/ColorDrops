package me.stalexgaming.colordrops.managers;

import me.stalexgaming.colordrops.Main;
import me.stalexgaming.colordrops.enums.GameState;
import me.stalexgaming.colordrops.enums.Team;
import me.stalexgaming.colordrops.listeners.Listeners;
import me.stalexgaming.colordrops.utils.Color;
import me.stalexgaming.colordrops.utils.LocationUtil;
import me.stalexgaming.colordrops.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class GameManager {

    private static GameManager instance = new GameManager();

    public static GameManager getInstance(){
        return instance;
    }

    ArenaManager arenaManager = ArenaManager.getInstance();
    TeamManager teamManager = TeamManager.getInstance();
    LocationUtil locationUtil = LocationUtil.getInstance();

    public void countdown(){
        if(arenaManager.isArenaReady()) {
            int minimumPlayers = Main.getInstance().minimumPlayers;
            new BukkitRunnable() {
                int counter = 45;

                public void run() {
                    if (Bukkit.getOnlinePlayers().size() >= minimumPlayers) {
                        if (GameState.getState() == GameState.LOBBY) {
                            if (counter % 5 == 0 && counter != 0) {
                                Bukkit.broadcastMessage(Color.np("&6Moving to arena in &e" + counter + "&6 seconds."));
                            }
                            if (counter == 0) startGame();
                            counter--;
                            if (Bukkit.getOnlinePlayers().size() == 10 && counter > 10) {
                                counter = 10;
                            }
                        } else {
                            this.cancel();
                        }
                    } else {
                        counter = 45;
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 20);
        }
    }

    public void startGame(){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        for(Team team : teamManager.getTeams()){
            int i = 1;
            for(String s : teamManager.getTeamPlayers(team)){
                if(Bukkit.getPlayer(s) != null){
                    Player t = Bukkit.getPlayer(s);

                    t.teleport(locationUtil.deserializeLoc(locationsFile.getString("arena.spawns." + team.toString().toLowerCase() + "." + i)));
                    i++;
                }
                continue;
            }
        }

        new BukkitRunnable(){
            int counter = 10;
            public void run() {
                if(counter != 0) {
                    Bukkit.broadcastMessage(Color.np("&6ColorDrops starting in &e" + counter + "&6..."));
                    counter--;
                } else {
                    Listeners.released = true;
                    Bukkit.broadcastMessage(Color.np("&6The game has started!"));

                    Title title = new Title(Color.np("&6Released!"), "", 10, 30, 10);
                    title.sendToAllPlayers();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

}
