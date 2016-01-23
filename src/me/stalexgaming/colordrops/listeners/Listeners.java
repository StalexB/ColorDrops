package me.stalexgaming.colordrops.listeners;

import me.stalexgaming.colordrops.Main;
import me.stalexgaming.colordrops.enums.Area;
import me.stalexgaming.colordrops.enums.GameState;
import me.stalexgaming.colordrops.enums.Team;
import me.stalexgaming.colordrops.events.AreaWalkEvent;
import me.stalexgaming.colordrops.managers.GameManager;
import me.stalexgaming.colordrops.managers.NexusManager;
import me.stalexgaming.colordrops.managers.TeamManager;
import me.stalexgaming.colordrops.player.SPlayer;
import me.stalexgaming.colordrops.utils.Color;
import me.stalexgaming.colordrops.utils.LocationUtil;
import me.stalexgaming.colordrops.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class Listeners implements Listener {

    Main plugin;

    public static boolean released = false;

    public Listeners(Main main){
        this.plugin = main;
    }

    LocationUtil locationUtil = LocationUtil.getInstance();
    TeamManager teamManager = TeamManager.getInstance();
    NexusManager nexusManager = NexusManager.getInstance();
    GameManager gameManager = GameManager.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);

        Player p = e.getPlayer();
        SPlayer player = new SPlayer(p);

        int blue = Team.BLUE.getPlayers().size();
        int red = Team.RED.getPlayers().size();

        p.teleport(locationUtil.deserializeLoc(locationsFile.getString("arena.lobby")));
        gameManager.setCarrying(p, 0);


        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        org.bukkit.scoreboard.Team r = sb.registerNewTeam("red");
        r.setPrefix(Color.np("&c"));
        org.bukkit.scoreboard.Team b = sb.registerNewTeam("blue");
        b.setPrefix(Color.np("&b"));
        for(Player online : Bukkit.getOnlinePlayers()){
            Team team = teamManager.getTeam(online);
            if(team == Team.BLUE){
                b.addEntry(online.getName());
            } else {
                r.addEntry(online.getName());
            }
        }

        p.setScoreboard(sb);

        e.setJoinMessage(null);

        if(blue <= red){
            player.setTeam(Team.BLUE);
        } else {
            player.setTeam(Team.RED);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        SPlayer player = new SPlayer(p);

        e.setQuitMessage(null);

        player.removePlayer();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location loc = new Location(e.getTo().getWorld(), (int) e.getTo().getX(), (int) e.getTo().getY(), (int) e.getTo().getZ());
        if(Main.getInstance().redSpawnArea.contains(loc)){
            Bukkit.getPluginManager().callEvent(new AreaWalkEvent(Area.RED_SPAWN, p));
        }
        if(Main.getInstance().blueSpawnArea.contains(loc)){
            Bukkit.getPluginManager().callEvent(new AreaWalkEvent(Area.BLUE_SPAWN, p));
        }
        if(Main.getInstance().blockspawnAreas.contains(loc)){
            Bukkit.getPluginManager().callEvent(new AreaWalkEvent(getArea(loc), p));
        }
        if ((int) e.getFrom().getX() != (int) e.getTo().getX() || (int) e.getFrom().getZ() != (int) e.getTo().getZ()) {
            if (!released) {
                if (GameState.getState() == GameState.INGAME) {
                    e.getPlayer().teleport(e.getFrom());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        Player p = e.getEntity();
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.SPECTATOR);

        new BukkitRunnable(){
            int i = 10;
            public void run() {
                i--;
                if(i > 0) {
                    Title title = new Title(Color.np("&6Respawning in &e" + String.valueOf(i)), "", 5, 10, 5);
                    title.sendToPlayer(p);
                } else {
                    this.cancel();
                    p.setGameMode(GameMode.SURVIVAL);

                    if(teamManager.getTeam(p) == Team.BLUE){
                        p.teleport(locationUtil.deserializeLoc(locationsFile.getString("arena.spawns.blue.1")));
                    } else if(teamManager.getTeam(p) == Team.RED){
                        p.teleport(locationUtil.deserializeLoc(locationsFile.getString("arena.spawns.red.1")));
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            if(GameState.getState() == GameState.ENDING || GameState.getState() == GameState.LOBBY){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAreaWalk(AreaWalkEvent e){
        if(GameState.getState() != GameState.ENDING) {
            Player p = e.getPlayer();
            Area a = e.getArea();

            if (a != null) {
                if(e.isBlockSpawn()){
                    int block = nexusManager.getColor(a);
                    gameManager.setCarrying(p, block);
                } else {
                    Team spawn = getTeam(a);
                    int carrying = gameManager.getCarrying(p);
                    if(spawn == teamManager.getTeam(p)){
                        if(carrying == nexusManager.getCurrentNexusColor()){
                            Bukkit.broadcastMessage(Color.np(spawn.getColor() + p.getName() + " &6has secured the color!"));
                            gameManager.addPoint(spawn);
                            gameManager.setCarrying(p, 0);
                            nexusManager.generateNewNexus();
                        }
                    }
                }
            }
        }
    }

    public Area getArea(Location loc){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        List<Area> areas = Area.getBlockSpawns();
        int i = 0;
        for(int t = 1; t < 9; t++){
            String[] data = locationsFile.getString("arena.blockspawnareas." + t).split(" ");

            Location minimum = locationUtil.deserializeLoc(data[0]);
            Location maximum = locationUtil.deserializeLoc(data[1]);
            for (double x = minimum.getX(); x <= maximum.getX(); x++) {
                for (double y = minimum.getY(); y <= maximum.getY(); y++) {
                    for (double z = minimum.getZ(); z <= maximum.getZ(); z++) {
                        Location location = new Location(minimum.getWorld(), x, y, z);
                        if(location.equals(loc)){
                            return areas.get(i);
                        }
                    }
                }
            }
            i++;
        }
        return null;
    }

    private Team getTeam(Area a){
        if(a == Area.RED_SPAWN){
            return Team.RED;
        } else {
            return Team.BLUE;
        }
    }

}
