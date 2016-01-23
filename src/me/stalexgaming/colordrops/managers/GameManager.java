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

import java.util.HashMap;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class GameManager {

    private static GameManager instance = new GameManager();

    public static GameManager getInstance(){
        return instance;
    }

    private HashMap<String, Integer> carrying = new HashMap<>();

    public int minecart = 0;

    ArenaManager arenaManager = ArenaManager.getInstance();
    TeamManager teamManager = TeamManager.getInstance();
    LocationUtil locationUtil = LocationUtil.getInstance();
    NexusManager nexusManager = NexusManager.getInstance();

    public void countdown(){
        if(arenaManager.isArenaReady()) {
            int minimumPlayers = Main.getInstance().minimumPlayers;
            new BukkitRunnable() {
                int counter = 10;

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
                        counter = 10;
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 20);
        }
    }

    public void startGame(){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        GameState.setState(GameState.INGAME);
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
            Main.getInstance().initializeBlocks();
        }

        new BukkitRunnable(){
            int counter = 10;
            public void run() {
                if(counter != 0) {
                    Bukkit.broadcastMessage(Color.np("&6ColorDrops starting in &e" + counter + "&6..."));
                    counter--;
                } else {
                    nexusManager.startNexus();
                    Listeners.released = true;
                    Bukkit.broadcastMessage(Color.np("&6The game has started!"));

                    Title title = new Title(Color.np("&6Released!"), "", 10, 30, 10);
                    title.sendToAllPlayers();

                    this.cancel();

                    startCounter();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public void startCounter(){
        new BukkitRunnable(){
            int i = 600;
            public void run() {
                i--;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public void addPoint(Team team){
        if(team == Team.BLUE){
            minecart--;
            checkWin();
        } else if(team == Team.RED){
            minecart++;
            checkWin();
        }
    }

    public void checkWin(){
        if(minecart <= -4){
            GameState.setState(GameState.ENDING);
            Title win = new Title(Color.np("&aYou won!"), "", 15, 40, 15);
            win.sendToTeam(Team.BLUE);
            Title lose = new Title(Color.np("&cYou lost!"), "", 15, 40, 15);
            win.sendToTeam(Team.RED);

            Bukkit.broadcastMessage(Color.np("&6ColorDrops was won by team &b&lBLUE&6!"));
        } else if(minecart >= 4){
            GameState.setState(GameState.ENDING);
            Title win = new Title(Color.np("&aYou won!"), "", 15, 40, 15);
            win.sendToTeam(Team.RED);
            Title lose = new Title(Color.np("&cYou lost!"), "", 15, 40, 15);
            win.sendToTeam(Team.BLUE);

            Bukkit.broadcastMessage(Color.np("&6ColorDrops was won by team &c&lRED&6!"));
        }
    }

    public int getMinecart(){
        return minecart;
    }

    public void setCarrying(Player p, int i){
        carrying.put(p.getName(), i);
    }

    public int getCarrying(Player p){
        if(carrying.containsKey(p.getName())){
            return carrying.get(p.getName());
        }
        return 0;
    }

}
